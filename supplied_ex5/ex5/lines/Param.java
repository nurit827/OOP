package ex5.lines;

import ex5.semantics.Type;

/**
 * One parameter in a method declaration header: a type paired with a name, with no value. The
 * parser builds a Param per parameter.
 * @author Nurit Tolkowsky, Mili Green
 */
public class Param {
	private final Type type;
	private final String name;
	private final boolean isFinal;

	/**
	 * Creates a new instance.
	 *
	 * @param type    the parameter's declared type
	 * @param name    the parameter's name
	 * @param isFinal whether the parameter was declared final
	 */
	public Param(Type type, String name, boolean isFinal) {
		this.type = type;
		this.name = name;
		this.isFinal = isFinal;
	}

	/**
	 * @return the parameter's declared type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the parameter's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return true if the parameter was declared final
	 */
	public boolean isFinal() {
		return isFinal;
	}
}
