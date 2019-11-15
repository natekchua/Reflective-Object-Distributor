/*
  an object that contains references to other objects.
*/
public class ObjectB {
    private ObjectA primitiveObject;

    public ObjectB(){

    }

    public ObjectB(ObjectA primitiveObject){
        this.primitiveObject = primitiveObject;
    }
}
