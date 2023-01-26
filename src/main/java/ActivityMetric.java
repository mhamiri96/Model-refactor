public class ActivityMetric {

    // number of parameters:
    public static int NP(String className, String methodLine){
        String method = Info.getNameMethod(methodLine);
        return Info.getNbrParameters(className , method);
    }

    // number of edges :
    public static int NED(String className, String method){
        int edgesOfCOA = new CreateObjectAction(className).getEdges();
        int edgesOfRSFA = new ReadStructuralFeatureAction(className , method).getEdges();
        int edgesOfCOpA = new CallOperationAction(className , method).getEdges();
        int edgesOfCBA = new CallBehaviorAction(className , method).getEdges();
        return edgesOfCOA + edgesOfRSFA + edgesOfCOpA + edgesOfCBA;
    }

    // number of actions :
    public static int NAC(String className, String method){
        int actionsOfCOA = new CreateObjectAction(className).getAction();
        int actionsOfRSFA = new ReadStructuralFeatureAction(className , method).getActions();
        int actionsOfCOpA = new CallOperationAction(className , method).getActions();
        int actionsOfCBA = new CallBehaviorAction(className , method).getActions();
        return actionsOfCOA + actionsOfRSFA + actionsOfCOpA + actionsOfCBA;
    }

    // Locality :
    public static double LO(String className, String method){
        int actionsOfCOA = new CreateObjectAction(className).getAction();
        int actionsOfRSFA = new ReadStructuralFeatureAction(className , method).getActions();
        int actionsOfCOpA = new CallOperationAction(className , method).getActions();
        int actionsOfCBA = new CallBehaviorAction(className , method).getActions();
        int readInReferenced = actionsOfCOA + actionsOfRSFA + actionsOfCOpA;
        int allRead = actionsOfCOA + actionsOfRSFA + actionsOfCOpA + actionsOfCBA;
        return (double)readInReferenced/allRead;
    }
}
