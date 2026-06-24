package ex5.lines;

import ex5.semantics.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A parsed method-declaration header.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class MethodDeclLine implements ParsedLine {
	private final String name;
	private final List<Param> params;

	/**
	 * Creates a new instance.
	 *
	 * @param name   the method's name
	 * @param params the method's parameters in declaration order
	 */
	public MethodDeclLine(String name, List<Param> params) {
		this.name = name;
		this.params = params;
	}

	/**
	 * @return the kind of this line, always METHOD_DECL
	 */
	@Override
	public Kind getKind() {
		return Kind.METHOD_DECL;
	}

	/**
	 * @return the method's parameters
	 */
	public List<Param> getParams() {
		return params;
	}

	/**
	 * @return the method's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Declares each parameter into the current (method top) scope as an already-initialized
	 * variable, rejecting duplicate parameter names.
	 *
	 * @param methodManager the current validation context
	 * @throws SyntaxException if two parameters share a name
	 */
	@Override
	public void checkSemantics(MethodManager methodManager) throws SyntaxException {
		Scope currentScope = methodManager.getCurrentScope();
		Set<String> seenParams = new HashSet<>();
		for (Param param : params) {
			String paramName = param.getName();
			Type paramType = param.getType();
			if (paramType == null) {
				// not a valid type
				throw new TypeMismatchException("Not a valid parameter type");
			}
			if (!seenParams.add(paramName)) {
				// duplicated names
				throw new DuplicateVariableException("Duplicate parameter name " + paramName);
			}
			Variable paramVar = new Variable(paramType, param.isFinal());
			paramVar.markInitialized();
			currentScope.declare(paramName, paramVar);
		}
	}
}
