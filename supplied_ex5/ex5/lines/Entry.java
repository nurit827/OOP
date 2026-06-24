package ex5.lines;

/**
 * One declared variable on a declaration line: a name plus an optional raw value string. The
 * value is null when the variable is declared without initialization.
 * @author Nurit Tolkowsky, Mili Green
 */
public class Entry {
	private final String name;
	private final String value;

	/**
	 * Creates a new instance.
	 *
	 * @param name  the declared variable's name
	 * @param value the raw value string, or null if declared without a value
	 */
	public Entry(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the declared variable's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the raw value string, or null if the variable was declared without a value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return true if this entry was declared with a value
	 */
	public boolean hasValue() {
		return value != null;
	}
}
