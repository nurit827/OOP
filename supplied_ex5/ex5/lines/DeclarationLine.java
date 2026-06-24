package ex5.lines;

import ex5.semantics.*;

import java.util.List;

/**
 * A parsed variable-declaration line; declares each variable and type-checks any initializers.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class DeclarationLine implements ParsedLine {
	private final boolean isFinal;
	private final Type type;
	private final List<Entry> entries;

	/**
	 * Creates a new instance.
	 *
	 * @param isFinal whether the line carries the final modifier (applies to all entries)
	 * @param entries the declared variables, each a name plus an optional value
	 * @param type    the declared type shared by all entries
	 */
	public DeclarationLine(boolean isFinal, List<Entry> entries, Type type) {
		this.entries = entries;
		this.isFinal = isFinal;
		this.type = type;
	}

	/**
	 * @return the kind of this line, always DECLARATION
	 */
	@Override
	public Kind getKind() {
		return Kind.DECLARATION;
	}

	/**
	 * @return whether this declaration is final
	 */
	public boolean isFinal() {
		return isFinal;
	}

	/**
	 * @return the declared type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the declared variables of this line
	 */
	public List<Entry> getEntries() {
		return entries;
	}

	/**
	 * Declares each entry into the current scope, type checking any initializer and rejecting an
	 * uninitialized final declaration.
	 *
	 * @param methodManager the current validation context
	 * @throws SyntaxException if a name clashes, a final has no value, or a value type mismatches
	 */
	@Override
	public void checkSemantics(MethodManager methodManager) throws SyntaxException {
		Scope scope = methodManager.getCurrentScope();
		for (Entry entry : entries) {
			String name = entry.getName();
			if (!entry.hasValue()) {
				if (isFinal) {
					throw new FinalAssignmentException(
							"Final variable '" + name + "' must be initialized when declared.");
				} else {
					scope.declare(name, new Variable(type, isFinal));
				}
			} else {
				Type valueType = Values.typeOf(entry.getValue(), scope);
				if (!type.accepts(valueType)) {
					throw new TypeMismatchException(
							"Cannot assign " + valueType + " to '" + name + "' of type " + type + ".");
				} else {
					Variable variable = new Variable(type, isFinal);
					variable.markInitialized();
					scope.declare(name, variable);
				}
			}
		}
	}
}
