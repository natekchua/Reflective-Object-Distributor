import ObjectTypes.*;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class ObjectCreator {
    private Scanner input = new Scanner(System.in);
    private ArrayList <Object> objects = new ArrayList<>(); //stores objects created by the user.

    public static void main(String[] args) {
        ObjectCreator program = new ObjectCreator();
        program.runMenu();
    }

    /*
    method to handle user interaction for creating objects.
     */
    public void runMenu(){
        boolean proceed = true;
        while(proceed) {
            displayMenu();
            System.out.print("\nEnter choice: ");
            int choice = input.nextInt();
            switch (choice) {
                case 1:
                    objects.add(createObjectA());
                    break;
                case 2:
                    objects.add(createObjectB());
                    break;
                case 3:
                    objects.add(createObjectC());
                    break;
                case 4:
                    objects.add(createObjectD());
                    break;
                case 5:
                    objects.add(createObjectE());
                    break;
                case 6:
                    proceed = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
            //todo: pass in set of objects to serialize
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
        System.out.println("** CREATING OBJECT WITH ARRAY OF PRIMITIVES **");

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

    /*
    user creates an object that contains an array of object references.
     */
    private ObjectD createObjectD() {
        System.out.println("** CREATING OBJECT WITH ARRAY OF OBJECT REFERENCES **");

        System.out.print("Set length of array: ");
        int length = input.nextInt();
        ObjectD referenceArrayObject = new ObjectD(length);
        ObjectA [] arr = referenceArrayObject.getArray();

        for(int i = 0; i < arr.length; i++){
            System.out.print("Set value for element at index" + "[" + i + "]: ");
            arr[i] = createObjectA();
        }
        referenceArrayObject.setArray(arr);
        return referenceArrayObject;
    }

    /*
    user creates an object that uses an instance of one of Java's collection classes to
    refer to other objects. ObjectTypes.ObjectE uses ArrayList.
     */
    private ObjectE createObjectE() {
        ObjectE collectionObject = new ObjectE();
        System.out.println("** CREATING OBJECT WITH JAVA COLLECTION **");

        ArrayList<ObjectA> arr = collectionObject.getObjectsArray();
        System.out.println("Add an object into Java Collection Structure? (y/n): ");
        input.nextLine();
        String choice = input.nextLine();

        if(choice.equals("y")){
            boolean proceed = true;
            while(proceed){
                arr.add(createObjectA());
                System.out.println("Add another object? (y/n): ");
                input.nextLine();
                choice = input.nextLine();
                if(choice.equals("n"))
                    proceed = false;
            }
        }

        return collectionObject;
    }

    private void displayMenu() {
        System.out.println("\n==============");
        System.out.println("OBJECT CREATOR");
        System.out.println("==============");

        System.out.println("\nSelect an Object to create:");
        System.out.println("1. Object with only Primitive Variables");
        System.out.println("2. Object that contains references to other objects");
        System.out.println("3. Object that contains an array of primitives");
        System.out.println("4. Object that contains an array of object references");
        System.out.println("5. Object that uses an instance of one of Java's collection classes to refer to other objects");
        System.out.println("6. quit");
    }
}
