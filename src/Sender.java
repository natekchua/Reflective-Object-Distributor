import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Sender {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        ObjectCreator objectCreator = new ObjectCreator();
        Serializer serializer = new Serializer();
        Scanner input = new Scanner(System.in);

        System.out.println("Host: ");
        String host = input.nextLine();
        System.out.println("Port: ");
        int port = input.nextInt();

        ArrayList<Object> objects = objectCreator.createObjectsMenu();
        for(Object object : objects){
            Document doc = serializer.serialize(object);

            Socket socket = new Socket(host, port);
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            System.out.println("Sending document to Receiver.");
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            new XMLOutputter().output(doc, byteOut);
            dataOut.write(byteOut.toByteArray());
            dataOut.close();
            socket.close();
        }
    }
}
