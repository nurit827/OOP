package ex5.lines;

import ex5.semantics.SyntaxException;

/**
 * Thrown when a final variable is assigned a value on a line other than its declaration line.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class FinalAssignmentException extends SyntaxException {
	/**
	 * Constructs the exception with an informative message.
	 *
	 * @param message a human-readable description of the final-assignment error
	 */
	public FinalAssignmentException(String message) {
		super(message);
	}
}
