import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Sender {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        ObjectCreator objectCreator = new ObjectCreator();
        Serializer serializer = new Serializer();
        Scanner input = new Scanner(System.in);

        System.out.print("Host: ");
        String host = input.nextLine();
        System.out.print("Port: ");
        int port = input.nextInt();

        ArrayList<Object> objects = objectCreator.createObjectsMenu();
       // for(Object object : objects){
            Document doc = serializer.serialize(objects);

//            Socket socket = new Socket(host, port);
//            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());

            System.out.println("Sending document to Receiver.");
//            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//            new XMLOutputter().output(doc, byteOut);
//            dataOut.write(byteOut.toByteArray());
//            dataOut.close();
//            socket.close();
            try{
                Socket socket = new Socket(host, port);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(doc);
                outputStream.flush();
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
