import java.util.ArrayList;

/*
 an object that contains an array of primitives.
*/
public class ObjectC {
    private int [] array;

    public ObjectC(int length){
        this.array = new int[length];
    }

    public int[] getArray() {
        return array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }
}
