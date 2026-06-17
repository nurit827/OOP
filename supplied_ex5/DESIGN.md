# s-Java Verifier — Design Specification

This document describes the **agreed class design** for the OOP Exercise 5 s-Java verifier.
You (Claude Code) already have the official exercise instructions (`ex5_instructions`).
This file is **not** a restatement of those rules — it is the architecture to implement them
against. Where this doc and the official spec disagree on a *rule*, the official spec wins.
Where they concern *structure* (classes, responsibilities, method signatures), follow this doc.

---

## 1. Core design principles (decided, do not relitigate)

1. **Two layers, kept strictly separate:**
   - **Parser layer** — touches raw text. Turns each line into a `ParsedLine` object or rejects it.
   - **Semantic layer** — never touches raw text. Operates only on `ParsedLine` objects and the
     symbol-table state (`Scope`, `Variable`, `Method`).
   The parser is a **gate**: malformed text is rejected (illegal); well-formed text is parsed
   *once* into structured pieces and passed forward. Nothing downstream re-parses text.

2. **Behavioral `ParsedLine` interface (polymorphic, not a type-switch).**
   Each line kind knows how to semantically validate *itself* via `checkSemantics(ctx)`.
   The driver/managers never do `instanceof` dispatch on line kinds.

3. **`Scope` searches itself, then delegates upward.**
   Each `Scope` holds its own variables **and a reference to its parent `Scope`**. Lookup is
   "check self → ask parent → throw if past global". The parent-pointer chain *is* the scope stack.

4. **Driver owns scope lifecycle and cross-line structure.**
   Scope push/pop (on `{` / `}`), brace matching, and "return must be last in a method" are
   **between-line** facts. A single line cannot check them. These live in the manager that walks
   the body — **not** inside any `ParsedLine`.

5. **Exceptions, not boolean threading.**
   Validation methods return `void` and **throw** on the first illegality. A granular
   exception hierarchy (see §5.11):
   - illegal code  → `SyntaxException` base (and its subclasses) → printed value `1`
   - IO / arg error → `IOErrorException` → printed value `2`
   Each throw site throws the **most specific** subclass that fits.

6. **Methods are a flat namespace, not scoped.**
   Methods cannot nest, cannot shadow, and there is exactly one method namespace per file.
   They live in a flat `Map<String, Method>` on `GeneralManager` — **not** inside any `Scope`.

7. **`GeneralManager` is the driver.** No separate "Verifier" class. `GeneralManager` owns the
   `Parser`, the global `Scope`, and the method table, and runs both passes. It creates one
   `MethodManager` per method body in pass 2.

---

## 2. Two-pass flow

Method calls and global references can appear **before** the thing they refer to is declared, so
a single forward pass is impossible. Use two passes:

- **Pass 1 — collect.** Parse every line. Register global variable declarations into the global
  `Scope`, and register each method signature into the method table. Record where each method
  body begins and ends (line range) so pass 2 can walk it. Per-line *syntax* errors surface here
  (since every line is parsed anyway).
- **Pass 2 — validate bodies.** For each method, create a `MethodManager` and walk the body
  lines, calling `checkSemantics(ctx)` on each, pushing/popping scopes on `{`/`}`, and tracking
  brace balance + return-last.

> Decision still open for the implementer: record method line-ranges in pass 1 (preferred) vs.
> re-scan for `void` openings in pass 2. Recording ranges in pass 1 is cleaner since you are
> already parsing there. Default to recording ranges.

---

## 3. Output contract

- Print a single digit via `System.out.println`: `0` legal, `1` illegal, `2` IO error.
- On `1` or `2`, also print an informative message to `System.err`.
- On illegal code, reporting the **first** problem is sufficient.

---

## 4. Package structure

```
ex5
└── main
    └── Sjavac          (entry point: ex5.main.Sjavac)
```

`ex5.main.Sjavac` must contain `main`. Other classes may live in `main` or in sibling packages
under `ex5` — group by responsibility (e.g. `ex5.parsing`, `ex5.semantics`, `ex5.lines`).
**Each custom exception goes in the same package as the class that throws it** (not a separate
"exceptions" package), and must **not** be a nested class.

---

## 5. Classes

Fields are `private` (and `final` where noted). Getters are `public`. Each line class's fields are
set by the parser at construction.

### 5.1 Enums / value helpers

**`Type`** (enum) — `INT, DOUBLE, BOOLEAN, CHAR, STRING`
- `boolean accepts(Type other)` — assignment compatibility:
  `double` accepts `int`; `boolean` accepts `int` and `double`; otherwise types must be equal.
- `static Type fromKeyword(String)` — maps `"int"`→`INT`, etc.

**`Kind`** (enum) — `DECLARATION, ASSIGNMENT, METHOD_DECL, METHOD_CALL, CONDITION, RETURN, CLOSE_BRACE`

Small data holders used by the line classes (records or simple classes):
- **`Entry`** — variable name + optional value string (for `DeclarationLine`).
- **`Assign`** — variable name + value string (for `AssignmentLine`).
- **`Param`** — type + name (for `MethodDeclLine`).

### 5.2 `Variable`

A value record describing one declared variable. The **name is not stored here** — it is the key
the `Scope` files it under.

- fields: `Type type`, `boolean initialized`, `boolean isFinal` (final)
- `Type getType()`
- `boolean isInitialized()`
- `boolean isFinal()`
- `void markInitialized()` — flips the flag when assigned

Why these three: `type` drives assignment compatibility; `initialized` enforces "no use before
assignment"; `isFinal` enforces "no reassignment after declaration".

### 5.3 `Method`

A signature record — the thing method calls are checked against. Does **not** hold the body; once
a body is validated nothing else cares about it.

- fields: `String name` (final), `List<Type> paramTypes` (final)
- `String getName()`
- `List<Type> getParamTypes()`
- `boolean matches(List<Type> argTypes)` — arg count + per-arg compatibility (using `Type.accepts`)

### 5.4 `Scope`

Owns the variables visible in one block, and links to its enclosing scope.

- fields: `Map<String, Variable> vars`, `Scope parent` (final; `null` for the global scope)
- `void declare(String name, Variable v)` — throws if `name` already exists **in this scope**
- `Variable resolve(String name)` — search self → parent → throw if not found anywhere in chain
- `Variable resolveLocal(String name)` — this scope only; `null` if absent (used for the
  duplicate-declaration check)
- `Scope getParent()`

> `resolve` is the general "is this name visible here?" lookup. `resolveLocal` is the shallow
> check used only to detect re-declaration in the same block.

### 5.5 `ParsedLine` (interface) — behavioral

- `Kind getKind()` — used by the driver for cross-line tracking (return-last, brace handling)
- `void checkSemantics(MethodManager ctx) throws SyntaxException` — each line validates itself,
  reaching the symbol tables through `ctx`

`ctx` (a `MethodManager`) is how a line asks "what is visible right now":
`ctx.getCurrentScope().resolve(...)` and `ctx.getGeneral().getMethod(...)`.

### 5.6 Line classes (implement `ParsedLine`)

**`DeclarationLine`**
- fields: `boolean isFinal`, `Type type`, `List<Entry> entries`
- `isFinal()`, `getType()`, `getEntries()`, `getKind() → DECLARATION`
- `checkSemantics`: for each entry, build a `Variable` (final = the line's `isFinal`) and
  `declare` it into the current scope; if the entry has a value, check the value's type is
  accepted by `type` and `markInitialized()`.
  - `isFinal` is the **line-level** flag: `final` applies to *all* entries on the line, and a
    final entry must have a value at declaration.

**`AssignmentLine`**
- fields: `List<Assign> assigns`
- `getAssigns()`, `getKind() → ASSIGNMENT`
- `checkSemantics`: for each assign, `resolve` the name, reject if `isFinal`, check the value's
  type is accepted, then `markInitialized()`.

**`MethodDeclLine`**
- fields: `String name`, `List<Param> params`
- `getName()`, `getParams()`, `getKind() → METHOD_DECL`
- `checkSemantics`: declare each parameter into the current scope as already-initialized.
  (The driver opens the method's top scope before this runs, so params land there.)

**`MethodCallLine`**
- fields: `String name`, `List<String> args`
- `getName()`, `getArgs()`, `getKind() → METHOD_CALL`
- `checkSemantics`: look up the method via `ctx.getGeneral()` (throw if missing); resolve each
  arg to a `Type` (literal or initialized variable); call `method.matches(argTypes)`.

**`ConditionLine`** (covers both `if` and `while` — distinguished by a flag)
- fields: `boolean isWhile`, `List<String> tokens`
- `isWhile()`, `getTokens()`, `getKind() → CONDITION`
- `checkSemantics`: each operand must be `true`/`false`, an int/double literal, or an initialized
  `boolean`/`int`/`double` variable; `&&` / `||` must sit between two operands.

**`ReturnLine`**
- no fields; `getKind() → RETURN`; `checkSemantics` is a no-op.
- The driver uses `getKind()` to enforce "return is the last statement in a method".

**`CloseBraceLine`**
- no fields; `getKind() → CLOSE_BRACE`; `checkSemantics` is a no-op.
- The driver pops the current scope when it sees this.

> Getter note: with the behavioral interface, a line validates itself, so the managers do **not**
> read the line's pieces via getters. Keep a getter only if an external caller actually uses it;
> otherwise the line reads its own private fields directly inside `checkSemantics`. Treat the
> getters above as optional surface, not mandatory.

### 5.7 `Parser`

Text → `ParsedLine`, or throw. The **only** public method is `parseLine`; everything else is
internal organization.

- fields: `private` compiled `Pattern` constants (one per line kind)
- `ParsedLine parseLine(String raw) throws SyntaxException`
- private helpers:
  - `classify(String)` — the **router**: inspects the line's ending suffix (`;` `{` `}`) and
    leading keyword to decide the `Kind`, then dispatches to the right `parseX`. For the no-data
    kinds (`ReturnLine`, `CloseBraceLine`) it constructs them directly. Empty/comment lines are
    dropped here (not turned into objects).
  - `parseDeclaration(String)` → `DeclarationLine`
  - `parseAssignment(String)` → `AssignmentLine`
  - `parseMethodDecl(String)` → `MethodDeclLine`
  - `parseMethodCall(String)` → `MethodCallLine`
  - `parseCondition(String)` → `ConditionLine`

> There are 7 kinds but only 5 `parseX` helpers, because `ReturnLine` and `CloseBraceLine` carry
> no data to extract.

### 5.8 `GeneralManager` (driver + file-wide state)

Owns the parser, the global scope, and the method table; runs both passes.

- fields: `Parser parser` (final), `Scope globalScope` (final), `Map<String, Method> methods` (final)
- `int verify(List<String> lines)` — runs pass 1 (collect) then pass 2 (walk each method body via
  a `MethodManager`); returns `0` / `1` / `2`
- `void collectGlobal(DeclarationLine line) throws SyntaxException`
- `void collectMethod(MethodDeclLine line) throws SyntaxException` — throws on duplicate method name
- `Scope getGlobalScope()`
- `boolean methodExists(String name)`
- `Method getMethod(String name)`

### 5.9 `MethodManager` (validates one method body)

Holds the current scope and a back-reference to `GeneralManager`. Owns scope push/pop and the
per-body cross-line tracking (brace balance, return-last). Lines reach globals and the method
table through it.

- fields: `Scope currentScope`, `GeneralManager general` (final)
- `void enterBlock()` — push a child scope whose parent is the current scope
- `void exitBlock()` — pop back to the parent scope
- `Scope getCurrentScope()`
- `GeneralManager getGeneral()`

> When a method body begins, the manager opens the method's **top** scope with the **global scope
> as its parent** (so locals/params chain up to globals). It then runs `MethodDeclLine`'s check to
> place the parameters into that top scope.

### 5.10 `Sjavac` (entry point — `ex5.main.Sjavac`)

- `public static void main(String[] args)` — validate arg count, open/read the file, delegate to
  `GeneralManager.verify`, print `0`/`1`/`2`, and `System.err` an informative message on `1`/`2`.
  Top-level `catch` for the two custom exceptions maps them to the right exit digit.

### 5.11 Exceptions (granular hierarchy; each in its thrower's package; none nested)

Two roots, both `extends Exception`. The driver catches only the two roots:
`SyntaxException` → print `1`, `IOErrorException` → print `2` — each with an informative
`System.err` message. Each throw site throws the **most specific** subclass that fits.

**`SyntaxException extends Exception`** — base class for all illegal s-Java code → printed value `1`.
Catching the base catches every subclass below. Subclasses (each in the same package as its thrower):

- `UndeclaredVariableException` — reference to a variable not visible in the current scope chain.
- `UninitializedVariableException` — use of a declared-but-not-yet-initialized variable.
- `FinalAssignmentException` — assignment to a `final` variable after its declaration line.
- `TypeMismatchException` — value/variable type not accepted by the target type.
- `DuplicateVariableException` — a name already declared in the same scope (re-declaration).
- `DuplicateMethodException` — a second method declared with an existing method name.
- `UndeclaredMethodException` — a call to a method that does not exist in the method table.
- `ArgumentMismatchException` — method call with wrong arg count or incompatible arg types.
- `MalformedLineException` — parser-level malformed text (a line that is not well-formed s-Java).
- `ScopeStructureException` — cross-line structural error: brace imbalance or `return` not last.

**`IOErrorException extends Exception`** — single class for bad argument count, missing file, and
wrong/missing `.sjava` suffix → printed value `2`.

---

## 6. Responsibility map (one-line summary per class)

| Class | Owns | Answers / does |
|---|---|---|
| `Parser` | regex patterns | "is this line well-formed text? give me its pieces" |
| `ParsedLine` (+ kinds) | extracted pieces | "validate myself against the current context" |
| `Scope` | vars in one block + parent link | "is this name visible? declare it" |
| `Variable` | type/initialized/final | the facts the rules ask about a variable |
| `Method` | name + param types | "do these argument types fit me?" |
| `GeneralManager` | global scope, method table, parser | run both passes; global + method lookups |
| `MethodManager` | current scope, brace/return tracking | walk one body; hand lines their context |
| `Sjavac` | args, IO, output | run the program; print 0/1/2 |

---

## 7. Things deliberately left to the implementer

- **Method body line-ranges:** record in pass 1 (preferred) or re-scan in pass 2.
- **Value-type resolution helper:** resolving a token to a `Type` ("is this `5` or `x`?") recurs
  in `AssignmentLine`, `MethodCallLine`, and `ConditionLine`. Consider a single shared helper
  (e.g. on `MethodManager`) rather than three copies — but keep it optional if it adds noise.
- **`Entry` / `Assign` / `Param`** can be Java records or plain small classes.

---

## 8. Hard constraints from the official spec (reminders, not the full list)

- Reserved words: `int double boolean char String void final if while true false return`.
- A code line ends in exactly one of `;` `{` `}` (whitespace allowed around them); the suffix may
  not move to the next line.
- Only `//` comments, and only at column 0. No `/* */`, no `/** */`, no mid-line `//`.
- No operators, no arrays.
- Whitespace required between `void`/name, type/name, `final`/type; not allowed inside
  names/values/keywords.
- Variable names: letters/digits/`_`, not starting with a digit, not a lone `_`, not starting with
  two or more `_`. Method names additionally must start with a letter.
- Method calls only inside methods; methods never nested; no overloading; `return;` last.
- `final` before the type; final vars initialized at declaration, never reassigned.
- Compatibility widening: `int`→`double`; `int`/`double`→`boolean`.

Where this section is incomplete, defer to the official `ex5_instructions`.
