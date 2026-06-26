# Comprehensive Guide to SlangForJava STEP2 - Lexer & Recursive Descent Parser

## Table of Contents
1. [Project Overview](#project-overview)
2. [What's New Since STEP1](#whats-new-since-step1)
3. [The Compiler Front-End Pipeline](#the-compiler-front-end-pipeline)
4. [Core Programming Concepts](#core-programming-concepts)
5. [Design Patterns Used](#design-patterns-used)
6. [File-by-File Analysis](#file-by-file-analysis)
7. [How the Code Works Together](#how-the-code-works-together)
8. [Operator Precedence Explained](#operator-precedence-explained)
9. [Learning Objectives](#learning-objectives)

---

## Project Overview

In **STEP1**, we built an expression evaluator that worked on **Abstract Syntax Trees (ASTs)** that we constructed *by hand* in Java code. To evaluate `5 * 10`, we had to manually write:

```java
Exp e = new BinaryExp(new NumericConstant(5), new NumericConstant(10), Operator.MUL);
```

**STEP2 removes that manual work.** Now we can feed in a plain string like `"-2*(-3+3)"` and the program will:
1. Break the string into **tokens** (numbers, operators, parentheses) — this is **lexing**.
2. Assemble those tokens into an **AST** automatically, respecting operator precedence — this is **parsing**.
3. Evaluate the AST (exactly like STEP1).

### What the Code Does
- Reads a mathematical expression as **text**
- Converts text into an AST automatically using a **Lexer** and a **Recursive Descent Parser**
- Honors operator precedence (`*` and `/` bind tighter than `+` and `-`)
- Handles parentheses and unary minus/plus
- Evaluates the resulting AST to produce a number

---

## What's New Since STEP1

STEP2 **reuses** all of STEP1's AST classes unchanged (just re-declared in `package STEP2;` so each step stays self-contained), and **adds a front end** on top.

| File | Status | Role |
|------|--------|------|
| `Exp.java` | Reused from STEP1 | Abstract expression node |
| `NumericConstant.java` | Reused from STEP1 | Leaf holding a number |
| `BinaryExp.java` | Reused from STEP1 | `+ - * /` node |
| `UnaryExp.java` | Reused from STEP1 | Unary `+ -` node |
| `Operator.java` | Reused from STEP1 | Operator enum |
| `RuntimeContext.java` | Reused from STEP1 | Execution context |
| `Token.java` | **NEW** | The kinds of tokens the lexer can produce |
| `Lexer.java` | **NEW** | Turns text → tokens |
| `RDParser.java` | **NEW** | Turns tokens → AST |
| `AbstractBuilder.java` | **NEW** | Base class for builders |
| `ExpressionBuilder.java` | **NEW** | Convenience wrapper: text → AST |
| `Program.java` | Changed | Now parses a string instead of hand-building the AST |

**Key idea:** The *evaluation* half of the compiler is identical to STEP1. Everything new in STEP2 is about *getting from text to an AST*.

---

## The Compiler Front-End Pipeline

A traditional compiler/interpreter processes source code in phases. STEP2 implements the first two:

```
  "-2*(-3+3)"   (raw text)
        |
        v
   +---------+
   |  LEXER  |   Lexical Analysis  (Lexer.java)
   +---------+
        |
        v
  [TOK_SUB, TOK_DOUBLE(2), TOK_MUL, TOK_OPAREN, ...]   (a stream of tokens)
        |
        v
   +----------+
   |  PARSER  |   Syntax Analysis  (RDParser.java)
   +----------+
        |
        v
        AST   (tree of Exp objects - same classes as STEP1)
        |
        v
   +-----------+
   | EVALUATE  |   (Exp.evaluate - unchanged from STEP1)
   +-----------+
        |
        v
      -0.0   (result)
```

- **Lexing (scanning):** Group raw characters into meaningful units called *tokens*. `"23"` becomes one `TOK_DOUBLE` token, not two separate digit characters.
- **Parsing:** Arrange tokens into a tree that captures *structure* and *precedence*.

---

## Core Programming Concepts

### 1. Tokens and Lexical Analysis
A **token** is the smallest meaningful unit of a language — like a word in a sentence. The lexer's job is to scan characters one at a time and emit tokens.

In this project, the token kinds are defined in the `Token` enum:
- `TOK_PLUS`, `TOK_SUB`, `TOK_MUL`, `TOK_DIV` — operators
- `TOK_OPAREN`, `TOK_CPAREN` — parentheses
- `TOK_DOUBLE` — a number (its value is fetched separately via `getNumber()`)
- `TOK_NULL` — signals end of input
- `ILLEGAL_TOKEN` — the initial/invalid state

**Why separate the value from the token kind?** A `TOK_DOUBLE` only tells you "a number appeared here." The actual numeric value is stored in the lexer's `number` field and retrieved with `getNumber()`. This keeps the enum simple (it only describes *categories*, not data).

### 2. Lookahead and Stateful Scanning
The `Lexer` keeps three pieces of state:
- `index` — where we currently are in the string
- `length` — total length (so we know when to stop)
- `number` — the most recently scanned numeric value

Each call to `getToken()` advances `index` past one token and returns it. The parser repeatedly calls `getToken()` to pull the next token when it's ready. This "give me the next token" model is the heart of how lexers and parsers cooperate.

### 3. Recursive Descent Parsing
**Recursive descent** is a top-down parsing technique where each grammar rule becomes a method, and the methods call each other (and themselves) to match the structure of the input.

Our grammar (informally):
```
expr   -> term   ( ('+' | '-') term   )*
term   -> factor ( ('*' | '/') factor )*
factor -> number
        | '(' expr ')'
        | ('+' | '-') factor
```

This maps directly to three methods:
- `expr()`  handles `+` and `-`
- `term()`  handles `*` and `/`
- `factor()` handles numbers, parentheses, and unary operators

The method-call *layering* is what encodes precedence (explained in detail later).

### 4. Inheritance for Code Reuse (`RDParser extends Lexer`)
The parser **extends** the lexer:
```java
public class RDParser extends Lexer {
```
Because of inheritance, the parser can call `getToken()` and `getNumber()` directly as if they were its own methods. The constructor passes the input string up to the lexer with `super(str)`:
```java
public RDParser(String str) {
    super(str);
}
```
This is a clean way to say "a parser *is a* lexer that also knows how to build trees."

### 5. The `super` Keyword
`super(str)` calls the **parent class constructor** (`Lexer(String expr)`). It must be the first statement in the subclass constructor. This is how `RDParser` initializes the lexing machinery it inherits.

### 6. Single-Token Lookahead (`currentToken`)
The parser holds one token of *lookahead* in the `currentToken` field. At any moment, `currentToken` is "the next token I haven't consumed yet." The pattern throughout the parser is:
1. Look at `currentToken` to decide what to do.
2. Consume it by calling `currentToken = getToken()` to advance.

This one-token-ahead style is called an **LL(1)** parser.

### 7. The Ternary Operator
The parser uses Java's ternary `? :` to pick an operator concisely:
```java
lToken == Token.TOK_PLUS ? Operator.PLUS : Operator.MINUS
```
Reads as: "if the saved token was `TOK_PLUS`, use `Operator.PLUS`, otherwise `Operator.MINUS`." It's shorthand for an `if/else` that produces a value.

### 8. Exceptions for Error Handling
When the lexer hits an unrecognized character, or the parser finds a missing `)`, it prints a message and throws:
```java
throw new RuntimeException();
```
We use `RuntimeException` (an *unchecked* exception) so callers aren't forced to declare `throws` everywhere. The `ExpressionBuilder` catches it and turns a parse failure into a `null` result.

### 9. Reusing the STEP1 AST
The parser's whole purpose is to produce the *same* objects STEP1 used:
```java
retValue = new BinaryExp(retValue, e1, Operator.MUL);
retValue = new NumericConstant(getNumber());
retValue = new UnaryExp(retValue, Operator.MINUS);
```
Once built, the tree is evaluated with the unchanged `evaluate()` method. This shows a powerful separation: **how a tree is built is independent of how it is used.**

---

## Design Patterns Used

### 1. Builder Pattern
`AbstractBuilder` and `ExpressionBuilder` wrap the messy details of constructing an object behind a simple call:
```java
ExpressionBuilder b = new ExpressionBuilder("-2*(-3+3)");
Exp e = b.getExpression();
```
The client doesn't need to know a `Lexer` and `RDParser` are involved — it just asks for an expression. `AbstractBuilder` is an empty base class that anticipates future builders (e.g., a `StatementBuilder` in later steps).

### 2. Interpreter Pattern (carried over from STEP1)
Each AST node still knows how to interpret itself via `evaluate()`. STEP2 just automates the *creation* of those nodes.

### 3. Composite Pattern (carried over from STEP1)
The AST is still a tree of uniformly-treated `Exp` nodes (leaves: `NumericConstant`; composites: `BinaryExp`, `UnaryExp`).

---

## File-by-File Analysis

### Token.java
```java
public enum Token {
    ILLEGAL_TOKEN(-1),
    TOK_PLUS(1),
    TOK_MUL(2),
    TOK_DIV(3),
    TOK_SUB(4),
    TOK_OPAREN(5),
    TOK_CPAREN(6),
    TOK_DOUBLE(7),
    TOK_NULL(8);

    private final int value;
    Token(int value) { this.value = value; }
    public int getValue() { return value; }
}
```

**Purpose:** Enumerates every kind of token the lexer can emit.

**Key Concepts:**
- Each constant describes a *category* of input, not a specific value.
- Follows STEP1's `Operator` style by attaching an `int` to each constant. The code only ever compares tokens with `==`, so the integers aren't strictly required — they're kept for stylistic consistency with the rest of the project.

**Design Decisions:**
- Why an enum? Type safety — the parser can only branch on valid token kinds.
- Why a separate `TOK_NULL`? It gives the parser a clean "end of input" signal instead of relying on a magic value.
- Why `TOK_DOUBLE` and not one constant per number? The *kind* is "a number"; the actual digits are stored separately in the lexer.

---

### Lexer.java
```java
public class Lexer {
    private String iExpr;
    private int index;
    private int length;
    private double number;

    public Lexer(String expr) {
        iExpr = expr;
        length = iExpr.length();
        index = 0;
    }

    public Token getToken() { ... }
    public double getNumber() { return number; }
}
```

**Purpose:** Converts the raw input string into a sequence of tokens, one `getToken()` call at a time.

**Walkthrough of `getToken()`:**
1. **Skip whitespace** — advance `index` past spaces and tabs.
2. **Check for end of string** — if `index == length`, return `TOK_NULL`.
3. **Switch on the current character:**
   - `+ - * / ( )` → return the matching single-character token and advance by one.
   - A digit `0`–`9` → keep consuming digits, build up the number string, convert with `Double.parseDouble`, store it in `number`, and return `TOK_DOUBLE`.
   - Anything else → print an error and throw.

**Key Concepts:**
- **Stateful scanning:** `index` persists across calls, so each call picks up where the last left off.
- **Maximal munch:** the digit loop grabs *all* consecutive digits, so `"123"` becomes a single number, not three tokens.
- `Character.isDigit(...)` is a tidy way to test for `0`–`9`.

**Design Decisions:**
- Why store the value in `number` instead of returning it? `getToken()` must return a `Token` (the kind). Returning the numeric value separately via `getNumber()` keeps a single, uniform return type.
- Why only integers (no decimals)? Like the original, the lexer only scans digit characters. Numbers are still stored as `double`, so adding decimal support later is a small change to this one loop.

---

### RDParser.java
```java
public class RDParser extends Lexer {
    private Token currentToken;

    public RDParser(String str) { super(str); }

    public Exp callExpr() { currentToken = getToken(); return expr(); }
    public Exp expr()    { ... }   // + and -
    public Exp term()    { ... }   // * and /
    public Exp factor()  { ... }   // numbers, parens, unary
}
```

**Purpose:** Consumes the token stream from the lexer and builds an AST.

**`callExpr()` — the entry point:**
- Primes the parser by reading the first token into `currentToken`.
- Delegates to `expr()`, the lowest-precedence rule.

**`expr()` — addition and subtraction:**
1. Parse a `term()` first (so multiplication is handled before addition).
2. While the current token is `+` or `-`: remember it, advance, parse another expression, and wrap both sides in a `BinaryExp`.

**`term()` — multiplication and division:**
- Same shape as `expr()`, but for `*` and `/`, and it calls `factor()` for its operands.

**`factor()` — the atoms:**
- A number → make a `NumericConstant` using `getNumber()`.
- An open paren `(` → recursively parse a full `expr()`, then require a closing `)` (error if missing).
- A leading `+` or `-` → parse another `factor()` and wrap it in a `UnaryExp`.
- Anything else → "Illegal Token" error.

**Key Concepts:**
- **Mutual recursion:** `expr → term → factor → expr` (via parentheses) is how arbitrarily nested expressions are handled.
- **Lookahead consumption:** every branch follows "inspect `currentToken`, then `currentToken = getToken()` to move on."

---

### AbstractBuilder.java
```java
public class AbstractBuilder {
}
```

**Purpose:** A (currently empty) base class for all builders.

**Why include it now?** Forward-thinking design. Later steps add more builders (for statements, programs, etc.); giving them a common base keeps the architecture consistent. It mirrors how STEP1 introduced an empty `RuntimeContext` ahead of need.

---

### ExpressionBuilder.java
```java
public class ExpressionBuilder extends AbstractBuilder {
    public String exprString;

    public ExpressionBuilder(String expr) { exprString = expr; }

    public Exp getExpression() {
        try {
            RDParser p = new RDParser(exprString);
            return p.callExpr();
        } catch (Exception e) {
            return null;
        }
    }
}
```

**Purpose:** A one-stop wrapper that hides the lexer/parser wiring behind a single method.

**Key Concepts:**
- **Encapsulation of process:** the client just supplies a string and gets back an `Exp`.
- **Graceful failure:** if parsing throws, `getExpression()` returns `null` rather than crashing the program.

**Design Note:** Because errors collapse to `null`, the caller should check for `null` before evaluating. (`Program` here trusts the input is valid.)

---

### Program.java
```java
public class Program {
    public static void main(String[] args) {
        ExpressionBuilder b = new ExpressionBuilder("-2*(-3+3)");
        Exp e = b.getExpression();
        System.out.println(e.evaluate(null));

        try {
            System.in.read();
        } catch (Exception ex) {
        }
    }
}
```

**Purpose:** Demonstrates the full pipeline on a real string.

**Contrast with STEP1:** In STEP1, `main` hand-built the AST node by node. Here it just hands a *string* to the builder — the lexer and parser do the construction. This is the central payoff of STEP2.

**Execution for `"-2*(-3+3)"`:**
1. Build → parse into an AST equivalent to `(-2) * ((-3) + 3)`.
2. Evaluate → `-2 * 0 = -0.0`.
3. Print → `-0.0`.
4. Wait for a keypress (keeps the console open on Windows).

---

## How the Code Works Together

### End-to-end flow for `"-2*(-3+3)"`

```
main()
  |
  v
new ExpressionBuilder("-2*(-3+3)")
  |
  v
getExpression()
  |
  v
new RDParser(...) ---- extends ----> Lexer (holds the string)
  |
  v
callExpr()
  |  currentToken = getToken()   // first token: '-'
  v
expr() --> term() --> factor()
                         |  sees '-', so: unary minus
                         |  factor() again --> NumericConstant(2)
                         v
                       UnaryExp(2, MINUS)         // the "-2"
        term() sees '*', consumes it, parses another factor()
                         |  sees '(' --> recurse into expr()
                         |      expr() --> term() --> factor() = UnaryExp(3, MINUS)  // "-3"
                         |      expr() sees '+', parses NumericConstant(3)
                         |      builds BinaryExp(-3, 3, PLUS)
                         |  requires ')'  (present, ok)
                         v
        term() builds BinaryExp( UnaryExp(2,MINUS), BinaryExp(-3,3,PLUS), MUL )
  |
  v
AST returned to main, then evaluate(null)
  |
  v
-2 * (-3 + 3) = -2 * 0 = -0.0
```

### Resulting AST
```
        *
       / \
      -   +
      |  / \
      2 -   3
        |
        3
```
(The left `-` is unary minus on `2`; the right subtree is `(-3) + 3`.)

---

## Operator Precedence Explained

The single most important concept in STEP2 is **how method layering creates precedence**. The rule:

> The rule called **last** (deepest) binds **tightest**.

```
expr()    handles  + -     (called first  -> loosest binding)
  └─ term()   handles  * /   (called next)
        └─ factor() handles numbers, ( ), unary  (called last -> tightest)
```

### Why this works
Consider `2 + 3 * 4`:
1. `expr()` calls `term()` to get its left operand.
2. `term()` calls `factor()` → gets `2`. It then checks for `*` or `/` — but the next token is `+`, not `*`. So `term()` returns just `2`.
3. Back in `expr()`, the token is `+`. It consumes it and parses the right side, which goes through `term()` again.
4. This time `term()` gets `3`, sees `*`, consumes it, gets `4`, and builds `BinaryExp(3, 4, MUL)`.
5. `expr()` builds `BinaryExp(2, (3*4), PLUS)`.

Result tree:
```
    +
   / \
  2   *
     / \
    3   4
```
The `*` ended up *below* the `+`, so it evaluates first — exactly the precedence we want. **No precedence table is needed; the call hierarchy is the precedence.**

### Parentheses override precedence
In `factor()`, a `(` triggers a recursive call to `expr()`. That restarts the whole precedence ladder *inside* the parentheses, so `(2 + 3) * 4` forces the addition into its own subtree, evaluated before the multiplication.

### A note on associativity
This parser calls itself recursively on the right operand (`expr()` inside `expr()`), which makes the operators group to the **right**. For `+ - * /` that doesn't change the final numeric answer for the examples here, but it's worth knowing the tree leans right. Left-associative parsing is a common refinement in later/advanced versions.

---

## Learning Objectives

### What You'll Learn From This Code

1. **Lexical analysis** — how raw text is grouped into tokens, and why a lexer keeps scanning state between calls.
2. **Recursive descent parsing** — how to turn a grammar into mutually recursive methods.
3. **Operator precedence by structure** — how method layering (`expr → term → factor`) encodes precedence with no lookup tables.
4. **Single-token lookahead (LL(1))** — the "inspect `currentToken`, then advance" loop.
5. **Inheritance in practice** — `RDParser extends Lexer` and the `super(...)` constructor call.
6. **The Builder pattern** — hiding multi-object construction behind one method.
7. **Separation of concerns** — building an AST is fully decoupled from evaluating it (the STEP1 evaluator is reused untouched).

### How to Extend This Project

#### Support decimal numbers
In `Lexer.getToken()`, extend the digit loop to also accept a single `.`:
```java
while (index < length &&
       (Character.isDigit(iExpr.charAt(index)) || iExpr.charAt(index) == '.')) {
    str += iExpr.charAt(index);
    index++;
}
```

#### Add an exponent operator `^`
1. Add `TOK_POW` to `Token` and `POW` to `Operator`.
2. Lex `^` in the switch.
3. Add a new precedence level (e.g., a `power()` method called by `factor()`), and handle `POW` in `BinaryExp.evaluate()` with `Math.pow(...)`.

#### Report better errors
Instead of `throw new RuntimeException()`, throw a custom exception carrying the `index` where the error occurred, so messages can point at the offending character.

### Common Mistakes to Avoid
1. **Forgetting to advance** — every time you accept a token, call `currentToken = getToken()`, or the parser loops forever.
2. **Wrong precedence layering** — if `expr()` and `term()` are swapped, `*` and `+` precedence inverts.
3. **Not priming `currentToken`** — `callExpr()` must read the first token before any rule runs.
4. **Ignoring `null` from `getExpression()`** — a failed parse returns `null`; evaluating it throws `NullPointerException`.

### Testing Your Understanding

#### Exercise 1: Predict the output
```java
new ExpressionBuilder("2 + 3 * 4").getExpression().evaluate(null);
// Expected: 14.0  (not 20.0 - precedence!)
```

#### Exercise 2: Draw the AST for `(2 + 3) * 4`
```
    *
   / \
  +   4
 / \
2   3
// Expected result: 20.0
```

#### Exercise 3: Trace `-(-5)`
- `factor()` sees `-`, parses another `factor()`.
- Inner `factor()` sees `-`, parses `5`.
- Builds `UnaryExp(UnaryExp(5, MINUS), MINUS)` → `5.0`.

---

## Conclusion

STEP2 transforms the project from "evaluate a hand-built tree" into "**evaluate a tree built automatically from text.**" The new pieces — `Token`, `Lexer`, `RDParser`, and the builders — form the **front end** of a compiler, while STEP1's AST and `evaluate()` logic remain the unchanged **back end**.

The standout lesson is that **operator precedence falls out of the parser's structure**: by layering `expr() → term() → factor()`, tighter-binding operators naturally sink deeper into the tree and evaluate first. This same recursive-descent technique scales up to real programming languages — and the next steps build directly on it by adding statements, variables, and control flow.

### Next Steps in Learning
1. **STEP3** — add statements (`PRINT`, `PRINTLINE`) and separate "evaluate for a value" from "execute for an effect."
2. **STEP4** — add variables, a symbol table, and type checking.
3. **STEP5+** — generate real executables via code generation.

---

*This guide covers the concepts, code, and design decisions in SlangForJava STEP2. Read it alongside the STEP1 guide to see how a hand-built evaluator grows a real parser front end.*
