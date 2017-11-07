package ru.track.prefork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.workers.NioClient;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 */
public class Server {
    private int port;
    private ServerSocket socket;
    private boolean stopped;
    static Logger log = LoggerFactory.getLogger(NioClient.class);

    public Server(int port) throws IOException {
        socket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        this.port = port;
    }

    public void run() throws IOException {
        while (!stopped) {
            Socket client = socket.accept();
            System.out.println("Accepted!");
            OutputStream writer = client.getOutputStream();
            String resp = Client.read(client);
            System.out.println("Client: " + resp);
            writer.write(resp.getBytes());
            client.shutdownOutput();
        }
        socket.close();
    }

    public void close() {
        stopped = true;
    }
    public static void main(String[] args) {
        try {
            Server s =  new Server(8080 );
            s.run();
        }
        catch (IOException exc) {
            System.out.println(exc.getMessage());
        }
    }
}
