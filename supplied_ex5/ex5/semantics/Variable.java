package ex5.semantics;

public class Variable {
    private final Type type;
    private final boolean isFinal;
    private boolean initialized;
    public Variable(Type type, boolean isFinal){
        this.type = type;
        this.isFinal = isFinal;
        this.initialized = false;
    }

    public Type getType() {
        return type;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void markInitialized(){
        initialized = true;
    }
}
