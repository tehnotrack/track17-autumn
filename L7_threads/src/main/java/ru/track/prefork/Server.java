package ru.track.prefork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.workers.NioClient;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */

public class Server {
    private int port;
    private ServerSocket socket;
    private boolean stopped;
    static Logger log = LoggerFactory.getLogger(NioClient.class);
    public class Worker implements Runnable {
        Socket client;
        Worker(Socket client) {
            this.client = client;

        }

        public void run() {
            try {
                String name = Thread.currentThread().getName();
                System.out.printf("%s started\n", name);
                InputStream reader = client.getInputStream();
                OutputStream writer = client.getOutputStream();
                int len;
                byte[] buf = new byte[1024];
                while((len = reader.read(buf)) >= 0) {
                    writer.write(buf, 0, len);
                    byte[] copy = Arrays.copyOfRange(buf,0, len);
                    String temp = new String(copy);
                    System.out.println(temp);
                }
                reader.close();
                client.close();
            }
            catch (IOException ex) {

            }

        }
    }
    public Server(int port) throws IOException {
        socket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        this.port = port;
    }


    public void run() throws IOException {
        AtomicInteger clientsCounter = new AtomicInteger();
        while (!stopped) {
            Socket client = socket.accept();
            Thread thread = new Thread(new Worker(client),
                    String.format("Client[%d]@%s:%s", clientsCounter.addAndGet(1),
                            client.getLocalAddress(),
                            client.getLocalPort()));

            thread.start();

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
