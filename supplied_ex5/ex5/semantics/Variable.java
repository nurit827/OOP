package ex5.semantics;

/**
 * A declared variable's facts: its type, whether it is final, and whether it has been initialized.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class Variable {
	private final Type type;
	private final boolean isFinal;
	private boolean initialized;

	/**
	 * Creates a variable record. A new variable starts uninitialized.
	 *
	 * @param type    the variable's declared type
	 * @param isFinal whether the variable was declared final
	 */
	public Variable(Type type, boolean isFinal) {
		this.type = type;
		this.isFinal = isFinal;
		this.initialized = false;
	}

	/**
	 * @return the variable's declared type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return true if the variable was declared final
	 */
	public boolean isFinal() {
		return isFinal;
	}

	/**
	 * @return true if the variable has been assigned a value
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Marks the variable as initialized.
	 */
	public void markInitialized() {
		initialized = true;
	}

	/**
	 * Sets the variable's initialized state directly. Used to snapshot and restore the
	 * global scope's initialization state between method bodies.
	 *
	 * @param initialized the initialized state to set
	 */
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
}
