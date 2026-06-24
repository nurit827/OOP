package ex5.semantics;

/**
 * Thrown when a variable name is declared a second time in the same scope.
 * @author Nurit Tolkowsky, Mili Green
 */
public class DuplicateVariableException extends SyntaxException {
	/**
	 * Constructs the exception with an informative message.
	 *
	 * @param message a human-readable description of the duplicate-variable error
	 */
	public DuplicateVariableException(String message) {
		super(message);
	}
}
