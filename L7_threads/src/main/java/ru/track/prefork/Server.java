package ru.track.prefork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Server {
    private int port;
    static Logger log = LoggerFactory.getLogger(Server.class);

    public Server(int port) throws IOException {
        this.port = port;
    }

    public void serve() throws IOException {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
            Socket socket = serverSocket.accept();

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            byte[] buffer = new byte[1024];

            while (true) {
                int nRead = in.read(buffer);
                String input = new String(buffer, 0, nRead);
                log.info("Client: " + input);
                out.write(input.getBytes());
                out.flush();
                Arrays.fill(buffer, (byte) 0);
            }
        }catch (IOException e){
            log.error("ERROR"+e.getMessage());
        } finally {
            if (serverSocket!=null)
                serverSocket.close();
        }

    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(9000);
        server.serve();
    }
}
