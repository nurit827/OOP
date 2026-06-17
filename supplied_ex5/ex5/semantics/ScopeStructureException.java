package ex5.semantics;

/**
 * Thrown for cross-line structural errors that no single line can detect on its own: an
 * unbalanced brace (a closing brace without a matching opening one, or a method/block left open),
 * or a {@code return} statement that is not the last statement in its method body.
 */
public class ScopeStructureException extends SyntaxException {

    /**
     * @param message a human-readable description of the scope-structure error
     */
    public ScopeStructureException(String message) {
        super(message);
    }
}
