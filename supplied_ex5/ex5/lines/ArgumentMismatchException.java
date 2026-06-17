package ex5.lines;

import ex5.semantics.SyntaxException;

/**
 * Thrown when a method call supplies the wrong number of arguments, or an argument whose type is
 * not compatible with the corresponding declared parameter type.
 */
public class ArgumentMismatchException extends SyntaxException {

    /**
     * @param message a human-readable description of the argument-mismatch error
     */
    public ArgumentMismatchException(String message) {
        super(message);
    }
}
