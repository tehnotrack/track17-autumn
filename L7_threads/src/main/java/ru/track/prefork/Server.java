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
    private Map<Integer, NewClient> idmap;

    class NewClient{

        private String name;
        private Boolean alive;
        private Socket clientsocket;
        private OutputStream out;

        NewClient(Socket clientsocket, Integer id)
        {
            String address = clientsocket.getInetAddress().toString().replaceAll("\\/", "");
            this.name = "Client[" + id + "]@" + address + ":" + clientsocket.getPort();
            this.alive = true;
            this.clientsocket = clientsocket;
        }

        Boolean getAlive()
        {
            return this.alive;
        }

        void dead()
        {
            this.alive = false;
        }

        String getName()
        {
            return this.name;
        }

        void setOut(OutputStream out)
        {
            this.out = out;
        }

        OutputStream getOut()
        {
            return this.out;
        }
    }

    class Mythread extends Thread{
        Socket socket;
        Integer id;

        Mythread(Socket socket, Integer id) {
            this.socket = socket;
            this.id = id;
        }

        @Override
        public void run()
        {
            log.info("Hello!");
            byte[] buffer = new byte[1024];
//            log.info("Im here");
            OutputStream out;
            InputStream in = null;
            try{
                out = socket.getOutputStream();
                in = socket.getInputStream();
                idmap.get(id).setOut(out);
            }
            catch (IOException e)
            {
                idmap.get(id).dead();
                log.error("cant open connection");
            }
            int nRead;
            String message;
//            log.info("Its okay");
//            log.info(in.toString());

            try {
                while((nRead = in.read(buffer)) > 0)
                {
 //                   log.info("Im here2");
                    message = new String(buffer, 0, nRead);
                    if (message.equals("exit"))
                        break;
                    log.info("Client:" + message);
                    telleveryone(message, id);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            idmap.get(id).dead();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void telleveryone(String message, Integer id)
    {
  //      log.info(idmap.toString());
 //       log.info("I try to tell everyone" + message);
        for(Map.Entry<Integer, NewClient> entry: idmap.entrySet())
        {
 //           log.info(entry.getKey() + " Clients number");
 //           log.info(entry.getValue().getAlive().toString());
            if (entry.getValue().getAlive() && (entry.getKey() != id))
            {
//                log.info(entry.getValue().toString());
                try {
                    entry.getValue().getOut().write(message.getBytes());
//                    log.info("I wrote" + message);
                    entry.getValue().getOut().flush();
                } catch (IOException e) {
                    log.error("Cant write");
                }
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
            Thread newclient = new Mythread(socket, id);
            idmap.put(id, new NewClient(socket, id));
            newclient.setName(idmap.get(id).getName());
            newclient.start();
        }

    }


    public static void main(String args[]) throws IOException {
        Server server = new Server(9000);
        server.serve();
    }
}
