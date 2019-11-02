import java.util.HashMap;
import java.util.Scanner;

public class ObjectCreator {
    private Scanner input = new Scanner(System.in);

    public void runMenu(){
        System.out.println("==========");
        System.out.println("OBJECT CREATOR");
        System.out.println("Select an Object to create:");
        displayMenu();
        int choice = input.nextInt();

            switch(choice){
                case 1:
                    createObjectA();                    //obj with primitive values only
                    break;
                case 2:
                    //obj that contains references to other objects
                    createObjectB();
                    break;
                case 3:
                    //object that contains an array of primitives
                    createObjectC();
                    break;
                case 4:
                    //Object that uses an instance of one of Java's collection classes to refer to other objects
                    new ObjectD();
                    break;
                case 5:
                    createObjectE();
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

    }

    /*
    user creates a simple object with only primitives for instance variables.
     */
    private ObjectA createObjectA() {
        ObjectA primitiveObject = new ObjectA();

        System.out.println("** CREATING SIMPLE OBJECT WITH PRIMITIVE FIELDS ONLY **");
        System.out.print("Enter int value: ");
        primitiveObject.setA(input.nextInt());
        System.out.print("Enter double value: ");
        primitiveObject.setB(input.nextDouble());
        System.out.print("Enter float value: ");
        primitiveObject.setC(input.nextFloat());

        return primitiveObject;
    }

    /*
    user creates an object that contains references to other objects.
    other object must be created at the same time, and user is able to set primitive values of the object.
    also checks for circular references.
     */
    private ObjectB createObjectB() {
        System.out.println("** CREATING OBJECT WITH REFERENCE OBJECTS **");
        return new ObjectB(createObjectA());
    }

    /*
    user creates an object that contains an array of primitives.
     */
    private ObjectC createObjectC() {
        System.out.print("** CREATING OBJECT WITH ARRAY OF PRIMITIVES **");

        System.out.print("Set length of array: ");
        int length = input.nextInt();
        ObjectC primitiveArrayObject = new ObjectC(length);

        int[] arr = primitiveArrayObject.getArray();
        for(int i = 0; i < arr.length; i++){
            System.out.print("Set value for element at index" + "[" + i + "]: ");
            arr[i] = input.nextInt();
        }
        primitiveArrayObject.setArray(arr);
        return primitiveArrayObject;
    }

    private void createObjectD() {
    }

    private void createObjectE() {

    }

    private void displayMenu() {
        System.out.println("1. Object with only Primitive Variables");
        System.out.println("2. Object that contains references to other objects");
        System.out.println("3. Object that contains an array of primitives");
        System.out.println("4. Object that contains an array of object references");
        System.out.println("5. Object that uses an instance of one of Java's collection classes to refer to other objects");
        System.out.println("6. quit");
    }
}
