package ru.track.prefork;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.InternetAddress;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static java.lang.System.exit;
import static java.lang.System.in;
import static java.lang.System.setOut;

/**
 *
 */
public class Server {
    private int port;
    static Logger log = LoggerFactory.getLogger(Server.class);
    //    OutputStream out = null;
//    InputStream in = null;
    static ArrayList clientOutputStreams;

    public Server(int port) throws IOException {
        this.port = port;
    }

    public class MultiThreadServer implements Runnable {
        Socket clientSocket = null;

        public MultiThreadServer(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            BufferedReader clientInput = null;

            try {
                clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String msgToClients;
            try {
                while ((msgToClients = clientInput.readLine()) != null) {
                    log.info("message from client" + msgToClients);
                    tellEveryone(msgToClients);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            try {
                serverSocket = new ServerSocket(9000, 10, InetAddress.getByName("localhost"));
                log.info("Server is working");
            } catch (IOException ioe) {
                log.error("Can't raise server");
                ioe.printStackTrace();
                exit(-1);
            }

            try {
                clientSocket = serverSocket.accept();
                log.info("Got new client" + clientSocket.getPort());
            } catch (IOException ioe) {
                log.error("Client cant connect");
                ioe.printStackTrace();
                exit(-1);
            }
//            try {
//                in = clientSocket.getInputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                out = clientSocket.getOutputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    public void serve() throws Exception {
        byte[] buffer = new byte[1024];
        int nRead;
        while (true) try {
            nRead = in.read(buffer);
            if (nRead == 0) ;
            else {
                String str = new String(buffer, 0, nRead);
                out.write(str.getBytes());
                out.flush();
            }
        } catch (Exception ex) {
            //log.error("smth's gone wrong");
        }
        }
    }

    public static void main (String[] args) throws Exception {
        Server server = new Server(9000);
        server.runserver();
        server.serve();
    }
}
