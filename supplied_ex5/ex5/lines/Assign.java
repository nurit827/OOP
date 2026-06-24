package ex5.lines;

/**
 * One assignment on an assignment line.
 * @author Nurit Tolkowsky, Mili Green
 */
public class Assign {
	private final String name;
	private final String value;

	/**
	 * Creates a new instance.
	 *
	 * @param name  the target variable's name
	 * @param value the raw value string assigned to it
	 */
	public Assign(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the target variable's name
	 */
	public String getName() {
		return name;
	}

	/**

	 * @return the raw value string assigned to the variable
	 */
	public String getValue() {
		return value;
	}
}
