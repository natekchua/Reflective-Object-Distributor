public class ObjectD {
    private ObjectC [] array;

    public ObjectD(){

    }

    public ObjectD(int length){
        this.array = new ObjectC[length];
    }

    public ObjectC[] getArray() {
        return array;
    }

    public void setArray(ObjectC[] array) {
        this.array = array;
    }
}
