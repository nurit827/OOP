package ex5.semantics;

/**
 * Thrown when code references a variable name that is not visible anywhere in the current scope
 * chain (the name was never declared in this scope or any enclosing one).
 */
public class UndeclaredVariableException extends SyntaxException {

    /**
     * @param message a human-readable description of the undeclared-variable error
     */
    public UndeclaredVariableException(String message) {
        super(message);
    }
}
