package ex5.semantics;

import ex5.lines.*;

import java.util.List;

/**
 * Validates one method body -  tracks the current scope and hands each line its context.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class MethodManager {
	private Scope currentScope;
	private final GeneralManager generalManager;

	/**
	 * Creates a new instance.
	 *
	 * @param generalManager the file-wide driver, for global and method-table lookups
	 * @param scope          the method's top scope (its parent is the global scope)
	 */
	public MethodManager(GeneralManager generalManager, Scope scope) {
		currentScope = scope;
		this.generalManager = generalManager;
	}

	/**
	 * Pushes a new child scope, making it the current scope.
	 */
	public void enterScope() {
		currentScope = new Scope(currentScope);
	}

	/**
	 * Pops the current scope, returning to its parent.
	 */
	public void exitScope() {
		currentScope = currentScope.getParentScope();
	}

	/**
	 * @return the file-wide driver
	 */
	public GeneralManager getGeneralManager() {
		return generalManager;
	}

	/**
	 * @return the scope currently in effect
	 */
	public Scope getCurrentScope() {
		return currentScope;
	}

	/**
	 * Walks the method body, validating each line and pushing/popping scopes on block
	 * boundaries, then enforces that the body's last statement is a return.
	 *
	 * @param parsed the method body lines (from the method header to its last inner line)
	 * @throws SyntaxException if a line is illegal or the body does not end with a return
	 */
	public void verify(List<ParsedLine> parsed) throws SyntaxException {
		for (ParsedLine line : parsed) {
			line.checkSemantics(this);
			Kind kind = line.getKind();
			if (kind == Kind.CONDITION) {
				enterScope();
			}
			if (kind == Kind.CLOSE_BRACE) {
				exitScope();
			}
		}
		if (parsed.get(parsed.size() - 1).getKind() != Kind.RETURN) {
			throw new ScopeStructureException("Method must end with a 'return;' statement.");
		}
	}
}
