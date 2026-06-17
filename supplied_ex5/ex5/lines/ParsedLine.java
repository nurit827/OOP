package ex5.lines;

import ex5.semantics.Kind;
import ex5.semantics.MethodManager;
import ex5.semantics.SyntaxException;

public interface ParsedLine {
    public Kind getKind();
    public void checkSemantics(MethodManager methodManager) throws SyntaxException;
}
