# Comprehensive Guide to SlangForJava STEP3 - Statements & Keywords

## Table of Contents
1. [Project Overview](#project-overview)
2. [What's New Since STEP2](#whats-new-since-step2)
3. [The Central Idea: Expressions vs Statements](#the-central-idea-expressions-vs-statements)
4. [The Extended Pipeline](#the-extended-pipeline)
5. [Core Programming Concepts](#core-programming-concepts)
6. [Design Patterns Used](#design-patterns-used)
7. [File-by-File Analysis](#file-by-file-analysis)
8. [How the Code Works Together](#how-the-code-works-together)
9. [The Grammar](#the-grammar)
10. [Learning Objectives](#learning-objectives)

---

## Project Overview

**STEP2** could take a string like `"-2*(-3+3)"` and evaluate it to a single number. But a real program isn't one expression — it's a **sequence of statements** that *do things*.

**STEP3 introduces statements.** Now the input is a small script:

```
PRINTLINE 2*10;
PRINTLINE 10;
PRINT 2*10;
```

and the program parses it into a **list of statements** and executes each one in order, producing console output. This is the first version that feels like it runs a *program* rather than solving a single sum.

### What the Code Does
- Recognizes the **keywords** `PRINT` and `PRINTLINE`
- Recognizes the statement terminator `;`
- Parses a whole **list of statements**
- **Executes** each statement for its side effect (printing to the console)
- Still parses and evaluates arithmetic expressions exactly like STEP2

---

## What's New Since STEP2

STEP3 keeps all of STEP2's expression machinery and adds a statement layer on top.

| File | Status | Role |
|------|--------|------|
| `Exp.java`, `NumericConstant.java`, `BinaryExp.java`, `UnaryExp.java` | Reused from STEP2 | Expression AST (unchanged) |
| `Operator.java`, `RuntimeContext.java` | Reused from STEP2 | Operators + context |
| `AbstractBuilder.java`, `ExpressionBuilder.java` | Reused from STEP2 | Builders |
| `Token.java` | **Changed** | Adds `TOK_PRINT`, `TOK_PRINTLN`, `TOK_SEMI`, `TOK_UNQUOTED_STRING` |
| `Lexer.java` | **Changed** | Scans keywords, `;`, and newlines |
| `ValueTable.java` | **NEW** | A keyword-table entry (keyword text → token) |
| `Stmt.java` | **NEW** | Abstract base for statements |
| `PrintStatement.java` | **NEW** | `PRINT <expr> ;` |
| `PrintLineStatement.java` | **NEW** | `PRINTLINE <expr> ;` |
| `RDParser.java` | **Changed** | Adds `parse()`, statement-list parsing |
| `Program.java` | **Changed** | Runs multi-statement scripts |

---

## The Central Idea: Expressions vs Statements

This is the most important concept in STEP3.

| | **Expression** (`Exp`) | **Statement** (`Stmt`) |
|---|---|---|
| Question it answers | "What is your **value**?" | "What **effect** do you perform?" |
| Method | `double evaluate(...)` | `boolean execute(...)` |
| Example | `2 * 10` → produces `20` | `PRINTLINE 2*10;` → prints `20` |
| Returns | a number | a `boolean` (success flag) |

> **You *evaluate* an expression for its value. You *execute* a statement for its effect.**

A statement usually *contains* an expression. `PrintLineStatement` holds an `Exp`; when executed, it first **evaluates** that expression (to get a number), then **prints** it. So statements sit one level above expressions and drive them.

---

## The Extended Pipeline

```
  "PRINTLINE 2*10;\r\nPRINT 10;\r\n"   (a script, many lines)
        |
        v
   +---------+
   |  LEXER  |   now also recognizes PRINT / PRINTLINE / ; / newlines
   +---------+
        |
        v
  [TOK_PRINTLN, TOK_DOUBLE, TOK_MUL, TOK_DOUBLE, TOK_SEMI, TOK_PRINT, ...]
        |
        v
   +----------+
   |  PARSER  |   parse() -> a LIST of statements
   +----------+
        |
        v
  ArrayList<Stmt> = [ PrintLineStatement(2*10), PrintStatement(10) ]
        |
        v
   +-----------+
   | EXECUTE   |   loop: for each Stmt -> s.execute(null)
   +-----------+
        |
        v
     console output
```

Compared to STEP2, the parser no longer returns a single `Exp`; it returns a **list of `Stmt` objects**, and `main` loops over them calling `execute()`.

---

## Core Programming Concepts

### 1. Keyword Recognition (the `default` branch of the lexer)
In STEP2, a letter was an error. In STEP3, when the lexer sees a letter it **scans the whole word**:

```java
if (Character.isLetter(c)) {
    String tem = "" + c;
    index++;
    while (index < lengthString &&
           (Character.isLetterOrDigit(exp.charAt(index)) || exp.charAt(index) == '_')) {
        tem += exp.charAt(index);
        index++;
    }
    tem = tem.toUpperCase();
    ...
}
```

It grabs letters, digits, and underscores, then upper-cases the result — so `print`, `Print`, and `PRINT` are all treated the same (the language is **case-insensitive** for keywords).

### 2. The Keyword Table (`ValueTable`)
Instead of hard-coding `if (word.equals("PRINT"))`, the lexer keeps a small table:

```java
val = new ValueTable[2];
val[0] = new ValueTable(Token.TOK_PRINT, "PRINT");
val[1] = new ValueTable(Token.TOK_PRINTLN, "PRINTLINE");
```

After scanning a word, it loops through the table looking for a match:

```java
for (int i = 0; i < val.length; i++) {
    if (val[i].value.compareTo(tem) == 0) {
        return val[i].tok;   // it's a keyword
    }
}
return Token.TOK_UNQUOTED_STRING; // otherwise it's an identifier
```

**Why a table?** Adding a new keyword later (e.g., `WHILE`) becomes a one-line table entry rather than another `if`. This is a small, data-driven design.

> `ValueTable` is a Java class here. In the original C# it was a `struct`. Java has no `struct`, so a plain class with public fields is the natural equivalent.

### 3. Statement Terminator and Newlines
Two more characters matter now:
- `;` becomes `TOK_SEMI` — it ends a statement.
- `\r` and `\n` (line breaks) are **skipped**. When the lexer sees them it just advances and restarts the scan:

```java
case '\r':
case '\n':
    index++;
    continue;   // restart getToken (skip the line break)
```

> The C# original used `goto re_start` here. Java has no `goto`, so the whole method body is wrapped in `while (true)` and `continue` jumps back to the top — identical behavior, structured control flow.

### 4. A Statement Class Hierarchy (polymorphism again)
`Stmt` is abstract with one method:

```java
public abstract class Stmt {
    public abstract boolean execute(RuntimeContext con);
}
```

`PrintStatement` and `PrintLineStatement` each override `execute()`. This is the same polymorphism idea used for `Exp` in STEP1 — the parser produces a mixed list of `Stmt` subtypes, and the `main` loop calls `execute()` on each without knowing or caring which concrete type it is.

### 5. Parsing a List (not just one thing)
The new entry point `parse()` returns an `ArrayList<Stmt>`:

```java
public ArrayList<Stmt> parse() {
    getNext();               // read the first token
    return statementList();
}

private ArrayList<Stmt> statementList() {
    ArrayList<Stmt> arr = new ArrayList<>();
    while (currentToken != Token.TOK_NULL) {
        Stmt temp = statement();
        if (temp != null) arr.add(temp);
    }
    return arr;
}
```

It keeps parsing statements until the token stream runs out (`TOK_NULL`). This "loop until end of input" pattern is how a parser handles a whole file.

> This port uses Java **generics** (`ArrayList<Stmt>`) where the C# original used an untyped `ArrayList`. Generics give compile-time type safety and remove the need for casts when reading items back out.

### 6. One Token of Lookahead + Remembering the Last (`getNext`)
STEP3 adds a helper that advances the current token while remembering the previous one:

```java
protected Token getNext() {
    lastToken = currentToken;
    currentToken = getToken();
    return currentToken;
}
```

`lastToken` isn't actually consumed yet (it's groundwork for later steps), but `getNext()` is used throughout statement parsing as the standard "consume and move on" step.

---

## Design Patterns Used

### 1. Interpreter Pattern (now at two levels)
STEP1/STEP2 interpreted **expressions** via `evaluate()`. STEP3 adds a second interpreter layer: **statements** interpreted via `execute()`. Statements interpret themselves by driving the expression interpreter underneath.

### 2. Composite Pattern (carried over)
The expression AST is still a composite tree. The statement list is a flat sequence, but each statement composes an expression subtree.

### 3. Table-Driven Design (new)
The `ValueTable` keyword table replaces branching logic with data. This is a lightweight version of how real lexers use lookup tables/maps for keywords.

---

## File-by-File Analysis

### Token.java
Adds four constants to STEP2's set:
- `TOK_PRINT` — the `PRINT` keyword
- `TOK_PRINTLN` — the `PRINTLINE` keyword
- `TOK_UNQUOTED_STRING` — a scanned word that isn't a keyword (an identifier)
- `TOK_SEMI` — the `;` terminator

Everything else is unchanged from STEP2.

---

### ValueTable.java
```java
public class ValueTable {
    public Token tok;     // Token id
    public String value;  // Token string

    public ValueTable(Token tok, String value) {
        this.tok = tok;
        this.value = value;
    }
}
```
**Purpose:** One row of the keyword table — pairs a keyword's text with the token it should produce. Simple data holder.

---

### Lexer.java
**What changed vs STEP2:**
- Constructor builds the keyword table (`PRINT`, `PRINTLINE`).
- `getToken()` is wrapped in `while (true)` so newlines can restart the scan.
- New cases: `\r`/`\n` (skip), `;` (→ `TOK_SEMI`).
- New `default` behavior: scan a word and either return its keyword token or `TOK_UNQUOTED_STRING`.

**Common bug to watch for:** if you forget the `case ';'` branch, every statement fails at its terminating `;` because the lexer can never produce `TOK_SEMI` — the `;` falls through to `default`, isn't a letter, and throws. (This is exactly the kind of "one missing case" error that's easy to introduce when typing the switch by hand.)

---

### Stmt.java
```java
public abstract class Stmt {
    public abstract boolean execute(RuntimeContext con);
}
```
**Purpose:** The base type for all statements. Returning `boolean` lets a statement report success/failure (always `true` for now).

---

### PrintStatement.java / PrintLineStatement.java
```java
public class PrintLineStatement extends Stmt {
    private Exp ex;
    public PrintLineStatement(Exp ex) { this.ex = ex; }

    @Override
    public boolean execute(RuntimeContext con) {
        double a = ex.evaluate(con);   // 1. evaluate the expression
        System.out.println(a);         // 2. perform the effect (print)
        return true;
    }
}
```
**Purpose:** Hold an expression and, on `execute()`, evaluate then print it.
- `PrintStatement` uses `System.out.print` (no newline).
- `PrintLineStatement` uses `System.out.println` (adds a newline).

The two classes are nearly identical — the only difference is `print` vs `println`. That difference *is* the feature.

---

### RDParser.java
**Unchanged from STEP2:** `callExpr()`, `expr()`, `term()`, `factor()` — the whole expression parser.

**New in STEP3:**
- `parse()` — reads the first token, then parses a statement list.
- `statementList()` — loops until `TOK_NULL`, collecting statements.
- `statement()` — switches on the current token to pick a statement kind (`TOK_PRINT` / `TOK_PRINTLN`); anything else is an "Invalid statement" error.
- `parsePrintStatement()` / `parsePrintLNStatement()` — each consumes the keyword, parses an expression, requires a `;`, and builds the matching statement object.

**Shape of a statement parse:**
```java
private Stmt parsePrintLNStatement() {
    getNext();                 // skip past the PRINTLINE keyword
    Exp a = expr();            // parse the expression
    if (currentToken != Token.TOK_SEMI) {
        throw new RuntimeException("; is expected");   // enforce the ';'
    }
    return new PrintLineStatement(a);
}
```

---

### Program.java
Builds a script string, parses it into a list, and executes each statement:
```java
RDParser p = new RDParser(a);
ArrayList<Stmt> arr = p.parse();
for (Stmt s : arr) {
    s.execute(null);
}
```
The `RuntimeContext` is still `null` — statements don't need it yet (variables arrive in STEP4).

---

## How the Code Works Together

### End-to-end for `PRINTLINE 2*10;`

```
parse()
  |  getNext() -> currentToken = TOK_PRINTLN
  v
statementList()  (loop while token != NULL)
  |
  v
statement()  -> sees TOK_PRINTLN -> parsePrintLNStatement()
  |     getNext()            // consume PRINTLINE, now at '2'
  |     expr()               // parses 2*10 into BinaryExp(2,10,MUL)
  |     check currentToken == TOK_SEMI  (the ';')  OK
  |     return PrintLineStatement( BinaryExp(2,10,MUL) )
  |  getNext()               // move past ';' to next statement
  v
... more statements ...
  |
  v
main: for each Stmt -> execute(null)
  |
  v
PrintLineStatement.execute:
     ex.evaluate(null) -> 20.0
     System.out.println(20.0)  ->  prints "20.0"
```

### The two levels of the tree
For `PRINTLINE 2*10;` the parser builds:
```
PrintLineStatement          <- statement level (execute for effect)
        |
        *                   <- expression level (evaluate for value)
       / \
      2   10
```
`execute()` walks the statement, which calls `evaluate()` on the expression subtree.

---

## The Grammar

STEP3's grammar (informal EBNF), with the new statement rules on top of STEP2's expression rules:

```
<stmtlist>      := { <statement> }+
<statement>     := <printstmt> | <printlinestmt>
<printstmt>     := PRINT     <expr> ;
<printlinestmt> := PRINTLINE <expr> ;

<expr>          := <term>   | <term>   {+|-} <expr>
<term>          := <factor> | <factor> {*|/} <term>
<factor>        := <number> | ( <expr> ) | {+|-} <factor>
```

Each rule maps to a method:
- `<stmtlist>` → `statementList()`
- `<statement>` → `statement()`
- `<printstmt>` / `<printlinestmt>` → `parsePrintStatement()` / `parsePrintLNStatement()`
- `<expr>`/`<term>`/`<factor>` → `expr()`/`term()`/`factor()` (from STEP2)

---

## Learning Objectives

### What You'll Learn From This Code
1. **The expression/statement distinction** — value vs effect, `evaluate()` vs `execute()`.
2. **Keyword lexing** — scanning identifiers and looking them up in a keyword table.
3. **Table-driven design** — using data (`ValueTable[]`) instead of branching to recognize keywords.
4. **Parsing a sequence** — looping until end-of-input to build a list of statements.
5. **A second polymorphic hierarchy** — `Stmt` mirrors `Exp`; the executor loop is type-agnostic.
6. **Structured control flow** — replacing `goto` with a `while(true) + continue` loop.

### How to Extend This Project

#### Add a new keyword statement (e.g., `PRINTTAB`)
1. Add `TOK_PRINTTAB` to `Token`.
2. Add a table row in the `Lexer` constructor:
   ```java
   val = new ValueTable[3];
   val[0] = new ValueTable(Token.TOK_PRINT, "PRINT");
   val[1] = new ValueTable(Token.TOK_PRINTLN, "PRINTLINE");
   val[2] = new ValueTable(Token.TOK_PRINTTAB, "PRINTTAB");
   ```
3. Create a `PrintTabStatement extends Stmt`.
4. Add a `case TOK_PRINTTAB:` in `statement()` and a `parsePrintTabStatement()`.

#### Make output match C# formatting
Java prints `20.0` where C# prints `20`. If you want integer-looking output, format the double before printing (e.g., strip a trailing `.0` when the value is a whole number).

### Common Mistakes to Avoid
1. **Forgetting `case ';'`** in the lexer → every statement throws at its terminator.
2. **Not skipping newlines** → `\r`/`\n` become illegal tokens between lines.
3. **Forgetting to `getNext()` after a statement** in `statement()` → the parser gets stuck re-reading the same token (infinite loop) or misreads the next statement.
4. **Not priming the first token** (`getNext()` at the start of `parse()`) → the first statement is never seen.

### Testing Your Understanding

#### Exercise 1: Predict the output
```
PRINTLINE 3+4*2;
PRINT 10-5;
```
Expected:
```
11.0
5.0
```
(`4*2` binds before `+`, so `3+8 = 11`; the second line has no newline.)

#### Exercise 2: Why does `PRINTLINE 5` (no semicolon) fail?
Because `parsePrintLNStatement()` requires `currentToken == TOK_SEMI` after the expression and throws `"; is expected"` otherwise.

#### Exercise 3: What token does the word `total_1` become?
`TOK_UNQUOTED_STRING` — it's a valid identifier (letters/digits/underscore) but not in the keyword table.

---

## Conclusion

STEP3 lifts the project from "evaluate one expression" to "**run a script of statements.**" The key additions are a second class hierarchy (`Stmt` alongside `Exp`), keyword-aware lexing driven by a small table, and a parser that loops to produce a *list* of statements executed in order.

The guiding principle — **expressions are evaluated for values; statements are executed for effects** — is exactly how full languages are structured, and it sets up everything that follows: STEP4 adds variables (so statements can store state), and later steps add control flow and functions.

### Next Steps in Learning
1. **STEP4** — variables, a symbol table, and type checking; `RuntimeContext` finally starts holding state.
2. **STEP5+** — generate real executables via code generation.

---

*This guide covers the concepts, code, and design decisions in SlangForJava STEP3. Read it after the STEP1 and STEP2 guides to see how a single-expression evaluator grows into a statement-executing mini-interpreter.*
