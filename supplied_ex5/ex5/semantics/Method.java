package ex5.semantics;

import java.util.List;

/**
 * A method signature (name and parameter types) that method calls are validated against.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class Method {
	private final String name;
	private final List<Type> paramList;

	/**
	 * Creates a new instance.
	 *
	 * @param name      the method's name
	 * @param paramList the method's parameter types in declaration order
	 */
	public Method(String name, List<Type> paramList) {
		this.name = name;
		this.paramList = paramList;
	}

	/**
	 * @return the method's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the method's parameter types in declaration order
	 */
	public List<Type> getParamList() {
		return paramList;
	}
}
