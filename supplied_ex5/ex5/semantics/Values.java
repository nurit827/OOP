package ex5.semantics;

import java.util.regex.Pattern;

/**
 * Resolves the {@link Type} of a raw right-hand-side value string. A value is either a literal
 * (e.g. {@code 5}, {@code "hi"}, {@code true}) or the name of an already-declared, initialized
 * variable. Shared by declaration lines, assignment lines, and method-call argument checking, so
 * the literal-matching and variable-resolution logic lives in one place.
 */
public final class Values {
    private static final Pattern INT     = Pattern.compile("-?\\d+");
    private static final Pattern DOUBLE  = Pattern.compile("-?(\\d+\\.\\d*|\\.\\d+|\\d+\\.)");
    private static final Pattern BOOLEAN = Pattern.compile("true|false");
    private static final Pattern CHAR    = Pattern.compile("'.'");
    private static final Pattern STRING  = Pattern.compile("\"[^\"']*\"");

    private Values() {}

    /**
     * Resolves the type of a raw right-hand-side string: either a literal, or the name of an
     * already-declared, initialized variable visible from {@code scope}.
     *
     * @param raw   the raw value text (a literal or a variable name)
     * @param scope the scope in which a variable reference is resolved
     * @return the {@link Type} of the value
     * @throws UndeclaredVariableException   if {@code raw} is not a literal and names no variable
     *                                       visible from {@code scope}
     * @throws UninitializedVariableException if {@code raw} names a variable that has not been
     *                                       initialized
     */
    public static Type typeOf(String raw, Scope scope)
            throws UndeclaredVariableException, UninitializedVariableException {
        raw = raw.trim();

        if (INT.matcher(raw).matches())     return Type.INT;
        if (DOUBLE.matcher(raw).matches())  return Type.DOUBLE;
        if (BOOLEAN.matcher(raw).matches()) return Type.BOOLEAN;
        if (CHAR.matcher(raw).matches())    return Type.CHAR;
        if (STRING.matcher(raw).matches())  return Type.STRING;

        // Not a literal -> must be a variable reference.
        Variable v = scope.resolve(raw);
        if (!v.isInitialized()) {
            throw new UninitializedVariableException(
                    "Variable '" + raw + "' is used before being initialized.");
        }
        return v.getType();
    }
}
