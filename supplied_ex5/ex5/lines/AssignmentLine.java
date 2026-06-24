package ex5.lines;

import ex5.semantics.*;

import java.util.List;

/**
 * A parsed variable assignment line that validates each assignment against the current scope.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class AssignmentLine implements ParsedLine {
	private final List<Assign> assigns;

	/**
	 * Creates a new instance.
	 *
	 * @param assigns the assignments on this line
	 */
	public AssignmentLine(List<Assign> assigns) {
		this.assigns = assigns;
	}

	/**
	 * @return the kind of this line, always ASSIGNMENT
	 */
	@Override
	public Kind getKind() {
		return Kind.ASSIGNMENT;
	}

	/**
	 * @return the assignments of this line
	 */
	public List<Assign> getAssigns() {
		return assigns;
	}

	/**
	 * Resolves each target, rejects assignment to a final, type-checks the value, then marks the
	 * target initialized.
	 *
	 * @param methodManager the current validation context
	 * @throws SyntaxException if a target is undeclared or final, or a value type mismatches
	 */
	@Override
	public void checkSemantics(MethodManager methodManager) throws SyntaxException {
		Scope scope = methodManager.getCurrentScope();
		for (Assign assign : assigns) {
			String name = assign.getName();
			Variable variable = scope.resolve(name);
			if (variable.isFinal()) {
				throw new FinalAssignmentException(
						"Final variable '" + name + "' can't change it's assignment");
			}
			Type assignRight = Values.typeOf(assign.getValue(), scope);
			Type assignLeft = variable.getType();
			if (!assignLeft.accepts(assignRight)) {
				throw new TypeMismatchException(
						"Cannot assign " + assignRight + " to '" + name + "' of type " + assignLeft + ".");
			} else {
				variable.markInitialized();
			}
		}
	}
}
