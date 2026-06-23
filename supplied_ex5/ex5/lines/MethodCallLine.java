package ex5.lines;

import ex5.semantics.*;

import java.util.List;
import java.util.Map;

public class MethodCallLine implements ParsedLine{
    private final String methodName;
    private final List<String> args;
    public MethodCallLine(String name, List<String> args) {
        this.methodName = name;
        this.args = args;
    }

    @Override
    public Kind getKind() {
        return Kind.METHOD_CALL;
    }

    @Override
    public void checkSemantics(MethodManager methodManager) throws SyntaxException {
        GeneralManager generalManager = methodManager.getGeneralManager();
        Method targetMethod = generalManager.getMethod(methodName);
        if (targetMethod == null) {
            throw new UndeclaredMethodException("Method '" + methodName + "' not found.");
        }
        List<Type> expectedParams = targetMethod.getParamList();
        if (args.size() != expectedParams.size()) {
            // method with an incompatible number arguments is illegal
            throw new ArgumentMismatchException("Expected " + expectedParams.size() + " arguments but got " + args.size() + ".");
        }
        for ( int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            Type expectedParamType = expectedParams.get(i);
            Variable variable = methodManager.getCurrentScope().search(arg);
            if (variable != null) {
                //known variable
                if (!variable.isInitialized()) {
                    //make sure it is initialized
                    throw new UninitializedVariableException("Variable " + variable + " has not been initialized before being used.");
                }
                if (!expectedParamType.accepts(variable.getType())) {
                    throw new TypeMismatchException("Cannot accept " + variable.getType() + " as " + expectedParamType + ".");
                }
            }
            else {
                //not a known variable, so checks variable constants
                Type constantVariableType = Values.typeOf(arg,methodManager.getCurrentScope());
                if (constantVariableType == null){
                    throw new TypeMismatchException("Cannot accept " + arg + " as a valid type.");
                }
                if (!expectedParamType.accepts(constantVariableType)) {
                    //wrong type
                    throw new TypeMismatchException("Argument '" + arg + "' is not a valid " + expectedParamType + ".");
                }

            }
        }

    }
}
