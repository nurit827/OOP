package ex5.lines;

import ex5.semantics.SyntaxException;

/**
 * Thrown when a {@code final} variable is assigned a value on a line other than its declaration
 * line. Final variables are constants and may only be initialized at declaration time.
 */
public class FinalAssignmentException extends SyntaxException {

    /**
     * @param message a human-readable description of the final-assignment error
     */
    public FinalAssignmentException(String message) {
        super(message);
    }
}
