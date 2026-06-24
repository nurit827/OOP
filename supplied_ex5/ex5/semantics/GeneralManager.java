package ex5.semantics;

import ex5.lines.*;
import ex5.parsing.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralManager {
    private final Scope globalScope;
    private final Map<String, Method> methods;
    private final Parser parser;

    public GeneralManager() {
        this.globalScope = new Scope();
        this.methods = new HashMap<>();
        this.parser = new Parser();
    }

    public Method getMethod(String methodName) {
        return methods.get(methodName);
    }

    public Parser getParser() {
        return parser;
    }

    public Scope getGlobalScope() {
        return globalScope;
    }

    public boolean methodExists(String methodName){
        return methods.containsKey(methodName);
    }

    public void addGlobal(DeclarationLine declarationLine) throws FinalAssignmentException, TypeMismatchException, DuplicateVariableException, UninitializedVariableException, UndeclaredVariableException {
        List<Entry> entries = declarationLine.getEntries();
        boolean isFinal = declarationLine.isFinal();
        Type type = declarationLine.getType();

        for (Entry entry : entries){
            String name = entry.getName();
            if (!entry.hasValue()){
                if(isFinal){
                    throw new FinalAssignmentException("Final variable '" + name + "' must be initialized when declared.");
                } else {
                    globalScope.declare(name, new Variable(type, isFinal));
                }
            } else {
                Type vt = Values.typeOf(entry.getValue(), globalScope);
                if(!type.accepts(vt)){
                    throw new TypeMismatchException( "Cannot assign " + vt + " to '" + name + "' of type " + type + ".");
                }else {
                    Variable variable = new Variable(type, isFinal);
                    variable.markInitialized();
                    globalScope.declare(name, variable);
                }
            }
        }

    }



    public void addMethod(MethodDeclLine methodDeclLine) throws DuplicateMethodException{
        String name = methodDeclLine.getName();
        List<Param> params= methodDeclLine.getParams();
        if (methodExists(name)){
            throw new DuplicateMethodException(name+ "method already exists in methods");
        } else {
            List<Type> paramTypes = new ArrayList<>();
            for (Param p : params) {
                paramTypes.add(p.getType());
            }
            methods.put(name, new Method(name, paramTypes));
        }
    }

    public void verify(List<String> lines)  throws SyntaxException {
        // parse once; drop comment/blank lines
        List<ParsedLine> parsed = new ArrayList<>();
        for (String line : lines) {
            ParsedLine pl = parser.parseLine(line);
            if (pl != null) {
                parsed.add(pl);
            }
        }

        // pass 1: collect globals + method signatures, record method ranges
        List<int[]> methodRanges = new ArrayList<>();   // each = {startIdx, endIdx} into 'parsed'
        int depth = 0;
        int methodStart = -1;

        for (int i = 0; i < parsed.size(); i++) {
            Kind kind = parsed.get(i).getKind();

            if (kind == Kind.METHOD_DECL) {
                if (depth != 0) {
                    throw new ScopeStructureException("Nested method declaration.");
                }
                addMethod((MethodDeclLine) parsed.get(i));
                methodStart = i;
                depth = 1;
            } else if (kind == Kind.CONDITION) {
                if (depth == 0) {
                    throw new ScopeStructureException("Condition outside a method.");
                }
                depth++;
            } else if (kind == Kind.CLOSE_BRACE) {
                depth--;
                if (depth < 0) {
                    throw new ScopeStructureException("Unmatched '}'.");
                }
                if (depth == 0) {
                    methodRanges.add(new int[]{methodStart, i});
                }
            } else if (kind == Kind.DECLARATION) {
                if (depth == 0) {
                    addGlobal((DeclarationLine) parsed.get(i));
                }
                // depth > 0: a local declaration, validated in pass 2
            } else {
                // assignment / method-call / return at file scope is illegal
                if (depth == 0) {
                    throw new ScopeStructureException("Statement outside a method.");
                }
            }
        }
        if (depth != 0) {
            throw new ScopeStructureException("Unbalanced braces: missing '}'.");
        }

        // pass 2 (to come): walk each range in 'parsed' using a MethodManager
        for (int[] range : methodRanges){
            int startIdx = range[0];
            int endIdx = range[1];
            Scope methodScope = new Scope(globalScope);
            MethodManager mm = new MethodManager(this, methodScope);
            mm.verify(parsed.subList(startIdx, endIdx));
        }

    }
}
