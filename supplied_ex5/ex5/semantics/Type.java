package ex5.semantics;

/**
 * The five value types supported by s-Java. Beyond identifying a type, this enum encodes s-Java's
 * assignment-compatibility (widening) rules via {@link #accepts(Type)}.
 */
public enum Type {
    INT,
    DOUBLE,
    BOOLEAN,
    CHAR,
    STRING;

    /**
     * Reports whether a value of type {@code other} may be assigned to (or passed where the code
     * expects) a value of this type. The widening rules are: {@code double} accepts {@code int};
     * {@code boolean} accepts {@code int} and {@code double}; otherwise the two types must be equal.
     *
     * @param other the source type being assigned from
     * @return {@code true} if the assignment is type-compatible
     */
    public boolean accepts(Type other) {
        if (this == other) {
            return true;
        }
        if (this == DOUBLE && other == INT) {
            return true;
        }
        return this == BOOLEAN && (other == INT || other == DOUBLE);
    }

    /**
     * Maps a type keyword as it appears in source (e.g. {@code "int"}, {@code "String"}) to its
     * {@link Type}.
     *
     * @param keyword the source type keyword
     * @return the matching {@link Type}, or {@code null} if the keyword is not a known type
     */
    public static Type fromKeyword(String keyword) {
        return switch (keyword) {
            case "int" -> INT;
            case "double" -> DOUBLE;
            case "boolean" -> BOOLEAN;
            case "char" -> CHAR;
            case "String" -> STRING;
            default -> null;
        };
    }
}
