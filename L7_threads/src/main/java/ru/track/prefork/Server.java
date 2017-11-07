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
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.exit;
import static java.lang.System.in;
import static java.lang.System.setOut;

/**
 *
 */
public class Server {
    private int port;
    static Logger log = LoggerFactory.getLogger(Server.class);
    ServerSocket serverSocket;
    Socket clientSocket = null;
    AtomicInteger atomicInteger = new AtomicInteger(0);


    public Server(int port) throws IOException {
        this.port = port;
    }

    public void serve() throws Exception {
        try {
            serverSocket = new ServerSocket(9000, 10, InetAddress.getByName("localhost"));
            log.info("Server is working");
        } catch (IOException ioe) {
            log.error("Can't raise server");
            ioe.printStackTrace();
            exit(-1);
        }

        try {
            while (true) {
                clientSocket = serverSocket.accept();
                MultiThreadedServer multiThreadedServer = new MultiThreadedServer(clientSocket);
                multiThreadedServer.start();
                atomicInteger.getAndIncrement();
                String address = clientSocket.getLocalAddress().toString().replaceAll("/", "");
                multiThreadedServer.setName("Client[" + atomicInteger.get() + "]@" + address + ":" + clientSocket.getPort());
                log.info("Got new client from port " + clientSocket.getPort());
            }
        } catch (IOException ioe) {
            log.error("Client cant connect");
            ioe.printStackTrace();
            exit(-1);
        }
    }

    class MultiThreadedServer extends Thread {
        Socket clientSocket = null;
        OutputStream out = null;
        InputStream in = null;

        public MultiThreadedServer (Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run () {
            try {
                in = clientSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out = clientSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] buffer = new byte[1024];
            int nRead;
            while (true) try {
                nRead = in.read(buffer);
                if (nRead == 0) ;
                else {
                    String str = new String(buffer, 0, nRead);
                    log.info(str);
                    out.write(str.getBytes());
                    out.flush();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(9000);
        server.serve();
    }
}