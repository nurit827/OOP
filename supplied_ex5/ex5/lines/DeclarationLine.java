package ex5.lines;

import ex5.semantics.*;

import java.util.List;

public class DeclarationLine implements ParsedLine{
    private final boolean isFinal;
    private Type type;
    private List<Entry> entries;

    public DeclarationLine(boolean isFinal, List<Entry> entries, Type type){
        this.entries = entries;
        this.isFinal = isFinal;
        this.type = type;
    }
    @Override
    public Kind getKind() {
        return Kind.DECLARATION;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public Type getType() {
        return type;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    @Override
    public void checkSemantics(MethodManager methodManager) throws SyntaxException {
        Scope scope = methodManager.getCurrentScope();
        for (Entry entry : entries){
            String name = entry.getName();
            if (!entry.hasValue()){
                if(isFinal){
                    throw new FinalAssignmentException("Final variable '" + name + "' must be initialized when declared.");
                } else {
                    scope.declare(name, new Variable(type, isFinal));
                }
            } else {
                Type vt = Values.typeOf(entry.getValue(), scope);
                if(!type.accepts(vt)){
                    throw new TypeMismatchException( "Cannot assign " + vt + " to '" + name + "' of type " + type + ".");
                }else {
                    Variable variable = new Variable(type, isFinal);
                    variable.markInitialized();
                    scope.declare(name, variable);
                }
            }
        }

    }
}
