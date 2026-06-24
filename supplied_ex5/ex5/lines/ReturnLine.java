package ex5.lines;

import ex5.semantics.Kind;
import ex5.semantics.MethodManager;
import ex5.semantics.SyntaxException;

/**
 * A parsed return statement.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class ReturnLine implements ParsedLine {
	/**
	 * Creates a return line.
	 */
	public ReturnLine() {
	}

	/**
	 * @return the kind of this line, always RETURN
	 */
	@Override
	public Kind getKind() {
		return Kind.RETURN;
	}

	/**
	 * A return statement carries nothing to validate on its own
	 *
	 * @param methodManager the current validation context (unused)
	 */
	@Override
	public void checkSemantics(MethodManager methodManager) {
		// nothing to validate for a bare return statement
	}
}
