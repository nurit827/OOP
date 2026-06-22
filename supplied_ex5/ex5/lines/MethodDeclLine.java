package ex5.lines;

import ex5.semantics.Kind;
import ex5.semantics.MethodManager;
import ex5.semantics.SyntaxException;

public class MethodDeclLine implements ParsedLine{
    @Override
    public Kind getKind() {
        return null;
    }

    @Override
    public void checkSemantics(MethodManager methodManager) throws SyntaxException {

    }
}
