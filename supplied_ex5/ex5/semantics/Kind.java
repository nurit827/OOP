package ex5.semantics;

/**
 * Identifies what kind of statement a parsed line is.
 * @author Nurit Tolkowsky, Mili Green
 */
public enum Kind {
	/** A variable declaration line, e.g. int a = 5, b;. */
	DECLARATION,
	/** A variable assignment line, e.g. a = 5;. */
	ASSIGNMENT,
	/** A method declaration header, e.g. void foo(int a) followed by an opening brace. */
	METHOD_DECL,
	/** A call to an existing method, e.g. foo(5);. */
	METHOD_CALL,
	/** An if or while block header. */
	CONDITION,
	/** A return; statement. */
	RETURN,
	/** A lone closing brace that ends a block. */
	CLOSE_BRACE
}
