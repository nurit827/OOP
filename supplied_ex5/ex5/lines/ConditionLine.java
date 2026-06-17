package ex5.lines;

import ex5.semantics.*;

import java.util.List;

public class ConditionLine implements ParsedLine{
    private boolean isWhile;
    private List<String> tokens;

    public ConditionLine(boolean isWhile, List<String> tokens){
        this.isWhile = isWhile;
        this.tokens = tokens;
    }

    public boolean isWhile() {
        return isWhile;
    }

    public List<String> getTokens() {
        return tokens;
    }

    @Override
    public Kind getKind() {
        return Kind.CONDITION;
    }

    @Override
    public void checkSemantics(MethodManager methodManager) throws SyntaxException {
        Scope scope = methodManager.getCurrentScope();
        if (tokens.isEmpty()){
            throw new SyntaxException("empty condition");
        }
        for (String token : tokens){
            Type t = Values.typeOf(token, scope);
            if (!Type.BOOLEAN.accepts(t)) {
                throw new TypeMismatchException(
                        "Condition operand '" + token + "' is " + t + ", not boolean.");
            }
        }
    }
}
