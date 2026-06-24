package ex5.semantics;

/**
 * Thrown when a declared but not yet initialized variable is used.
 * @author Nurit Tolkowsky, Mili Green
 */
public class UninitializedVariableException extends SyntaxException {
	/**
	 * Constructs the exception with an informative message.
	 *
	 * @param message a human-readable description of the uninitialized-variable error
	 */
	public UninitializedVariableException(String message) {
		super(message);
	}
}
