package ru.track.prefork;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 */
public class Server {
    private int port;
    static Logger log = LoggerFactory.getLogger(Server.class);
    static private AtomicInteger idcounter;
    private Map<Integer, Mythread> idmap;
    private Protocol<Message> protocol;


    public Server(int port) {
        this.port = port;
        Server.idcounter = new AtomicInteger(0);
        this.idmap = new ConcurrentHashMap<>();
        this.protocol = new BinaryProtocol<>();

    }


    public void serve() {
        ServerSocket ssock = null;
        try {
            ssock = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread adminthread = new Adminthread();
        adminthread.start();

        log.info("Its ok, im here");
        while (true) {
            Socket socket = null;
            try {
                socket = ssock.accept();
            } catch (IOException e) {
                log.error("Cant connect");
            }

            int id = idcounter.addAndGet(1);
            Mythread newclient = new Mythread(socket, id);
            idmap.put(id, newclient);
            newclient.start();
        }

    }


    class Adminthread extends Thread
    {
        @Override
        public void run()
        {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("list"))
                    log.info(idmap.toString());
                else if (line.matches("drop \\d+"))
                {
                    Integer id = Integer.parseInt(line.split(" ")[1]);
                    if (idmap.get(id) != null){
                        IOUtils.closeQuietly(idmap.get(id).getSocket());
                        idmap.remove(id);
                        log.info("dropped");
                    }
                    else
                        log.info("no such client");
                }
            }

        }
    }
    class Mythread extends Thread {

        @NotNull
        Socket socket;
        Integer id;
        private OutputStream out;

        Mythread(@NotNull Socket socket, Integer id) {
            this.socket = socket;
            this.id = id;
            setName(String.format("Client[%d]@%s:%d", id, socket.getInetAddress(), socket.getPort()));
        }

        void setOut(OutputStream out) {
            this.out = out;
        }

        Socket getSocket()
        {
            return this.socket;
        }

        @Override
        public void run() {
            log.info("Hello!");
            byte[] buffer = new byte[1024];
            OutputStream out;
            InputStream in = null;
            try {
                out = socket.getOutputStream();
                in = socket.getInputStream();
                idmap.get(id).setOut(out);
            } catch (IOException e) {
                idmap.get(id).interrupt();
                log.error("cant open connection");
            }

            try {
                while (in.read(buffer) > 0) {
                    Message newmes = protocol.decode(buffer);
                    if (newmes.getText().equals("exit"))
                    {
                        idmap.remove(id);
                        break;
                    }
                    log.info("Client:" + newmes.getText());

                    for (Map.Entry<Integer, Mythread> entry : idmap.entrySet()) {
                        if (entry.getValue().isAlive() && (entry.getKey() != id)) {
                            try {
                                entry.getValue().send(newmes);
                            } catch (IOException e) {
                                log.error("Cant write");
                            }
                        }
                    }
                }
            }catch (SocketException e){
 //               log.info("dropped1234");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

   //         idmap.get(id).interrupt();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        void send(Message message) throws IOException {
            out.write(protocol.encode(message));
            out.flush();
        }
    }


    public static void main(String args[]) throws IOException {
        Server server = new Server(9000);
        server.serve();
    }
}
