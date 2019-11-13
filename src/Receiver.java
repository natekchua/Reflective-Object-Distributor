import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Receiver {

    private static Scanner scanner = new Scanner(System.in);
    private static ObjectInspector visualizer = new ObjectInspector();

    public static void main(String[] args) throws Exception {
        InetAddress address = InetAddress.getLocalHost();
        System.out.println("Server: " + address.getHostAddress());

        System.out.print("Please enter desired port number: ");
        int port = scanner.nextInt();

        System.out.println("Creating server socket...");
        ServerSocket serverSocket = new ServerSocket(port, 0, address);

        Socket socket = serverSocket.accept();
        System.out.println("Received document from Sender");

        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(socket.getInputStream());

        //todo: deserialize document
    }
}
