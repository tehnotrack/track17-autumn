package ru.track;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private static Logger logger = LoggerFactory.getLogger("Server");
    private Map<Integer, Thread> idMap = new HashMap<>();
    private static List<ServerThread> threads = Collections.synchronizedList(new ArrayList<>());
    private static int currentId = 0;

    public Server(int port) {
        this.port = port;
    }

    private void start() throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("Couldn't start on port {}", port);
            throw e;
        }
    }

    private void shutdown() throws IOException {
        if (serverSocket != null)
            serverSocket.close();
    }

    static class ServerThread extends Thread{
        BlockingQueue<String> toSend = new LinkedBlockingQueue<>();

        Socket socket;
        private int ID = currentId;

        String threadName;

        public ServerThread(Socket clientSocket){
            socket = clientSocket;
            threadName = "Client["
                    + Integer.toString(ID)
                    + "]@"
                    + socket.getInetAddress().toString()
                    + ":"
                    + socket.getPort();
        }

        @Override
        public void run() {
            logger.info("New client");
            Thread.currentThread().setName(threadName);
            try {
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
//                IOUtils.toString(socket.getInputStream(), Charset.defaultCharset());
//                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                String input;
                while (true) {

                    while(new InputStreamReader(socket.getInputStream()).ready()){
                        input = in.readLine();
                        logger.info(" Got request from client {}: {}",  threadName, input);
                        if (input.equals("exit")) {
                            socket.close();
                            threads.remove(this);
                            return;
                        } else {
//                                    out.println(input);
                            synchronized (threads){
                                for(ServerThread thread : threads){
                                    if(thread.ID != ID)
                                        thread.toSend.add("Client"
                                                + Integer.toString(ID)
                                                + "@"
                                                + socket.getInetAddress().toString()
                                                + ":"
                                                + socket.getPort()
                                                +">"
                                                +input);
                                }
                            }
                        }
                    }

                    synchronized (toSend){
                        while(!toSend.isEmpty()){
                            out.println(toSend.poll(2, TimeUnit.SECONDS));
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Some troubles: {}", e);
            }
            catch (InterruptedException e){
                logger.error("Thread was interrupted {}", e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(Integer.parseInt(args[0]));
        server.start();

        while (true) {
            Socket clientSocket = server.serverSocket.accept();
            currentId++;
            ServerThread newThread = new ServerThread(clientSocket);
            threads.add(newThread);
            newThread.start();
        }

    }

}
