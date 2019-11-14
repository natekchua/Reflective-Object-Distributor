import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Receiver {

    private static Scanner scanner = new Scanner(System.in);
    private static ObjectInspector visualizer = new ObjectInspector();

    public static void main(String[] args) {
        try{
            System.out.print("Please Enter Server Port: ");
            int port = scanner.nextInt();

            System.out.println("Creating server socket...");
            ServerSocket serverSocket = new ServerSocket(port);

            Socket socket = serverSocket.accept();
            System.out.println("Successfully received document from Sender.");
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Document doc = (Document) inputStream.readObject();

            //todo: deserialize document

            socket.close();
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }
}
