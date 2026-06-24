package ex5.semantics;

import ex5.lines.*;
import ex5.parsing.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Driver of the verifier: owns the parser, global scope and method table, and runs both passes.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class GeneralManager {
	private final Scope globalScope;
	private final Map<String, Method> methods;
	private final Parser parser;

	/**
	 * Creates a driver with an empty global scope, an empty method table, and a new parser.
	 */
	public GeneralManager() {
		this.globalScope = new Scope();
		this.methods = new HashMap<>();
		this.parser = new Parser();
	}

	/**
	 * Creates a new instance.
	 *
	 * @param methodName the method name to look up
	 * @return the method with that name, or null if none exists
	 */
	public Method getMethod(String methodName) {
		return methods.get(methodName);
	}

	/**
	 * @return the parser used by this driver
	 */
	public Parser getParser() {
		return parser;
	}

	/**
	 * @return the global scope
	 */
	public Scope getGlobalScope() {
		return globalScope;
	}

	/**
	 * Creates a new instance.
	 *
	 * @param methodName the method name to check
	 * @return true if a method with that name has been registered
	 */
	public boolean methodExists(String methodName) {
		return methods.containsKey(methodName);
	}

	/**
	 * Registers a global declaration line, type-checking initializers and rejecting an
	 * uninitialized final declaration.
	 *
	 * @param declarationLine the global declaration to register
	 * @throws FinalAssignmentException       if a final global has no value
	 * @throws TypeMismatchException          if an initializer's type is not accepted
	 * @throws DuplicateVariableException     if a global name is declared twice
	 * @throws UninitializedVariableException if an initializer reads an uninitialized variable
	 * @throws UndeclaredVariableException    if an initializer reads an undeclared variable
	 */
	public void addGlobal(DeclarationLine declarationLine)
			throws FinalAssignmentException, TypeMismatchException, DuplicateVariableException,
			UninitializedVariableException, UndeclaredVariableException {
		List<Entry> entries = declarationLine.getEntries();
		boolean isFinal = declarationLine.isFinal();
		Type type = declarationLine.getType();

		for (Entry entry : entries) {
			String name = entry.getName();
			if (!entry.hasValue()) {
				if (isFinal) {
					throw new FinalAssignmentException(
							"Final variable '" + name + "' must be initialized when declared.");
				} else {
					globalScope.declare(name, new Variable(type, isFinal));
				}
			} else {
				Type valueType = Values.typeOf(entry.getValue(), globalScope);
				if (!type.accepts(valueType)) {
					throw new TypeMismatchException(
							"Cannot assign " + valueType + " to '" + name + "' of type " + type + ".");
				} else {
					Variable variable = new Variable(type, isFinal);
					variable.markInitialized();
					globalScope.declare(name, variable);
				}
			}
		}
	}

	/**
	 * Registers a global assignment line, rejecting assignment to a final and type-checking the
	 * value.
	 *
	 * @param assignmentLine the global assignment to register
	 * @throws FinalAssignmentException       if a target global is final
	 * @throws TypeMismatchException          if a value's type is not accepted
	 * @throws UndeclaredVariableException    if a target or value names no global
	 * @throws UninitializedVariableException if a value reads an uninitialized variable
	 */
	public void addGlobalAssignment(AssignmentLine assignmentLine)
			throws FinalAssignmentException, TypeMismatchException,
			UndeclaredVariableException, UninitializedVariableException {
		for (Assign assign : assignmentLine.getAssigns()) {
			String name = assign.getName();
			Variable variable = globalScope.resolve(name);
			if (variable.isFinal()) {
				throw new FinalAssignmentException(
						"Final variable '" + name + "' can't be reassigned.");
			}
			Type valueType = Values.typeOf(assign.getValue(), globalScope);
			if (!variable.getType().accepts(valueType)) {
				throw new TypeMismatchException(
						"Cannot assign " + valueType + " to '" + name + "' of type "
								+ variable.getType() + ".");
			}
			variable.markInitialized();
		}
	}

	/**
	 * Registers a method signature, rejecting a duplicate name.
	 *
	 * @param methodDeclLine the method declaration to register
	 * @throws DuplicateMethodException if a method with the same name already exists
	 */
	public void addMethod(MethodDeclLine methodDeclLine) throws DuplicateMethodException {
		String name = methodDeclLine.getName();
		List<Param> params = methodDeclLine.getParams();
		if (methodExists(name)) {
			throw new DuplicateMethodException(name + "method already exists in methods");
		} else {
			List<Type> paramTypes = new ArrayList<>();
			for (Param param : params) {
				paramTypes.add(param.getType());
			}
			methods.put(name, new Method(name, paramTypes));
		}
	}

	/**
	 * Runs both passes over the file: pass 1 parses every line, collects globals and method
	 * signatures, and records each method body's range. pass 2 walks each body.
	 *
	 * @param lines the raw source lines
	 * @throws SyntaxException if any line or cross-line structure is illegal
	 */
	public void verify(List<String> lines) throws SyntaxException {
		// parse once; drop comment/blank lines
		List<ParsedLine> parsed = new ArrayList<>();
		for (String line : lines) {
			ParsedLine pl = parser.parseLine(line);
			if (pl != null) {
				parsed.add(pl);
			}
		}

		// pass 1: collect globals + method signatures, record method ranges
		List<int[]> methodRanges = new ArrayList<>();   // each = {startIdx, endIdx} into 'parsed'
		int depth = 0;
		int methodStart = -1;

		for (int i = 0; i < parsed.size(); i++) {
			Kind kind = parsed.get(i).getKind();

			if (kind == Kind.METHOD_DECL) {
				if (depth != 0) {
					throw new ScopeStructureException("Nested method declaration.");
				}
				addMethod((MethodDeclLine) parsed.get(i));
				methodStart = i;
				depth = 1;
			} else if (kind == Kind.CONDITION) {
				if (depth == 0) {
					throw new ScopeStructureException("Condition outside a method.");
				}
				depth++;
			} else if (kind == Kind.CLOSE_BRACE) {
				depth--;
				if (depth < 0) {
					throw new ScopeStructureException("Unmatched '}'.");
				}
				if (depth == 0) {
					methodRanges.add(new int[]{methodStart, i});
				}
			} else if (kind == Kind.DECLARATION) {
				if (depth == 0) {
					addGlobal((DeclarationLine) parsed.get(i));
				}
				// depth > 0: a local declaration, validated in pass 2
			} else if (kind == Kind.ASSIGNMENT) {
				if (depth == 0) {
					addGlobalAssignment((AssignmentLine) parsed.get(i));
				}
				// depth > 0: a local assignment, validated in pass 2
			} else {
				// method-call / return at file scope is illegal
				if (depth == 0) {
					throw new ScopeStructureException("Statement outside a method.");
				}
			}
		}
		if (depth != 0) {
			throw new ScopeStructureException("Unbalanced braces: missing '}'.");
		}

		// pass 2: walk each method body using a MethodManager
		for (int[] range : methodRanges) {
			int startIdx = range[0];
			int endIdx = range[1];
			// snapshot global init-state so one method's assignments don't leak into the next
			Map<String, Boolean> snapshot = globalScope.snapshotInitialized();
			Scope methodScope = new Scope(globalScope);
			MethodManager mm = new MethodManager(this, methodScope);
			mm.verify(parsed.subList(startIdx, endIdx));
			globalScope.restoreInitialized(snapshot);
		}
	}
}
