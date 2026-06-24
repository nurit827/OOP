package ex5.semantics;

/**
 * Thrown when a method is declared with a name that already belongs to another method.
 * @author Nurit Tolkowsky, Mili Green
 */
public class DuplicateMethodException extends SyntaxException {
	/**
	 * Constructs the exception with an informative message.
	 *
	 * @param message a human-readable description of the duplicate-method error
	 */
	public DuplicateMethodException(String message) {
		super(message);
	}
}
