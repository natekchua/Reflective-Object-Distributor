import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Sender {
    public static void main(String[] args){
        ObjectCreator objectCreator = new ObjectCreator();
        Serializer serializer = new Serializer();
        Scanner input = new Scanner(System.in);

//        System.out.print("Enter Server Host: ");
//        String host = input.nextLine();
//        System.out.print("Enter Server Port: ");
//        int port = input.nextInt();

        ArrayList<Object> objects = objectCreator.createObjectsMenu();

        try{
            Socket socket = new Socket("localhost", 4444);

            System.out.println("\nSerializing Objects into a document...");
            Document doc = serializer.serialize(objects);
            new XMLOutputter().output(doc, System.out);
            XMLOutputter xml = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            xml.setFormat(format);
            xml.output(doc, new FileWriter("file.xml"));
            System.out.println("Serialization Successful.");

            System.out.println("\nSending Document to Receiver...");
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(doc);
            outputStream.flush();
            System.out.println("Document Successfully Sent.");

            socket.close();
        }catch(IOException | IllegalAccessException e){
            e.printStackTrace();
        }
    }
}
