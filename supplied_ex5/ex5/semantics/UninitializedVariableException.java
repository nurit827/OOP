package ex5.semantics;

/**
 * Thrown when a declared-but-not-yet-initialized variable is used (read in an assignment value,
 * a method-call argument, or a condition) before it has been assigned a value.
 */
public class UninitializedVariableException extends SyntaxException {

    /**
     * @param message a human-readable description of the uninitialized-variable error
     */
    public UninitializedVariableException(String message) {
        super(message);
    }
}
