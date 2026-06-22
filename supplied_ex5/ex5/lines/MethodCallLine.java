package ex5.lines;

import ex5.semantics.Kind;
import ex5.semantics.MethodManager;
import ex5.semantics.SyntaxException;

import java.util.List;

public class MethodCallLine implements ParsedLine{
    public MethodCallLine(String name, List<String> args) {
    }

    @Override
    public Kind getKind() {
        return null;
    }

    @Override
    public void checkSemantics(MethodManager methodManager) throws SyntaxException {

    }
}
