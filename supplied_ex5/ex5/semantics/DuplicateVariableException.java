package ex5.semantics;

/**
 * Thrown when a variable name is declared a second time in the same scope (a re-declaration),
 * including a local variable clashing with a method parameter in the method's top scope.
 */
public class DuplicateVariableException extends SyntaxException {

    /**
     * @param message a human-readable description of the duplicate-variable error
     */
    public DuplicateVariableException(String message) {
        super(message);
    }
}
