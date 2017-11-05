package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 */
public class Server {
    private int port;
    static Logger log = LoggerFactory.getLogger(Server.class);
    static private AtomicInteger idcounter;
    private Map<Integer, String> idmap;


    class Mythread extends Thread{
        Socket socket;

        Mythread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            byte[] buffer = new byte[1024];
                try {
                    String s;
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

    public Server(int port) {
        this.port = port;
        Server.idcounter = new AtomicInteger(0);
        this.idmap = new LinkedHashMap<>();

    }


    public void serve() {
        ServerSocket ssock = null;
        try {
            ssock = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true)
        {
            Socket socket = null;
            try {
                socket = ssock.accept();
            } catch (IOException e) {
                log.error("Cant connect");
            }

            int id = idcounter.addAndGet(1);
            Thread newclient = new Mythread(socket);
            String address = socket.getInetAddress().toString().substring(1, socket.getInetAddress().toString().length());
            idmap.put(id, "Client[" + id + "]@" + address + ":" + socket.getPort());
            newclient.setName(idmap.get(id));
            newclient.start();
        }

    }


    public static void main(String args[]) throws IOException {
        Server server = new Server(9000);
        server.serve();
    }
}
