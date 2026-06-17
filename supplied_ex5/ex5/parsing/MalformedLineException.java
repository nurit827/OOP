package ex5.parsing;

import ex5.semantics.SyntaxException;

/**
 * Thrown by the parser when a line of text is not well-formed s-Java: it matches none of the legal
 * line patterns (bad suffix, illegal name, stray operator, unsupported comment style, etc.). This
 * is the gate that keeps malformed text from ever reaching the semantic layer.
 */
public class MalformedLineException extends SyntaxException {

    /**
     * @param message a human-readable description of the malformed-line error
     */
    public MalformedLineException(String message) {
        super(message);
    }
}
