package ex5.parsing;

import ex5.lines.*;

import ex5.semantics.Kind;
import ex5.semantics.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns each raw source line into a ParsedLine, or rejects it as malformed text.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class Parser {
	/**
	 * Creates a parser.
	 */
	public Parser() {
	}

	private static final String TYPE_KW = "(int|char|String|boolean|double)";
	private static final Pattern METHOD_DECL =
			Pattern.compile("^\\s*void\\s+([a-zA-Z]\\w*)\\s*\\(\\s*(.*?)\\s*\\)\\s*\\{\\s*$");
	private static final String NAME = "([a-zA-Z]\\w*|_[a-zA-Z0-9]\\w*)";
	private static final Pattern NAME_PATTERN = Pattern.compile("^" + NAME + "$");
	private static final String VAR_NAME = "\\s+" + NAME + "\\s*$";
	private static final Pattern PARAM = Pattern.compile("^\\s*(final\\s+)?" + TYPE_KW + VAR_NAME);
	private static final Pattern DECLARATION_PATTERN =
			Pattern.compile("^\\s*(final\\s+)?" + TYPE_KW + "\\s+(.+?)\\s*;\\s*$");
	private static final Pattern METHOD_CALL =
			Pattern.compile("^\\s*([a-zA-Z]\\w*)\\s*\\(\\s*(.*?)\\s*\\)\\s*;\\s*$");
	private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("^\\s*(.+?)\\s*;\\s*$");
	private static final Pattern CONDITION_PATTERN =
			Pattern.compile("^\\s*(if|while)\\s*\\((.*)\\)\\s*\\{\\s*$");

	private static final Pattern FIRST_WORD = Pattern.compile("^([A-Za-z_]\\w*)");
	private static final Pattern RETURN_PATTERN = Pattern.compile("^return\\s*;$");
	private static final Pattern CLOSE_BRACE_PATTERN = Pattern.compile("^}$");

	// a comment line begins with // at column 0
	private boolean filterCommentLine(String line) {
		return line.startsWith("//");
	}

	// returns the leading identifier/keyword of a trimmed line, or "" if it starts otherwise
	private String firstWord(String line) {
		Matcher m = FIRST_WORD.matcher(line);
		return m.find() ? m.group(1) : "";
	}

	// routes a line to its kind by its ending suffix and leading keyword
	private Kind classify(String raw) throws MalformedLineException {
		String line = raw.trim();
		char last = line.charAt(line.length() - 1);
		String first = firstWord(line);
		if (last == '}') {
			if (!CLOSE_BRACE_PATTERN.matcher(line).matches()) {
				throw new MalformedLineException("A '}' must appear alone on its line.");
			}
			return Kind.CLOSE_BRACE;
		}
		if (last == '{') {
			if (first.equals("void")) {
				return Kind.METHOD_DECL;
			}
			if (first.equals("if") || first.equals("while")) {
				return Kind.CONDITION;
			}
			throw new MalformedLineException(
					"A line ends with { but isn't a method declaration nor a condition");
		}
		if (last == ';') {
			if (first.equals("return")) {
				if (!RETURN_PATTERN.matcher(line).matches()) {
					throw new MalformedLineException("Malformed return statement; expected 'return;'.");
				}
				return Kind.RETURN;
			}
			if (first.equals("final") || first.equals("int") || first.equals("char")
					|| first.equals("String") || first.equals("double")
					|| first.equals("boolean")) {
				return Kind.DECLARATION;
			}
			if (line.contains("=")) {
				return Kind.ASSIGNMENT;
			}
			return Kind.METHOD_CALL;
		}
		throw new MalformedLineException("line doesn't match any pattern");
	}

	// parses a declaration line into its final flag, type, and comma-separated entries
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
				String[] parts = entryString.split("=", 2);

				if (parts.length != 2) {
					throw new MalformedLineException("Invalid assignment syntax: " + entryString);
				}
				name = parts[0].trim();
				value = parts[1].trim();
			} else {
				name = entryString.trim();
			}

			if (!NAME_PATTERN.matcher(name).matches()) {
				throw new MalformedLineException("Illegal variable name: " + name);
			}

			entriesList.add(new Entry(name, value));
		}

		return new DeclarationLine(isFinal, entriesList, type);
	}

	// parses an assignment line into its comma-separated name=value pairs
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

			String[] parts = assignString.split("=", 2);

			if (parts.length != 2) {
				throw new MalformedLineException("Invalid assignment syntax: " + assignString);
			}

			String name = parts[0].trim();
			String value = parts[1].trim();

			if (!NAME_PATTERN.matcher(name).matches()) {
				throw new MalformedLineException("Illegal variable name: " + name);
			}

			assignsList.add(new Assign(name, value));
		}

		return new AssignmentLine(assignsList);
	}

	// parses a method declaration header into its name and typed parameters
	private MethodDeclLine parseMethodDecl(String raw) throws MalformedLineException {
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
				boolean isFinal = pm.group(1) != null;
				params.add(new Param(type, pm.group(3), isFinal));
			}
		}
		return new MethodDeclLine(name, params);
	}

	// parses a method call into its name and raw argument strings
	private MethodCallLine parseMethodCall(String raw) throws MalformedLineException {
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

	// parses an if/while header, splitting on &&/|| and storing the operands only
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

	/**
	 * Parses one raw source line into a ParsedLine, or returns null for an empty or comment line.
	 *
	 * @param raw the raw source line
	 * @return the parsed line, or null if the line is empty or a comment
	 * @throws MalformedLineException if the line is not well-formed s-Java
	 */
	public ParsedLine parseLine(String raw) throws MalformedLineException {
		if (raw.trim().isEmpty() || filterCommentLine(raw)) {
			return null;
		}
		Kind kind = classify(raw);
		switch (kind) {
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
