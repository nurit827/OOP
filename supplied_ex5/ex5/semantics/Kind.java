package ex5.semantics;

/**
 * Identifies what kind of statement a parsed line is. The driver uses a line's {@code Kind} for
 * cross-line tracking that no single line can perform on its own (brace push/pop on
 * {@link #CONDITION}/{@link #METHOD_DECL} and {@link #CLOSE_BRACE}, and the "{@link #RETURN} must be
 * the last statement" rule).
 */
public enum Kind {
    /** A variable declaration line, e.g. {@code int a = 5, b;}. */
    DECLARATION,
    /** A variable assignment line, e.g. {@code a = 5;}. */
    ASSIGNMENT,
    /** A method declaration header, e.g. {@code void foo(int a)} followed by an opening brace. */
    METHOD_DECL,
    /** A call to an existing method, e.g. {@code foo(5);}. */
    METHOD_CALL,
    /** An {@code if} or {@code while} block header. */
    CONDITION,
    /** A {@code return;} statement. */
    RETURN,
    /** A lone closing brace that ends a block. */
    CLOSE_BRACE
}
