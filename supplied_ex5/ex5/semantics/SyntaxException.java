package ex5.semantics;

/**
 * Base class for every illegal-s-Java-code error.
 * @author Nurit Tolkowsky, Mili Green
 */
public class SyntaxException extends Exception {
	/**
	 * Creates a syntax error with an informative message.
	 *
	 * @param message a human-readable description of the illegality
	 */
	public SyntaxException(String message) {
		super(message);
	}
}
