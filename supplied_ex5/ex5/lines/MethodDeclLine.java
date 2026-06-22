package ex5.lines;

import ex5.semantics.Kind;
import ex5.semantics.MethodManager;
import ex5.semantics.SyntaxException;

import java.util.List;

public class MethodDeclLine implements ParsedLine{
    private final String name;
    private final List<Param> params;
    public MethodDeclLine(String name, List<Param> params) {
        this.name = name;
        this.params = params;
    }

    @Override
    public Kind getKind() {
        return null;
    }

    @Override
    public void checkSemantics(MethodManager methodManager) throws SyntaxException {

    }

    public List<Param> getParams() {
        return params;
    }

    public String getName() {
        return name;
    }
}
