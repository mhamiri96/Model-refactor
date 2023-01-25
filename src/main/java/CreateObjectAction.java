/*
    CreateObjectAction :
    is an action that creates an object that conforms to a statically specified classifier
    and puts it on an output pin at runtime. This action instantiates a classifier.
*/

// implementation of fUML CreateObjectAction :

public class CreateObjectAction {

    public String classname;
    private static final int outputEdges = 1;
    private static final int numAction = 1;

    CreateObjectAction(String classname){
        this.classname = classname;
    }

    public int getEdges(){
        return outputEdges;
    }

    public int getAction(){
        return numAction;
    }


}
