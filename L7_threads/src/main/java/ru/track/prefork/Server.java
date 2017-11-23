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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class Server {

    public class Worker implements Runnable {
        Socket clientSocket;
        private Protocol<Message> protocol;
        private long id;

        Worker(@NotNull Socket client, @NotNull Protocol<Message> protocol, long id) {
            this.clientSocket = client;
            this.protocol = protocol;
            this.id = id;
            Thread.currentThread().setName(String.format("Client[%d]@%s:%d", id, client.getInetAddress(), client.getPort()));
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
                int nRead = in.read(buf);
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
    private Long clientsCounter;

    public Server(int port, Protocol<Message> protocol) throws IOException {
        socket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        this.port = port;
        this.protocol = protocol;
        this.clientsCounter = 0L;
        workersMap = new ConcurrentHashMap<>();
    }


    public void run() throws IOException {
        while (!stopped) {
            Socket client = socket.accept();
            log.info("New client connected");
            Worker worker = new Worker(client, protocol, clientsCounter);
            workersMap.put(clientsCounter, worker);
            clientsCounter += 1;
            new Thread(worker).start();
        }
        socket.close();
    }

    public void close() {
        stopped = true;
    }
    public static void main(String[] args) {
        try {
            Server s =  new Server(8080, new JavaSerializationProtocol() );
            s.run();
        }
        catch (IOException exc) {
            log.error(exc.getMessage());
            exc.printStackTrace();
        }
    }
}
