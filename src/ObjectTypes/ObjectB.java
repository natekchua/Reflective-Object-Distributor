package ObjectTypes;

/*
  an object that contains references to other objects.
*/
public class ObjectB {
    private ObjectA primitiveObject;

    public ObjectB(ObjectA primitiveObject){
        this.primitiveObject = primitiveObject;
    }
}
