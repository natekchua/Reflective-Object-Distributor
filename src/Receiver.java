import org.jdom2.Document;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver {

    private static ObjectInspector visualizer = new ObjectInspector();

    public static void main(String[] args) {
        try{
            InetAddress address = InetAddress.getLocalHost();
            System.out.println("Server at: " + address.getHostAddress());

            System.out.println("Waiting to receive document...");
            ServerSocket serverSocket = new ServerSocket(5000, 0, address);

            Socket socket = serverSocket.accept();
            System.out.println("Successfully received document from Sender.");
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Document doc = (Document) inputStream.readObject();

            System.out.println("\nRecreating Objects from Document through deserialization...");
            Deserializer deserializer = new Deserializer();
            Object o = deserializer.deserialize(doc);
            System.out.println("Deserialization Successful.\n");

            visualizer.inspect(o, true);
            socket.close();

        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
