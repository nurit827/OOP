package ex5.lines;

import ex5.semantics.SyntaxException;

/**
 * Thrown when a value or source variable's type is not accepted by the target type.
 * @author Nurit Tolkowsky, Mili Green
 */
public class TypeMismatchException extends SyntaxException {
	/**
	 * Constructs the exception with an informative message.
	 *
	 * @param message a human-readable description of the type-mismatch error
	 */
	public TypeMismatchException(String message) {
		super(message);
	}
}
