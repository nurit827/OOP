package ex5.semantics;

/**
 * Thrown when a method is declared with a name that already belongs to another method. s-Java has
 * a single flat method namespace and does not support overloading.
 */
public class DuplicateMethodException extends SyntaxException {

    /**
     * @param message a human-readable description of the duplicate-method error
     */
    public DuplicateMethodException(String message) {
        super(message);
    }
}
