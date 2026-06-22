package ex5.parsing;

import ex5.lines.*;
import ex5.semantics.
import ex5.semantics.Kind;

public class Parser {

    private boolean filterCommentLine(String line){
        return line.startsWith("//");
    }

    private Kind classify(String raw) throws MalformedLineException{
        String line = raw.trim();
        char last = line.charAt(line.length()-1);
        if (last=='}'){
            return Kind.CLOSE_BRACE;
        }
        if (last=='{') {
            if (line.startsWith("void")) {
                return Kind.METHOD_DECL;
            }
            if (line.startsWith("if") || line.startsWith("while")) {
                return Kind.CONDITION;
                throw new MalformedLineException("A line ends with { but isn't a method declaration nor a condition");
            }
        }
        if (last==';'){
            if(line.startsWith("return")){
                return Kind.RETURN;
            }
            if(line.startsWith("final")||line.startsWith("int")||line.startsWith("char")||line.startsWith("final"))
        }
    }

    private DeclarationLine parseDeclaration(String raw) throws MalformedLineException{

    }
    private AssignmentLine parseAssignment(String raw)  throws MalformedLineException{

    }
    private MethodDeclLine  parseMethodDecl(String raw)  throws MalformedLineException{

    }
    private MethodCallLine parseMethodCall(String raw)  throws MalformedLineException{

    }
    private ConditionLine parseCondition(String raw, boolean isWhile) throws MalformedLineException{

    }


}
