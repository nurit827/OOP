package ex5.lines;

import ex5.semantics.*;

import java.util.List;

/**
 * A parsed method call line.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class MethodCallLine implements ParsedLine {
	private final String methodName;
	private final List<String> args;

	/**
	 * Creates a new instance.
	 *
	 * @param name the called method's name
	 * @param args the raw argument strings (literals or variable names)
	 */
	public MethodCallLine(String name, List<String> args) {
		this.methodName = name;
		this.args = args;
	}

	/**
	 * @return the raw argument strings
	 */
	public List<String> getArgs() {
		return args;
	}

	/**
	 * @return the called method's name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @return the kind of this line, always METHOD_CALL
	 */
	@Override
	public Kind getKind() {
		return Kind.METHOD_CALL;
	}

	/**
	 * Resolves the target method, checks the argument count, then checks each argument's type
	 * against the matching parameter.
	 *
	 * @param methodManager the current validation context
	 * @throws SyntaxException if the method is unknown, the arity is wrong, or an argument's type
	 *                         is incompatible or uninitialized
	 */
	@Override
	public void checkSemantics(MethodManager methodManager) throws SyntaxException {
		GeneralManager generalManager = methodManager.getGeneralManager();
		Method targetMethod = generalManager.getMethod(methodName);
		if (targetMethod == null) {
			throw new UndeclaredMethodException("Method '" + methodName + "' not found.");
		}
		List<Type> expectedParams = targetMethod.getParamList();
		if (args.size() != expectedParams.size()) {
			// method with an incompatible number arguments is illegal
			throw new ArgumentMismatchException(
					"Expected " + expectedParams.size() + " arguments but got " + args.size() + ".");
		}
		for (int i = 0; i < args.size(); i++) {
			String arg = args.get(i);
			Type expectedParamType = expectedParams.get(i);
			Variable variable = methodManager.getCurrentScope().search(arg);
			if (variable != null) {
				// known variable
				if (!variable.isInitialized()) {
					// make sure it is initialized
					throw new UninitializedVariableException(
							"Variable " + variable + " has not been initialized before being used.");
				}
				if (!expectedParamType.accepts(variable.getType())) {
					throw new TypeMismatchException(
							"Cannot accept " + variable.getType() + " as " + expectedParamType + ".");
				}
			} else {
				// not a known variable, so checks variable constants
				Type constantVariableType = Values.typeOf(arg, methodManager.getCurrentScope());
				if (constantVariableType == null) {
					throw new TypeMismatchException("Cannot accept " + arg + " as a valid type.");
				}
				if (!expectedParamType.accepts(constantVariableType)) {
					// wrong type
					throw new TypeMismatchException(
							"Argument '" + arg + "' is not a valid " + expectedParamType + ".");
				}
			}
		}
	}
}
