package ex5.semantics;

/**
 * Thrown for cross-line structural errors that no single line can detect on its own.
 * @author Nurit Tolkowsky, Mili Green
 */
public class ScopeStructureException extends SyntaxException {
	/**
	 * Constructs the exception with an informative message.
	 *
	 * @param message a human-readable description of the scope-structure error
	 */
	public ScopeStructureException(String message) {
		super(message);
	}
}
