package ex5.lines;

import ex5.semantics.Kind;
import ex5.semantics.MethodManager;
import ex5.semantics.SyntaxException;

public class ReturnLine implements ParsedLine {
    @Override
    public Kind getKind() {
        return Kind.RETURN;
    }

    @Override
    public void checkSemantics(MethodManager methodManager) throws SyntaxException {
        return;
    }
}
