package ex5.lines;

import ex5.semantics.Type;

/**
 * One parameter in a method declaration header: a type paired with a name, with no value. The
 * parser builds a {@code Param} per parameter; the semantic layer later turns it into both a
 * {@code Type} (for the method signature) and an initialized {@code Variable} (in the method scope).
 */
public class Param {
    private final Type type;
    private final String name;

    /**
     * @param type the parameter's declared type
     * @param name the parameter's name
     */
    public Param(Type type, String name) {
        this.type = type;
        this.name = name;
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
}
