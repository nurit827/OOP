package ex5.lines;

import ex5.semantics.SyntaxException;

/**
 * Thrown when a method call refers to a method name that does not exist in the file's method table.
 */
public class UndeclaredMethodException extends SyntaxException {

    /**
     * @param message a human-readable description of the undeclared-method error
     */
    public UndeclaredMethodException(String message) {
        super(message);
    }
}
