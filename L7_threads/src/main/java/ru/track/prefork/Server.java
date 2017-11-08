package ru.track.prefork;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 *
 */
public class Server {
    private int port;
    private AtomicLong counter;
    private ConcurrentMap<Long, String> messages;
    Object sync = new Object();

    public Server(int port) {
        this.port = port;
        counter = new AtomicLong(0);
        messages = new ConcurrentHashMap<>();
    }

    private void handle(@NotNull Socket sock){
        try {
            long id = counter.getAndIncrement();
            Thread.currentThread().setName("Client[" + id + "]@"+sock.getInetAddress().toString()+":"+sock.getPort());
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            System.out.println("Подключился: " + Thread.currentThread().getName());
            InputStream is = sock.getInputStream();
            OutputStream os = sock.getOutputStream();
            new Thread(() -> {listen(is, id, "Client@"+sock.getInetAddress().toString()+":"+sock.getPort()+"> ", Thread.currentThread());}).start();
            synchronized(sync) {
                try {
                    while (true) {
                        sync.wait();
                        for (ConcurrentMap.Entry<Long, String> entry : messages.entrySet())
                            if (!entry.getKey().equals(id))
                                os.write(entry.getValue().getBytes());
                    }
                } catch (InterruptedException e) {}
            }
        }
        catch(IOException e) {}
    }

    private void listen(InputStream is, long id, String name, Thread t){
        try {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            int nRead;
            byte[] buffer = new byte[1024];
            while (true) {
                nRead = is.read(buffer);
                if (nRead > -1) {
                    String str = new String(buffer, 0, nRead);
                    System.out.println(name + str);
                    synchronized(sync){
                        messages.clear();
                        messages.put(id, name + str);
                        sync.notifyAll();
                    }
                } else break;
            }
        }
        catch(IOException e) {}
        finally{
            System.out.println("Client["+ id + "] отсоединился");
            t.interrupt();
        }
    }

    public void serve() throws IOException{
        ServerSocket ssock = null;
        try {
            ssock = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
            Socket sock;
            Thread t;
            while (true) {
                sock = ssock.accept();
                final Socket socket = sock;
                t = new Thread(() -> {handle(socket);});
                t.start();
            }
        }
        finally {
            IOUtils.closeQuietly(ssock);
        }
    }

    public static void main(String[] args) throws Exception {
        final Server server = new Server(8100);
        server.serve();
    }
}
