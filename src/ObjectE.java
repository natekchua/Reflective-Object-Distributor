import java.util.ArrayList;

/*
an object that contains an instance of one of Java's collection classes to refer to other objects.
 */
public class ObjectE {
    private ArrayList<ObjectA> objectsArray;

    public ObjectE(){

    }

    public ObjectE(ArrayList objectsArray){
        this.objectsArray = objectsArray;
    }
}
