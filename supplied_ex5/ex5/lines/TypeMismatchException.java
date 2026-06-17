package ex5.lines;

import ex5.semantics.SyntaxException;

/**
 * Thrown when a value or source variable's type is not accepted by the target type, e.g. assigning
 * a {@code String} into an {@code int}. Widening of {@code int} to {@code double} and of
 * {@code int}/{@code double} to {@code boolean} is allowed and does not raise this exception.
 */
public class TypeMismatchException extends SyntaxException {

    /**
     * @param message a human-readable description of the type-mismatch error
     */
    public TypeMismatchException(String message) {
        super(message);
    }
}
