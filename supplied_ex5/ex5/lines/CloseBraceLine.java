package ex5.lines;

import ex5.semantics.Kind;
import ex5.semantics.MethodManager;
import ex5.semantics.SyntaxException;

public class CloseBraceLine implements ParsedLine{
    @Override
    public Kind getKind() {
        return Kind.CLOSE_BRACE;
    }

    @Override
    public void checkSemantics(MethodManager methodManager) throws SyntaxException {
        return;
    }
}
