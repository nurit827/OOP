package ex5.semantics;

import java.util.List;

public class Method {
    private final String name;
    private final List<Type> paramList;

    public Method(String name, List<Type> paramList){
        this.name = name;
        this.paramList = paramList;
    }

    public String getName(){
        return name;
    }

    public List<Type> getParamList() {
        return paramList;
    }

    public boolean match(List<Type> paramCompare){
        if (paramCompare.size()!=paramList.size()){
            return false;
        }
        for (int i=0; i<paramCompare.size(); i++){
            if (!paramList.get(i).accepts(paramCompare.get(i))){
                return false;
            }
        }
        return true;
    }
}
