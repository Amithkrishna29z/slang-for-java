# Comprehensive Guide to SlangForJava STEP1 - Expression Evaluator

## Table of Contents
1. [Project Overview](#project-overview)
2. [Core Programming Concepts](#core-programming-concepts)
3. [Design Patterns Used](#design-patterns-used)
4. [File-by-File Analysis](#file-by-file-analysis)
5. [How the Code Works Together](#how-the-code-works-together)
6. [Learning Objectives](#learning-objectives)

---

## Project Overview

This project implements a simple **expression evaluator** using Java that can evaluate mathematical expressions represented as **Abstract Syntax Trees (ASTs)**. The system supports basic arithmetic operations including addition, subtraction, multiplication, and division, as well as unary operations like negation.

### What the Code Does
- Evaluates mathematical expressions like `5 * 10`
- Handles complex nested expressions like `-(10 + (30 + 50))`
- Uses object-oriented design patterns for extensibility
- Demonstrates AST construction and evaluation

---

## Core Programming Concepts

### 1. **Abstract Syntax Tree (AST)**
An AST is a tree representation of the abstract syntactic structure of source code. Each node represents a construct in the source code.

**Why use ASTs?**
- Represents code structure in a way that's easy to analyze and transform
- Separates parsing from execution
- Enables optimizations and code transformations
- Makes it easy to evaluate expressions recursively

**In this project:**
- `Exp` is the base node type
- `BinaryExp`, `UnaryExp`, and `NumericConstant` are specific node types
- The tree structure naturally represents operator precedence and grouping

### 2. **Abstract Classes and Inheritance**
An abstract class cannot be instantiated and is designed to be subclassed.

**Key Concepts:**
- **Abstract class**: A class declared with `abstract` keyword
- **Abstract method**: A method without implementation that must be overridden
- **Inheritance**: Child classes inherit properties and behaviors from parent classes

**In this project:**
```java
public abstract class Exp {
    public abstract double evaluate(RuntimeContext cont);
}
```
- `Exp` is abstract - you cannot create `new Exp()`
- All expression types must extend `Exp`
- Each subclass must implement `evaluate()`

### 3. **Polymorphism**
Polymorphism allows objects of different classes to be treated as objects of a common superclass.

**Types of Polymorphism:**
- **Compile-time (Method Overloading)**: Same method name, different parameters
- **Runtime (Method Overriding)**: Subclass provides specific implementation

**In this project:**
```java
Exp e = new BinaryExp(...);  // BinaryExp treated as Exp
double result = e.evaluate(null);  // Calls BinaryExp.evaluate()
```
- The same `evaluate()` call behaves differently based on the actual object type
- Enables writing generic code that works with all expression types

### 4. **Enum Types**
Enums are a special Java type used to define collections of constants.

**Benefits:**
- Type safety: Compiler checks for valid values
- Readability: Descriptive names instead of magic numbers
- Maintainability: Centralized constant definition
- Additional functionality: Can have fields and methods

**In this project:**
```java
public enum Operator {
    ILLEGAL(-1),
    PLUS(0),
    MINUS(1),
    DIV(2),
    MUL(3);

    private final int value;

    Operator(int value) {
        this.value = value;
    }
}
```
- Each operator has an associated integer value
- Provides compile-time checking for valid operators
- Makes code more readable than using integers directly

### 5. **Method Overriding with @Override**
Method overriding occurs when a subclass provides a specific implementation of a method that is already defined in its superclass.

**The @Override Annotation:**
- Tells the compiler you intend to override a method
- Provides compile-time error checking
- Documents the code's intent

**In this project:**
```java
@Override
public double evaluate(RuntimeContext cont) {
    // Implementation specific to this class
}
```

### 6. **Package Organization**
Packages are namespaces that organize related classes and interfaces.

**Benefits:**
- Prevents naming conflicts
- Provides access control
- Organizes code logically
- Supports modular design

**In this project:**
```java
package STEP1;
```
- All classes belong to the `STEP1` package
- Suggests this is step 1 of a larger project
- Indicates progressive learning approach

### 7. **Switch Statements with Enums**
Modern Java allows using enums directly in switch statements.

**Advantages:**
- Type-safe
- No need for break statements if using enhanced switch (not used here)
- Compile-time checking for all enum values
- More readable than if-else chains

**In this project:**
```java
switch (op) {
    case PLUS:
        return ex1.evaluate(cont) + ex2.evaluate(cont);
    case MINUS:
        return ex1.evaluate(cont) - ex2.evaluate(cont);
    // ...
}
```

### 8. **Recursive Evaluation**
Recursive evaluation is when a method calls itself to solve subproblems.

**In this project:**
```java
return ex1.evaluate(cont) + ex2.evaluate(cont);
```
- Binary expressions evaluate their left and right operands recursively
- Naturally handles nested expressions
- Base case: `NumericConstant` returns its value without recursion

### 9. **Runtime Context Pattern**
A runtime context provides the environment in which code executes.

**Purpose:**
- Holds variable values
- Maintains execution state
- Enables scoping rules
- Supports function calls and returns

**In this project:**
```java
public class RuntimeContext {
    public RuntimeContext() {
        // Currently empty - prepared for future expansion
    }
}
```
- Currently empty but prepared for variables, functions, etc.
- Demonstrates forward-thinking design
- Parameter passed to all `evaluate()` methods

### 10. **Constructor Overloading**
Constructors can be overloaded to provide different ways to initialize objects.

**In this project:**
```java
public BinaryExp(Exp ex1, Exp ex2, Operator op) {
    this.ex1 = ex1;
    this.ex2 = ex2;
    this.op = op;
}
```
- Initializes all instance variables
- Uses `this` keyword to distinguish between parameters and instance variables
- Ensures objects are created in a valid state

### 11. **Access Modifiers**
Java provides four levels of access control:

1. **public**: Accessible from anywhere
2. **protected**: Accessible within package and subclasses
3. **default (package-private)**: Accessible only within package
4. **private**: Accessible only within the class

**In this project:**
```java
private Exp ex1, ex2;  // Encapsulation - hide implementation details
public abstract double evaluate(RuntimeContext cont);  // Public interface
```

---

## Design Patterns Used

### 1. **Composite Pattern**
The Composite pattern allows you to compose objects into tree structures to represent part-whole hierarchies.

**Structure:**
- **Component**: `Exp` (abstract base class)
- **Leaf**: `NumericConstant` (has no children)
- **Composite**: `BinaryExp`, `UnaryExp` (have children)

**Benefits:**
- Treats individual objects and compositions uniformly
- Makes it easy to add new types of components
- Simplifies client code

**Implementation:**
```java
// Client code doesn't need to know if it's a leaf or composite
Exp expression = new BinaryExp(...);
double result = expression.evaluate(context);
```

### 2. **Interpreter Pattern**
The Interpreter pattern defines a grammar for a language and an interpreter to interpret sentences in that language.

**In this project:**
- Grammar: Simple arithmetic expressions
- Expression classes: Each represents a grammar rule
- `evaluate()` method: Interprets the expression

**Benefits:**
- Easy to change and extend the grammar
- Easy to implement new expressions
- Separates grammar from implementation

### 3. **Template Method Pattern**
The Template Method pattern defines the skeleton of an algorithm in a base class and lets subclasses override specific steps.

**In this project:**
```java
public abstract class Exp {
    public abstract double evaluate(RuntimeContext cont);  // Template method
}
```
- Base class defines the interface
- Each subclass provides its own implementation
- Ensures consistent interface across all expression types

---

## File-by-File Analysis

### Exp.java
```java
package STEP1;

public abstract class Exp {
    public abstract double evaluate(RuntimeContext cont);
}
```

**Purpose:** Base class for all expression types in the AST.

**Key Concepts:**
- **Abstract class**: Cannot be instantiated, serves as a template
- **Abstract method**: Forces subclasses to provide implementation
- **Double return type**: All expressions evaluate to numeric values
- **RuntimeContext parameter**: Prepares for future features like variables

**Design Decisions:**
- Why `double`? Supports both integer and floating-point arithmetic
- Why `RuntimeContext`? Enables future extensions (variables, functions)
- Why abstract? Ensures all expressions have evaluation semantics

**Usage Example:**
```java
// Cannot do this:
Exp e = new Exp();  // Compiler error!

// But can do this:
Exp e = new NumericConstant(5.0);  // OK
```

---

### NumericConstant.java
```java
package STEP1;

public class NumericConstant extends Exp {
    private double value;

    public NumericConstant(double value) {
        this.value = value;
    }

    @Override
    public double evaluate(RuntimeContext cont) {
        return value;
    }
}
```

**Purpose:** Represents literal numeric values in expressions (leaf nodes).

**Key Concepts:**
- **Inheritance**: Extends `Exp` base class
- **Encapsulation**: Private field with public constructor
- **Constructor**: Initializes the constant value
- **Method overriding**: Provides concrete implementation of `evaluate()`

**Design Decisions:**
- Why `private double value`? Encapsulation - prevents external modification
- Why simple return? Constants don't need computation
- Why ignore `RuntimeContext`? Constants are context-independent

**Usage Example:**
```java
Exp five = new NumericConstant(5.0);
Exp pi = new NumericConstant(3.14159);

System.out.println(five.evaluate(null));  // Output: 5.0
System.out.println(pi.evaluate(null));    // Output: 3.14159
```

---

### Operator.java
```java
package STEP1;

public enum Operator {
    ILLEGAL(-1),
    PLUS(0),
    MINUS(1),
    DIV(2),
    MUL(3);

    private final int value;

    Operator(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
```

**Purpose:** Defines all supported arithmetic operators as type-safe constants.

**Key Concepts:**
- **Enum**: Type-safe constant enumeration
- **Enum constructor**: Initializes each enum constant
- **Final field**: Immutable value associated with each operator
- **Getter method**: Provides read access to the value

**Design Decisions:**
- Why enum instead of constants? Type safety and better organization
- Why associated integer values? Useful for parsing or serialization
- Why `ILLEGAL`? Error handling for invalid operators
- Why `final`? Prevents modification after construction

**Enum Values:**
- `ILLEGAL(-1)`: Represents an invalid or unknown operator
- `PLUS(0)`: Addition operator (+)
- `MINUS(1)`: Subtraction operator (-)
- `DIV(2)`: Division operator (/)
- `MUL(3)`: Multiplication operator (*)

**Usage Example:**
```java
Operator op = Operator.PLUS;
System.out.println(op);           // Output: PLUS
System.out.println(op.getValue()); // Output: 0

// Type-safe comparison
if (op == Operator.PLUS) {
    // Handle addition
}

// Cannot use invalid values
// Operator invalid = 5;  // Compiler error!
```

---

### BinaryExp.java
```java
package STEP1;

public class BinaryExp extends Exp {
    private Exp ex1, ex2;
    private Operator op;

    public BinaryExp(Exp ex1, Exp ex2, Operator op) {
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.op = op;
    }

    @Override
    public double evaluate(RuntimeContext cont) {
        switch (op) {
            case PLUS:
                return ex1.evaluate(cont) + ex2.evaluate(cont);
            case MINUS:
                return ex1.evaluate(cont) - ex2.evaluate(cont);
            case DIV:
                return ex1.evaluate(cont) / ex2.evaluate(cont);
            case MUL:
                return ex1.evaluate(cont) * ex2.evaluate(cont);
            default:
                return Double.NaN;
        }
    }
}
```

**Purpose:** Represents binary operations (operations with two operands) like addition, subtraction, multiplication, and division.

**Key Concepts:**
- **Binary operation**: Takes two operands and produces one result
- **Composition**: Contains other `Exp` objects (recursive structure)
- **Switch statement**: Dispatches to appropriate operation
- **Recursive evaluation**: Evaluates operands before applying operator
- **Error handling**: Returns `Double.NaN` for unknown operators

**Instance Variables:**
- `private Exp ex1`: Left operand (first expression)
- `private Exp ex2`: Right operand (second expression)
- `private Operator op`: The operator to apply

**Design Decisions:**
- Why `Exp` type for operands? Allows nesting of any expression type
- Why switch on operator? Clear and efficient dispatch
- Why `Double.NaN`? Standard way to represent invalid results
- Why recursive evaluation? Naturally handles nested expressions

**Evaluation Process:**
1. Receive the `RuntimeContext`
2. Switch on the operator type
3. Recursively evaluate the left operand (`ex1`)
4. Recursively evaluate the right operand (`ex2`)
5. Apply the operator to the results
6. Return the final result

**Usage Example:**
```java
// Create: 5 + 3
Exp add = new BinaryExp(
    new NumericConstant(5),
    new NumericConstant(3),
    Operator.PLUS
);
System.out.println(add.evaluate(null));  // Output: 8.0

// Create: 10 * 2
Exp multiply = new BinaryExp(
    new NumericConstant(10),
    new NumericConstant(2),
    Operator.MUL
);
System.out.println(multiply.evaluate(null));  // Output: 20.0

// Nested: (5 + 3) * 2
Exp complex = new BinaryExp(
    add,                    // Reuse the addition expression
    new NumericConstant(2),
    Operator.MUL
);
System.out.println(complex.evaluate(null));  // Output: 16.0
```

**AST Visualization for `5 + 3 * 2`:**
```
    +
   / \
  5   *
     / \
    3   2
```

---

### UnaryExp.java
```java
package STEP1;

public class UnaryExp extends Exp {
    private Exp ex1;
    private Operator op;

    public UnaryExp(Exp ex1, Operator op) {
        this.ex1 = ex1;
        this.op = op;
    }

    @Override
    public double evaluate(RuntimeContext cont) {
        switch(op) {
            case PLUS:
                return ex1.evaluate(cont);
            case MINUS:
                return -ex1.evaluate(cont);
            default:
                return Double.NaN;
        }
    }
}
```

**Purpose:** Represents unary operations (operations with one operand) like unary plus and unary minus (negation).

**Key Concepts:**
- **Unary operation**: Takes one operand and produces one result
- **Negation**: Mathematical operation that changes the sign of a number
- **Identity operation**: Returns the operand unchanged (unary plus)
- **Single operand**: Only one expression to evaluate

**Instance Variables:**
- `private Exp ex1`: The single operand
- `private Operator op`: The unary operator

**Supported Operations:**
- `PLUS`: Unary plus - returns the operand unchanged (e.g., `+5` = `5`)
- `MINUS`: Unary minus - negates the operand (e.g., `-5` = `-5`)

**Design Decisions:**
- Why reuse `Operator` enum? Consistency with binary expressions
- Why support unary plus? Completeness and language specification
- Why only PLUS and MINUS? These are the most common unary operators
- Why `Double.NaN` for default? Error handling consistency

**Evaluation Process:**
1. Receive the `RuntimeContext`
2. Switch on the operator type
3. Recursively evaluate the single operand
4. Apply the unary operation:
   - For PLUS: return the value as-is
   - For MINUS: return the negated value
5. Return the result

**Usage Example:**
```java
// Unary plus: +5
Exp unaryPlus = new UnaryExp(
    new NumericConstant(5),
    Operator.PLUS
);
System.out.println(unaryPlus.evaluate(null));  // Output: 5.0

// Unary minus: -5
Exp unaryMinus = new UnaryExp(
    new NumericConstant(5),
    Operator.MINUS
);
System.out.println(unaryMinus.evaluate(null));  // Output: -5.0

// Nested: -(5 + 3)
Exp negatedSum = new UnaryExp(
    new BinaryExp(
        new NumericConstant(5),
        new NumericConstant(3),
        Operator.PLUS
    ),
    Operator.MINUS
);
System.out.println(negatedSum.evaluate(null));  // Output: -8.0
```

**AST Visualization for `-(5 + 3)`:**
```
    -
    |
    +
   / \
  5   3
```

---

### RuntimeContext.java
```java
package STEP1;

public class RuntimeContext {
    public RuntimeContext() {

    }
}
```

**Purpose:** Provides the execution environment for expression evaluation (currently empty, prepared for future expansion).

**Key Concepts:**
- **Context object**: Carries state through evaluation
- **Future-proofing**: Designed for extensibility
- **Empty implementation**: Placeholder for upcoming features

**Current State:**
- Empty constructor with no functionality
- No instance variables or methods
- Passed to all `evaluate()` methods but not used

**Future Potential Uses:**
```java
// Possible future implementation:
public class RuntimeContext {
    private Map<String, Double> variables = new HashMap<>();

    public void setVariable(String name, double value) {
        variables.put(name, value);
    }

    public Double getVariable(String name) {
        return variables.get(name);
    }
}
```

**Why Include It Now?**
- Demonstrates good architectural planning
- Makes adding variables easier later
- Shows understanding of interpreter patterns
- Prepares for scoping and state management

**Usage Example (Current):**
```java
RuntimeContext context = new RuntimeContext();
Exp expression = new NumericConstant(5.0);
double result = expression.evaluate(context);  // context ignored for now
```

---

### Program.java
```java
package STEP1;

public class Program {
    public static void main(String[] args) {
        // AST for 5*10
        Exp e = new BinaryExp(new NumericConstant(5), new NumericConstant(10), Operator.MUL);
        System.out.println(e.evaluate(null));

        // AST for -(10 + (30 + 50))
        e = new UnaryExp(
                new BinaryExp(new NumericConstant(10),
                        new BinaryExp(new NumericConstant(30), new NumericConstant(50), Operator.PLUS), Operator.PLUS),
                Operator.MINUS);

        System.out.println(e.evaluate(null));

        try {
            System.in.read();
        } catch (Exception ex) {

        }
    }
}
```

**Purpose:** Main program that demonstrates the expression evaluator by creating and evaluating ASTs.

**Key Concepts:**
- **Main method**: Entry point of the Java application
- **AST construction**: Manually building expression trees
- **Polymorphic evaluation**: Calling `evaluate()` on different expression types
- **Expression reuse**: Reassigning the same variable to different expressions
- **User input pause**: Keeps console window open (common in Windows)

**Detailed Breakdown:**

#### Example 1: Simple Multiplication
```java
Exp e = new BinaryExp(new NumericConstant(5), new NumericConstant(10), Operator.MUL);
System.out.println(e.evaluate(null));
```
**AST Structure:**
```
    *
   / \
  5   10
```
**Evaluation:**
1. Evaluate left operand: `5`
2. Evaluate right operand: `10`
3. Apply multiplication: `5 * 10 = 50`
4. Output: `50.0`

#### Example 2: Complex Nested Expression
```java
e = new UnaryExp(
    new BinaryExp(new NumericConstant(10),
        new BinaryExp(new NumericConstant(30), new NumericConstant(50), Operator.PLUS),
        Operator.PLUS),
    Operator.MINUS);
```
**Expression Represented:** `-(10 + (30 + 50))`

**AST Structure:**
```
    -
    |
    +
   / \
 10   +
     / \
    30  50
```

**Evaluation Process:**
1. Start at root (unary minus)
2. Evaluate its operand (binary plus):
   - Evaluate left: `10`
   - Evaluate right (binary plus):
     - Evaluate left: `30`
     - Evaluate right: `50`
     - Apply plus: `30 + 50 = 80`
   - Apply plus: `10 + 80 = 90`
3. Apply unary minus: `-90`
4. Output: `-90.0`

#### Console Pause
```java
try {
    System.in.read();
} catch (Exception ex) {

}
```
- Waits for user to press a key
- Prevents console window from closing immediately
- Common pattern in console applications on Windows

**Design Decisions:**
- Why manual AST construction? Demonstrates understanding of tree structure
- Why reuse variable `e`? Shows polymorphism - same type, different implementations
- Why `null` for context? RuntimeContext not needed yet
- Why empty catch block? Simple error handling for demo purposes

**Complete Execution Flow:**
```
1. Create AST for 5 * 10
2. Evaluate: 50.0
3. Print: 50.0
4. Create AST for -(10 + (30 + 50))
5. Evaluate: -90.0
6. Print: -90.0
7. Wait for user input
8. Program ends
```

---

## How the Code Works Together

### Execution Flow Diagram

```
Program.main()
    |
    v
Create AST Structure
    |
    v
Call evaluate() on root Exp
    |
    v
[Polymorphic Dispatch]
    |
    +---> NumericConstant.evaluate() --> return value
    |
    +---> BinaryExp.evaluate()
    |       |
    |       v
    |   Recursive evaluation of ex1 and ex2
    |       |
    |       v
    |   Apply operator
    |       |
    |       v
    |   Return result
    |
    +---> UnaryExp.evaluate()
            |
            v
        Recursive evaluation of ex1
            |
            v
        Apply unary operator
            |
            v
        Return result
```

### Example: Evaluating `5 * 10 + 3`

**AST Construction:**
```java
Exp expression = new BinaryExp(
    new BinaryExp(
        new NumericConstant(5),
        new NumericConstant(10),
        Operator.MUL
    ),
    new NumericConstant(3),
    Operator.PLUS
);
```

**AST Structure:**
```
      +
     / \
    *   3
   / \
  5   10
```

**Step-by-Step Evaluation:**

1. **Call `expression.evaluate(null)`**
   - This is a `BinaryExp` with `Operator.PLUS`

2. **BinaryExp.evaluate() executes:**
   ```java
   switch (op) {
       case PLUS:
           return ex1.evaluate(cont) + ex2.evaluate(cont);
   ```

3. **Evaluate `ex1` (the multiplication `5 * 10`):**
   - This is another `BinaryExp` with `Operator.MUL`
   - Calls `ex1.evaluate(cont)`: `NumericConstant(5).evaluate()` → `5.0`
   - Calls `ex2.evaluate(cont)`: `NumericConstant(10).evaluate()` → `10.0`
   - Applies multiplication: `5.0 * 10.0 = 50.0`
   - Returns `50.0`

4. **Evaluate `ex2` (the constant `3`):**
   - This is a `NumericConstant`
   - Returns `3.0`

5. **Apply the outer addition:**
   - `50.0 + 3.0 = 53.0`

6. **Final Result:** `53.0`

### Object Relationships

```
                    Exp (abstract class)
                         /    |    \
                        /     |     \
                       /      |      \
                      /       |       \
        NumericConstant  BinaryExp  UnaryExp
                       (has 2 Exp) (has 1 Exp)
                            |           |
                            v           v
                        Operator     Operator
                        (enum)       (enum)
```

### Key Relationships

1. **Inheritance:** All expression types extend `Exp`
2. **Composition:** `BinaryExp` and `UnaryExp` contain other `Exp` objects
3. **Association:** All expressions use `Operator` enum
4. **Dependency:** All expressions accept `RuntimeContext` parameter

---

## Learning Objectives

### What You'll Learn From This Code

#### 1. **Object-Oriented Programming Fundamentals**
- How to design class hierarchies
- When to use abstract classes vs. concrete classes
- How to implement inheritance and polymorphism
- The importance of encapsulation

#### 2. **Design Patterns**
- **Composite Pattern**: Building tree structures
- **Interpreter Pattern**: Evaluating expressions
- **Template Method Pattern**: Defining algorithm skeletons
- When and why to use each pattern

#### 3. **Data Structures**
- **Tree Structures**: Understanding ASTs
- **Recursive Algorithms**: Evaluating nested expressions
- **Graph Traversal**: Pre-order evaluation

#### 4. **Java-Specific Concepts**
- **Enums**: Type-safe constants
- **Abstract Classes**: Creating base classes
- **Method Overriding**: Providing specific implementations
- **Package Organization**: Structuring code
- **Access Modifiers**: Controlling visibility

#### 5. **Software Design Principles**
- **Open/Closed Principle**: Easy to extend, closed to modification
- **Single Responsibility Principle**: Each class has one job
- **Liskov Substitution Principle**: Subtypes can replace base types
- **Dependency Inversion**: Depend on abstractions, not concretions

#### 6. **Compiler and Interpreter Concepts**
- How programming languages represent expressions
- The difference between parsing and evaluation
- How operator precedence works in ASTs
- The role of runtime contexts

### How to Extend This Project

#### Adding New Operators
1. Add to `Operator` enum:
   ```java
   POWER(4),  // For exponentiation
   MODULO(5)  // For modulo
   ```

2. Add cases to `BinaryExp.evaluate()`:
   ```java
   case POWER:
       return Math.pow(ex1.evaluate(cont), ex2.evaluate(cont));
   case MODULO:
       return ex1.evaluate(cont) % ex2.evaluate(cont);
   ```

#### Adding Variables
1. Extend `RuntimeContext`:
   ```java
   private Map<String, Double> variables = new HashMap<>();

   public void setVariable(String name, double value) {
       variables.put(name, value);
   }

   public double getVariable(String name) {
       return variables.get(name);
   }
   ```

2. Create `VariableReference` class:
   ```java
   public class VariableReference extends Exp {
       private String name;

       public VariableReference(String name) {
           this.name = name;
       }

       @Override
       public double evaluate(RuntimeContext cont) {
           return cont.getVariable(name);
       }
   }
   ```

#### Adding Functions
1. Create `FunctionCall` class:
   ```java
   public class FunctionCall extends Exp {
       private String functionName;
       private Exp[] arguments;

       @Override
       public double evaluate(RuntimeContext cont) {
           // Implement function lookup and execution
       }
   }
   ```

### Common Mistakes to Avoid

1. **Forgetting @Override**: Always use `@Override` when overriding methods
2. **Not handling all enum cases**: Use `default` case or handle all values
3. **Ignoring null contexts**: Even if unused, pass the context parameter
4. **Creating circular references**: Be careful with recursive structures
5. **Not considering operator precedence**: AST structure must reflect precedence

### Testing Your Understanding

#### Exercise 1: Create an AST for `(15 - 5) / 2`
```java
Exp expression = new BinaryExp(
    new BinaryExp(
        new NumericConstant(15),
        new NumericConstant(5),
        Operator.MINUS
    ),
    new NumericConstant(2),
    Operator.DIV
);
// Expected result: 5.0
```

#### Exercise 2: Create an AST for `-(-10)`
```java
Exp expression = new UnaryExp(
    new UnaryExp(
        new NumericConstant(10),
        Operator.MINUS
    ),
    Operator.MINUS
);
// Expected result: 10.0
```

#### Exercise 3: Draw the AST for `3 + 4 * 5 - 2`
```
        -
       / \
      +   2
     / \
    3   *
       / \
      4   5
```

---

## Conclusion

This project provides a solid foundation for understanding:
- How compilers and interpreters work
- Object-oriented design principles
- Design patterns in practice
- Java language features
- Data structures and algorithms

The beauty of this design is its **extensibility**. You can easily add:
- New operators
- Variables and assignments
- Functions and procedures
- Control structures (if, while, for)
- Data types (strings, booleans, arrays)
- Scoping rules
- Error handling

This is often called a **"Mini-Language"** or **Domain-Specific Language (DSL)** implementation, and the patterns used here scale up to full programming languages.

### Next Steps in Learning

1. **Add a parser**: Convert string expressions to ASTs automatically
2. **Implement variables**: Add state to your language
3. **Add control flow**: Implement if statements and loops
4. **Create functions**: Allow defining and calling functions
5. **Build a REPL**: Interactive read-eval-print loop
6. **Add type checking**: Ensure type safety
7. **Optimize**: Implement constant folding and other optimizations

### Additional Resources

- **Design Patterns**: "Design Patterns: Elements of Reusable Object-Oriented Software" by Gang of Four
- **Compilers**: "Compilers: Principles, Techniques, and Tools" (Dragon Book)
- **Java**: Official Java documentation and tutorials
- **ASTs**: Research Abstract Syntax Trees and their applications

---

*This comprehensive guide covers every concept, line of code, and design decision in the SlangForJava STEP1 project. Use it as a reference as you explore and extend the codebase.*