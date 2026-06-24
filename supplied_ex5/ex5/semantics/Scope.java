package ex5.semantics;

import java.util.HashMap;
import java.util.Map;

/**
 * One block's variables plus a link to its enclosing scope.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class Scope {
	private Map<String, Variable> variables = new HashMap<>();
	private Scope parentScope;

	/**
	 * Creates a nested scope.
	 *
	 * @param parentScope the enclosing scope
	 */
	public Scope(Scope parentScope) {
		this.parentScope = parentScope;
	}

	/**
	 * Creates the global (top-level) scope, which has no parent.
	 */
	public Scope() {
		this.parentScope = null;
	}

	/**
	 * @return the enclosing scope, or null for the global scope
	 */
	public Scope getParentScope() {
		return parentScope;
	}

	/**
	 * Declares a variable in this scope.
	 *
	 * @param variableName the name to declare
	 * @param newVariable  the variable record to file under that name
	 * @throws DuplicateVariableException if the name is already declared in this scope
	 */
	public void declare(String variableName, Variable newVariable) throws DuplicateVariableException {
		if (searchLocal(variableName) != null) {
			throw new DuplicateVariableException(
					"Variable '" + variableName + "' is already declared in this scope.");
		}
		variables.put(variableName, newVariable);
	}

	/**
	 * Looks the name up in this scope only.
	 *
	 * @param variableName the name to look up
	 * @return the variable, or null if not declared in this scope
	 */
	public Variable searchLocal(String variableName) {
		if (variables.containsKey(variableName)) {
			return variables.get(variableName);
		}
		return null;
	}

	/**
	 * Looks the name up in the enclosing scopes, from the immediate parent up to the global scope.
	 *
	 * @param variableName the name to look up
	 * @return the variable, or null if not declared in any enclosing scope
	 */
	public Variable searchParent(String variableName) {
		Scope curScope = getParentScope();
		while (curScope != null) {
			Variable curVariable = curScope.searchLocal(variableName);
			if (curVariable != null) {
				return curVariable;
			}
			curScope = curScope.getParentScope();
		}
		return null;
	}

	/**
	 * Looks the name up in this scope, then in the enclosing scopes.
	 *
	 * @param variableName the name to look up
	 * @return the variable, or null if not visible anywhere in the chain
	 */
	public Variable search(String variableName) {
		Variable local = searchLocal(variableName);
		if (local != null) {
			return local;
		}
		return searchParent(variableName);
	}

	/**
	 * Captures the initialized state of every variable in this scope.
	 *
	 * @return a map from variable name to its current initialized state
	 */
	public Map<String, Boolean> snapshotInitialized() {
		Map<String, Boolean> snapshot = new HashMap<>();
		for (Map.Entry<String, Variable> entry : variables.entrySet()) {
			snapshot.put(entry.getKey(), entry.getValue().isInitialized());
		}
		return snapshot;
	}

	/**
	 * Restores the initialized state of this scope's variables from a snapshot.
	 *
	 * @param snapshot a map from variable name to the initialized state to restore
	 */
	public void restoreInitialized(Map<String, Boolean> snapshot) {
		for (Map.Entry<String, Boolean> entry : snapshot.entrySet()) {
			variables.get(entry.getKey()).setInitialized(entry.getValue());
		}
	}

	/**
	 * Resolves a name to its visible variable, searching this scope then the enclosing ones.
	 *
	 * @param variableName the name to resolve
	 * @return the visible variable
	 * @throws UndeclaredVariableException if the name is not visible anywhere in the chain
	 */
	public Variable resolve(String variableName) throws UndeclaredVariableException {
		Variable variable = search(variableName);
		if (variable != null) {
			return variable;
		}
		throw new UndeclaredVariableException(
				"Variable '" + variableName + "' is not declared in any scope.");
	}
}
