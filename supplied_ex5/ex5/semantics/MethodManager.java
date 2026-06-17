package ex5.semantics;

import java.util.regex.Pattern;

public class MethodManager {
    private Scope currentScope;
    private final GeneralManager generalManager;

    public MethodManager(GeneralManager generalManager,Scope scope){
        currentScope = scope;
        this.generalManager = generalManager;
    }
    public void enterScope(){
        currentScope = new Scope(currentScope);
    }
    public void exitScope(){
        currentScope = currentScope.getParentScope();
    }

    public GeneralManager getGeneralManager() {
        return generalManager;
    }
    public Scope getCurrentScope(){
        return currentScope;
    }
}
