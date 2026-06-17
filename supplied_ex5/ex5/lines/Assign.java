package ex5.lines;

/**
 * One assignment on an assignment line: a target variable name plus the raw value string assigned
 * to it (e.g. {@code a} and {@code "5"} in {@code a = 5;}). Unlike {@code Entry}, the value is
 * always present. The value is left as raw text here; the semantic layer resolves and type-checks
 * it. Used by {@code AssignmentLine}.
 */
public class Assign {
    private final String name;
    private final String value;

    /**
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
