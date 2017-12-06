package ru.track.prefork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.track.prefork.protocol.JavaSerializationProtocol;
import ru.track.prefork.protocol.Message;
import ru.track.prefork.protocol.Protocol;
import ru.track.prefork.protocol.ProtocolException;


/**
 * - multithreaded +
 * - atomic counter +
 * - setName() +
 * - thread -> Worker +
 * - save threads
 * - broadcast (fail-safe)
 *
 *
 */
public class Server {
    static Logger log = LoggerFactory.getLogger(Server.class);

    private int port;
    private AtomicLong serverCounter = new AtomicLong(0);
    private Protocol<Message> protocol;

    private ConcurrentMap<Long, Worker> workerMap;

    public Server(int port, Protocol<Message> protocol) {
        this.port = port;
        this.protocol = protocol;
        workerMap = new ConcurrentHashMap<>();
    }

    public void serve() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        while (true) {
            log.info("on select...");
            final Socket socket = serverSocket.accept();
            final long workerId = serverCounter.getAndIncrement();
            Worker worker = new Worker(socket, protocol, workerId);
            workerMap.put(workerId, worker);
            worker.start();
        }
    }

    class Worker extends Thread {
        private long id;
        @NotNull
        private Socket socket;

        @NotNull
        private Protocol<Message> protocol;

        @NotNull
        private OutputStream out;

        public Worker(@NotNull Socket socket, @NotNull Protocol<Message> protocol, long id) throws Exception {
            this.socket = socket;
            this.id = id;
            this.protocol = protocol;
            setName(String.format("Client[%d]@%s:%d", id, socket.getInetAddress(), socket.getPort()));

            out = socket.getOutputStream();
        }

        @Override
        public void run() {
            try {
                log.info("Connected");
                handleSocket(socket);
            } catch (Exception e) {
                workerMap.remove(id);
//                throw new RuntimeException(e);
            }
        }

        private void send(Message message) {
            try {
                out.write(protocol.encode(message));
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
        }

        private void handleSocket(Socket socket) throws IOException, ProtocolException {

            final InputStream in = socket.getInputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int nRead = in.read(buf);
                if (nRead != -1) {
                    Message fromClient = protocol.decode(buf);
                    fromClient.text = ">" + fromClient.text;
                    workerMap.forEach((aLong, worker) -> worker.send(fromClient));
                } else {
                    log.error("Connection failed");
                    return;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(9000, new JavaSerializationProtocol());
        server.serve();
    }
}
