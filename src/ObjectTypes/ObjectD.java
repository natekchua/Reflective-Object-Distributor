package ObjectTypes;

/*
an object that contains an array of object references.
 */
public class ObjectD {
    private ObjectA[] array;

    public ObjectD(){

    }

    public ObjectD(int length){
        this.array = new ObjectA[length];
    }

    public ObjectA[] getArray() {
        return array;
    }

    public void setArray(ObjectA[] array) {
        this.array = array;

    }
}
