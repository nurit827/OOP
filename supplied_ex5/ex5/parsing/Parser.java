package ex5.parsing;

import ex5.lines.*;

import ex5.semantics.Kind;
import ex5.semantics.SyntaxException;
import ex5.semantics.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static final String TYPE_KW = "(int|char|String|boolean|double)";
    private static final Pattern Declaration =
            Pattern.compile("^\\s*(final\\s+)?" + TYPE_KW + "\\s+(.+?)\\s*;\\s*$");
    private static final Pattern METHOD_DECL = Pattern.compile("^\\s*void\\s+([a-zA-Z]\\w*)\\s*\\(\\s*(.*?)\\s*\\)\\s*\\{\\s*$");
    private static final String VAR_NAME = "\\s+([a-zA-Z]\\w*|_[a-zA-Z0-9]\\w*)\\s*$";
    private static final Pattern PARAM = Pattern.compile("^\\s*(final\\s+)?" + TYPE_KW + VAR_NAME);
    private static final Pattern DECLARATION_PATTERN = Pattern.compile("^\\s*(final\\s+)?" + TYPE_KW + "\\s+(.+?)\\s*;\\s*$");
    private static final Pattern METHOD_CALL = Pattern.compile("^\\s*([a-zA-Z]\\w*)\\s*\\(\\s*(.*?)\\s*\\)\\s*;\\s*$");
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("^\\s*(.+?)\\s*;\\s*$");
    private static final Pattern CONDITION_PATTERN = Pattern.compile("^\\s*(if|while)\\s*\\((.*)\\)\\s*\\{\\s*$");


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
            }
            throw new MalformedLineException("A line ends with { but isn't a method declaration nor a condition");
        }
        if (last==';'){
            if(line.startsWith("return")){
                return Kind.RETURN;
            }
            if(line.startsWith("final")||line.startsWith("int")||line.startsWith("char")||line.startsWith("String")||line.startsWith("double")||line.startsWith("boolean")){
                return Kind.DECLARATION;
            }
            if (line.contains("=")){
                return Kind.ASSIGNMENT;
            }
            return Kind.METHOD_CALL;
        }
        throw new MalformedLineException("line doesn't match any pattern");
    }


    private DeclarationLine parseDeclaration(String line) throws MalformedLineException {
        Matcher matcher = DECLARATION_PATTERN.matcher(line);

        if (!matcher.matches()) {
            throw new MalformedLineException("Invalid declaration syntax.");
        }

        boolean isFinal = matcher.group(1) != null;
        Type type = Type.fromKeyword(matcher.group(2));

        String entriesRaw = matcher.group(3);
        String[] entries = entriesRaw.split(",");
        List<Entry> entriesList = new ArrayList<>();

        for (String entryString : entries) {
            String name;
            String value = null;

            if (entryString.contains("=")) {
                String[] parts = entryString.split("=");

                if (parts.length != 2) {
                    throw new MalformedLineException("Invalid assignment syntax: " + entryString);
                }
                name = parts[0].trim();
                value = parts[1].trim();
            } else {
                name = entryString.trim();
            }

            if (!name.matches(VAR_NAME)) {
                throw new MalformedLineException("Illegal variable name: " + name);
            }

            entriesList.add(new Entry(name, value));
        }

        return new DeclarationLine(isFinal, entriesList, type);
    }
    private AssignmentLine parseAssignment(String line) throws MalformedLineException {
        Matcher matcher = ASSIGNMENT_PATTERN.matcher(line);

        if (!matcher.matches()) {
            throw new MalformedLineException("Invalid assignment syntax.");
        }

        String assignsRaw = matcher.group(1);
        String[] assigns = assignsRaw.split(",");
        List<Assign> assignsList = new ArrayList<>();

        for (String assignString : assigns) {
            if (!assignString.contains("=")) {
                throw new MalformedLineException("Assignment is missing '=': " + assignString);
            }

            String[] parts = assignString.split("=");

            if (parts.length != 2) {
                throw new MalformedLineException("Invalid assignment syntax: " + assignString);
            }

            String name = parts[0].trim();
            String value = parts[1].trim();

            if (!name.matches(VAR_NAME)) {
                throw new MalformedLineException("Illegal variable name: " + name);
            }

            assignsList.add(new Assign(name, value));
        }

        return new AssignmentLine(assignsList);
    }
    private MethodDeclLine parseMethodDecl(String raw)  throws MalformedLineException{
        Matcher m = METHOD_DECL.matcher(raw);
        if (!m.matches()) {
            throw new MalformedLineException("Malformed method declaration: " + raw);
        }
        String name = m.group(1);
        String paramText = m.group(2).trim();

        List<Param> params = new ArrayList<>();
        if (!paramText.isEmpty()) {
            for (String piece : paramText.split(",")) {
                Matcher pm = PARAM.matcher(piece);
                if (!pm.matches()) {
                    throw new MalformedLineException("Malformed parameter: '" + piece + "'");
                }
                Type type = Type.fromKeyword(pm.group(2));
                params.add(new Param(type, pm.group(3)));
            }
        }
        return new MethodDeclLine(name, params);
    }
    private MethodCallLine parseMethodCall(String raw)  throws MalformedLineException{
        Matcher m = METHOD_CALL.matcher(raw);
        if (!m.matches()) {
            throw new MalformedLineException("Malformed method call: " + raw);
        }
        String name = m.group(1);
        String argsText = m.group(2).trim();

        List<String> args = new ArrayList<>();
        if (!argsText.isEmpty()) {
            for (String piece : argsText.split(",", -1)) {
                String arg = piece.trim();
                if (arg.isEmpty()) {
                    throw new MalformedLineException("Empty argument in method call: " + raw);
                }
                args.add(arg);
            }
        }
        return new MethodCallLine(name, args);
    }
    private ConditionLine parseCondition(String line) throws MalformedLineException {
        Matcher matcher = CONDITION_PATTERN.matcher(line);
        if (!matcher.matches()) {
            throw new MalformedLineException("Invalid condition syntax.");
        }

        boolean isWhile = matcher.group(1).equals("while");
        String conditionRaw = matcher.group(2);

        String[] rawTokens = conditionRaw.split("&&|\\|\\|", -1);
        List<String> tokensList = new ArrayList<>();

        for (String token : rawTokens) {
            String cleanToken = token.trim();

            if (cleanToken.isEmpty()) {
                throw new MalformedLineException("Invalid operator placement or empty condition.");
            }

            tokensList.add(cleanToken);
        }

        return new ConditionLine(isWhile, tokensList);
    }

    public ParsedLine parseLine(String raw) throws MalformedLineException{
        if (raw.trim().isEmpty() || filterCommentLine(raw)){
            return null;
        }
        Kind kind  = classify(raw);
        switch (kind){
            case DECLARATION:
                return parseDeclaration(raw);
            case ASSIGNMENT:
                return parseAssignment(raw);
            case METHOD_DECL:
                return parseMethodDecl(raw);
            case METHOD_CALL:
                return parseMethodCall(raw);
            case CONDITION:
                return parseCondition(raw);
            case RETURN:
                return new ReturnLine();
            case CLOSE_BRACE:
                return new CloseBraceLine();
            default:
                throw new MalformedLineException("Unknown line kind: " + raw);
        }
    }


}
