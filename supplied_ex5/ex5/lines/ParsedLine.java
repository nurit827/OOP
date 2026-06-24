package ex5.lines;

import ex5.semantics.Kind;
import ex5.semantics.MethodManager;
import ex5.semantics.SyntaxException;

/**
 * A single parsed s-Java line that knows its kind and how to validate itself against the
 * current context.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public interface ParsedLine {
	/**
	 * @return the kind of this line, used by the driver for cross-line tracking
	 */
	Kind getKind();

	/**
	 * Validates this line against the current symbol-table state, reached through the context.
	 *
	 * @param methodManager the context giving access to the current scope and the method table
	 * @throws SyntaxException if this line is semantically illegal
	 */
	void checkSemantics(MethodManager methodManager) throws SyntaxException;
}
