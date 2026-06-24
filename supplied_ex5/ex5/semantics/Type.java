package ex5.semantics;

/**
 * The five value types supported by s-Java. Beyond identifying a type, this enum encodes
 * s-Java's assignment rules via accepts.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public enum Type {
	/** The integer type. */
	INT,
	/** The double type. */
	DOUBLE,
	/** The boolean type. */
	BOOLEAN,
	/** The char type. */
	CHAR,
	/** The String type. */
	STRING;

	/**
	 * Reports whether a value of type other may be assigned to a value of this type. The widening
	 * rules are: double accepts int; boolean accepts int and double; otherwise the two types must
	 * be equal.
	 *
	 * @param other the source type being assigned from
	 * @return true if the assignment is type-compatible
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
	 * Maps a type keyword as it appears in source (e.g. "int", "String") to its type.
	 *
	 * @param keyword the source type keyword
	 * @return the matching type, or null if the keyword is not a known type
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
