package ru.track.prefork;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.workers.NioClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Signature;
import java.util.concurrent.ExecutorService;

/**
 *
 */
public class Server {
    private int port;
    static Logger log = LoggerFactory.getLogger(NioClient.class);


    public Server(int port) {
        this.port = port;
    }


    private boolean iterrupt(String str) {
        if (str.equals("exit"))
            return true;
        return false;
    }


    public void serve() throws IOException {
        ServerSocket serverSocket = null;
        Socket socket = null;
        String bufferString = null;
        try {
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
            OutputStream out = null;
            InputStream in = null;
            socket = serverSocket.accept();
            log.info("Accepted: " + socket.getPort());

            while (true) {
                out = socket.getOutputStream();
                in = socket.getInputStream();

                byte[] buffer = new byte[2048];
                int nRead = in.read(buffer);
                if (nRead == -1) {
                    socket = serverSocket.accept();
                    log.info("Accepted: " + socket.getPort());
                    continue;
                }
                bufferString = new String(buffer, 0, nRead);
                log.info("Client: " + bufferString);
                if (iterrupt(bufferString)) {
                    break;
                }
                out.write(buffer, 0, nRead);
                out.flush();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            serverSocket.close();
            socket.close();
        }

    }

    public static void main (String[] args) throws Exception {
        Server server = new Server(9000);
        server.serve();
    }

}
