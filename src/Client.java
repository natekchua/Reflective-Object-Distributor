import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args){
        String serverAddress = "localhost";
        int serverPort = 4444;
        try{
            Socket socket = new Socket(serverAddress, serverPort);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            Object obj = new Object();
            outputStream.writeObject(obj);
            outputStream.flush();
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
