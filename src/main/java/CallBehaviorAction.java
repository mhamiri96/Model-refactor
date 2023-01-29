/*
    CallBehaviorAction :
    invokes a behavior directly rather than invoking a behavioral feature that,
    in turn, results in the invocation of that behavior.
*/

// implementation of fUML CallBehaviorAction :

import java.util.Objects;

public class CallBehaviorAction {

    private int numOfEdges;
    private int numOfAction;

    public String classname;
    public String methodLine;

    CallBehaviorAction(String classname, String methodLine) {
        this.classname = classname;
        this.methodLine = methodLine;
        numOfAction = 0;
        numOfEdges = 0;
    }

    public int getEdges() {
        String[] rels = Info.getRelationslist(this.classname);
        String[] methods = Info.getMethodslist(this.classname);
        for (int i = 0; i < methods.length; i++) {
            String method = Info.getNameMethod(methods[i]);
            for (int j = 0; j < rels.length; j++) {
                String temp = rels[j].split(";")[1];
                if (Objects.equals(temp, method)) {
                    numOfEdges++;
                }
            }
        }
        return numOfEdges;
    }

    public int getActions() {
        String[] rels = Info.getRelationslist(this.classname);
        String[] methods = Info.getMethodslist(this.classname);
        for (int i = 0; i < methods.length; i++) {
            String method = Info.getNameMethod(methods[i]);
            for (int j = 0; j < rels.length; j++) {
                String temp = rels[j].split(";")[1];
                if (Objects.equals(temp, method)) {
                    numOfAction++;
                }
            }
        }
        return numOfAction;
    }
}
