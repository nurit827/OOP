package ex5.lines;

import ex5.semantics.Kind;
import ex5.semantics.MethodManager;

/**
 * A parsed lone closing brace lin.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class CloseBraceLine implements ParsedLine {
	/**
	 * Creates a close-brace line.
	 */
	public CloseBraceLine() {
	}

	/**
	 * @return the kind of this line, always CLOSE_BRACE
	 */
	@Override
	public Kind getKind() {
		return Kind.CLOSE_BRACE;
	}

	/**
	 * A closing brace carries nothing to validate on its own; the driver pops the current scope.
	 *
	 * @param methodManager the current validation context (unused)
	 */
	@Override
	public void checkSemantics(MethodManager methodManager) {
		// nothing to validate for a lone closing brace
	}
}
