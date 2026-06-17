package ex5.main;

/**
 * Signals an input/output level problem: an illegal number of program arguments, a missing or
 * unreadable file, or a file whose name does not end with the {@code .sjava} suffix. The driver
 * maps this exception to the program's exit value {@code 2}.
 */
public class IOErrorException extends Exception {

    /**
     * Creates an IO error with an informative message.
     *
     * @param message a human-readable description of the IO problem
     */
    public IOErrorException(String message) {
        super(message);
    }
}
