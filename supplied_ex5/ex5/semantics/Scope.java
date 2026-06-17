package ex5.semantics;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private Map<String,Variable> variables = new HashMap<>();
    private Scope parentScope;
    public Scope(Scope parentScope){
        this.parentScope = parentScope;
    }

    public Scope(){
        this.parentScope = null;
    }

    public Scope getParentScope(){
        return parentScope;
    }
    public void declare(String variableName , Variable newVariable) throws DuplicateVariableException {
        if (searchLocal(variableName) != null){
            throw new DuplicateVariableException(
                    "Variable '" + variableName + "' is already declared in this scope.");
        }
        variables.put(variableName,newVariable);
    }
    public Variable searchLocal(String variableName){
        if (variables.containsKey(variableName)){
            return variables.get(variableName);
        }
        return null;
    }
    public Variable searchParent(String variableName){
        Scope curScope = getParentScope();
        while (curScope!= null){

            Variable curVariable = curScope.searchLocal(variableName);
            if (curVariable!=null) {
                return curVariable;
            }
            curScope = curScope.getParentScope();
        }
        return null;
    }

    public Variable resolve(String variableName) throws UndeclaredVariableException {
        Variable local = searchLocal(variableName);
        if (local != null){
            return local;
        }
        Variable parentVar = searchParent(variableName);
        if (parentVar != null){
            return parentVar;
        }
        throw new UndeclaredVariableException(
                "Variable '" + variableName + "' is not declared in any scope.");
    }

}
