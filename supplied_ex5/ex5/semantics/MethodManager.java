package ex5.semantics;

import ex5.lines.*;
import ex5.parsing.MalformedLineException;
import ex5.parsing.Parser;

import java.util.List;
import java.util.regex.Pattern;

public class MethodManager {
    private Scope currentScope;
    private final GeneralManager generalManager;

    public MethodManager(GeneralManager generalManager, Scope scope) {
        currentScope = scope;
        this.generalManager = generalManager;
    }

    public void enterScope() {
        currentScope = new Scope(currentScope);
    }

    public void exitScope() {
        currentScope = currentScope.getParentScope();
    }

    public GeneralManager getGeneralManager() {
        return generalManager;
    }

    public Scope getCurrentScope() {
        return currentScope;
    }

    public void verify(List<ParsedLine> parsed) throws SyntaxException {
        for (ParsedLine line : parsed) {
            line.checkSemantics(this);
            Kind kind = line.getKind();
            if (kind == Kind.CONDITION) {
                enterScope();
            }
            if (kind == Kind.CLOSE_BRACE) {
                exitScope();
            }
        }
    }

}
