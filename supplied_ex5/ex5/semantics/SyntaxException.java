package ex5.semantics;

/**
 * Base class for every illegal-s-Java-code error. Catching this base type catches all of its
 * subclasses. The driver maps any {@code SyntaxException} to the program's exit value {@code 1}.
 * Each throw site throws the most specific subclass that fits the violation.
 */
public class SyntaxException extends Exception {

    /**
     * Creates a syntax error with an informative message.
     *
     * @param message a human-readable description of the illegality
     */
    public SyntaxException(String message) {
        super(message);
    }
}
