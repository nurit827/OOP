package ex5.lines;

import ex5.semantics.*;

import java.util.List;

public class AssignmentLine implements ParsedLine {
    private List<Assign> assigns;

    public AssignmentLine(List<Assign> assigns){
        this.assigns=assigns;
    }
    @Override
    public Kind getKind() {
        return Kind.ASSIGNMENT;
    }

    @Override
    public void checkSemantics(MethodManager methodManager) throws SyntaxException {
        Scope scope = methodManager.getCurrentScope();
        for (Assign assign : assigns) {
            String name = assign.getName();
            Variable variable = scope.resolve(name);
            if (variable.isFinal()) {
                throw new FinalAssignmentException("Final variable '" + name + "' can't change it's assignment");
            }
            Type assignRight = Values.typeOf(assign.getValue(), scope);
            Type assignLeft = variable.getType();
            if (!assignLeft.accepts(assignRight)){
                throw new TypeMismatchException( "Cannot assign " + assignRight + " to '" + name + "' of type " + assignLeft + ".");
            } else {
                variable.markInitialized();
            }
        }
    }

    public List<Assign> getAssigns() {
        return assigns;
    }
}
