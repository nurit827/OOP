package ex5.lines;

import ex5.semantics.*;

import java.util.List;

/**
 * A parsed if/while block header, type checks each boolean condition operand.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class ConditionLine implements ParsedLine {
	private final boolean isWhile;
	private final List<String> tokens;

	/**
	 * Creates a new instance.
	 *
	 * @param isWhile true for a while header, false for an if header
	 * @param tokens  the condition operands
	 */
	public ConditionLine(boolean isWhile, List<String> tokens) {
		this.isWhile = isWhile;
		this.tokens = tokens;
	}

	/**
	 * @return true if this is a while header, false for an if header.
	 */
	public boolean isWhile() {
		return isWhile;
	}

	/**
	 * @return the condition operands
	 */
	public List<String> getTokens() {
		return tokens;
	}

	/**
	 * @return the kind of this line, always CONDITION
	 */
	@Override
	public Kind getKind() {
		return Kind.CONDITION;
	}

	/**
	 * Type checks each operand: each must be a boolean value, or an int/double value or variable
	 * (which boolean accepts).
	 *
	 * @param methodManager the current validation context
	 * @throws SyntaxException if the condition is empty or an operand is not boolean-compatible
	 */
	@Override
	public void checkSemantics(MethodManager methodManager) throws SyntaxException {
		Scope scope = methodManager.getCurrentScope();
		if (tokens.isEmpty()) {
			throw new SyntaxException("empty condition");
		}
		for (String token : tokens) {
			Type operandType = Values.typeOf(token, scope);
			if (!Type.BOOLEAN.accepts(operandType)) {
				throw new TypeMismatchException(
						"Condition operand '" + token + "' is " + operandType + ", not boolean.");
			}
		}
	}
}
