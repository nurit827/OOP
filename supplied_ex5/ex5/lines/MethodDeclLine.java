package ex5.lines;

import ex5.semantics.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodDeclLine implements ParsedLine{
    private final String name;
    private final List<Param> params;
    public MethodDeclLine(String name, List<Param> params) {
        this.name = name;
        this.params = params;
    }

    @Override
    public Kind getKind() {
        return Kind.METHOD_DECL;
    }

    @Override
    public void checkSemantics(MethodManager methodManager) throws SyntaxException {
        GeneralManager generalManager = methodManager.getGeneralManager();
//        if(generalManager.getMethod(name) != null){
//            throw new DuplicateMethodException("Method "+name+" already exists");
//        }
        Set<String> seenParams = new HashSet<String>();
        for(Param param : params){
            String paramName = param.getName();
            Type paramType = param.getType();
            if (paramType == null) {
                //not a valid type
                throw new TypeMismatchException("Not a valid parameter type");
            }
            if(!seenParams.add(paramName)){
                //duplicated names
                throw new DuplicateMethodException("Duplicate parameter name "+paramName);
            }
        }
    }

    public List<Param> getParams() {
        return params;
    }

    public String getName() {
        return name;
    }
}
