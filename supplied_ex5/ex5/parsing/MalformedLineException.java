package ex5.parsing;

import ex5.semantics.SyntaxException;

/**
 * Thrown by the parser when a line of text is not well-formed s-Java: it matches none of the
 * legal line patterns.
 * @author Nurit Tolkowsky, Mili Green
 */
public class MalformedLineException extends SyntaxException {
	/**
	 * Constructs the exception with an informative message.
	 *
	 * @param message a human-readable description of the malformed-line error
	 */
	public MalformedLineException(String message) {
		super(message);
	}
}
