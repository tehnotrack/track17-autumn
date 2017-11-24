package ru.track.prefork;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.prefork.protocol.JavaSerializationProtocol;
import ru.track.prefork.protocol.Message;
import ru.track.prefork.protocol.Protocol;
import ru.track.prefork.protocol.ProtocolException;
import ru.track.workers.NioClient;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;


public class Server {

    public class Worker extends Thread {
        Socket clientSocket;
        private Protocol<Message> protocol;
        private long id;
        boolean isOpen;

        Worker(@NotNull Socket client, @NotNull Protocol<Message> protocol, long id) {
            super(String.format("Client[%d]@%s:%d", id, client.getInetAddress(), client.getPort()));
            this.clientSocket = client;
            this.protocol = protocol;
            this.id = id;
            this.isOpen = true;
        }

        void send(Message msg) {
            try {
                OutputStream out = clientSocket.getOutputStream();
                out.write(protocol.encode(msg));
                out.flush();
            } catch (IOException exc) {
                log.error(exc.getMessage());
                exc.printStackTrace();
            } catch (ProtocolException exc) {
                log.error(exc.getMessage());
                exc.printStackTrace();
            }
        }

        public void handleSocket(Socket clientSocket) throws IOException, ProtocolException {
            InputStream in;
            in = clientSocket.getInputStream();

            byte[] buf = new byte[1024];
            while (true) {
                int nRead = -1;
                try {
                    nRead = in.read(buf);
                }
                catch (IOException exc) {
                    if (!isOpen) {
                        System.err.println("Connection closed by server");
                    } else {
                        throw exc;
                    }
                }
                if (nRead != -1) {
                    Message fromClient = protocol.decode(buf);
                    fromClient.text = ">" + fromClient.text;
                    workersMap.forEach((num, worker) -> {
                        if (num != this.id) {
                            worker.send(fromClient);
                        }
                    });
                } else {
                    log.error("No more messages from client");
                    return;
                }
            }
        }

        public void run() {
            new Thread(() -> {
                Scanner in = new Scanner(System.in);
                while (true) {
                    String str = in.nextLine();
                    if (str.equals("list")) {
                        for (Worker w : workersMap.values()) {
                            System.out.println(w.getName());
                        }
                    } else if (str.startsWith("drop")) {
                        String[] subs = str.split(" ");
                        Worker w = workersMap.get(Long.parseLong(subs[1]));
                        if (w == null) {
                            System.out.println("No such client");
                        } else {
                            w.send(new Message(0, "Your connection was dropped", "Server"));
                            try {
                                w.isOpen = false;
                                w.clientSocket.close();
                            } catch (IOException exc) {
                                System.out.println("Can't drop connection");
                            }
                        }
                    }
                }
            }).start();
            try {
                handleSocket(clientSocket);
            } catch (Exception exc) {
                workersMap.remove(id);
                log.error(exc.getMessage());
                exc.printStackTrace();
                throw new RuntimeException(exc);
            }
        }

    }

    private int port;
    private ServerSocket socket;
    private boolean stopped;
    static Logger log = LoggerFactory.getLogger(NioClient.class);
    private Protocol<Message> protocol;
    private ConcurrentMap<Long,Worker> workersMap;
    private AtomicLong clientsCounter;

    public Server(int port, Protocol<Message> protocol) throws IOException {
        socket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        this.port = port;
        this.protocol = protocol;
        this.clientsCounter = new AtomicLong();
        workersMap = new ConcurrentHashMap<>();
    }


    public void run() throws IOException {
        while (!stopped) {
            Socket client = socket.accept();
            log.info("New client connected");
            Worker worker = new Worker(client, protocol, clientsCounter.addAndGet(1));
            workersMap.put(clientsCounter.get(), worker);
            worker.start();
        }
        socket.close();
    }

    public void close() {
        stopped = true;
    }
    public static void main(String[] args) {
        try {
            Server s =  new Server(8080, new JavaSerializationProtocol());
            s.run();
        }
        catch (IOException exc) {
            log.error(exc.getMessage());
            exc.printStackTrace();
        }
    }
}
