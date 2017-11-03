package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 *
 */
public class Server {
    private int port;
    static Logger log = LoggerFactory.getLogger(Server.class);


    public Server(int port) {
        this.port = port;
    }


    public void serve() {
        ServerSocket ssock = null;
        try {
            ssock = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buffer = new byte[1024];
        String s;


        while (true) {
            Socket socket = null;
            try {
                socket = ssock.accept();
            } catch (IOException e) {
                log.error("Cant connect");
            }

            try {
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();
                int nRead = in.read(buffer);
                while (true) {
                    out.write(buffer, 0, nRead);
                    s = new String(buffer, 0, nRead);
                    log.info("Client:" + s);
                    nRead = in.read(buffer);
                    if (s.equals("exit") | (nRead < 0))
                        break;
                }
            } catch (IOException e) {
                log.error("cant open connection");
            }

        }
    }

    public static void main(String args[]) throws IOException {
        Server server = new Server(9000);
        server.serve();
    }
}
