package ru.track.prefork;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler extends Thread {
    private long id;
    private Socket clientSocket;
    static Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private String message = null;

    @NotNull
    private BufferedReader in;
    @NotNull
    private PrintWriter out;

    @NotNull
    public PrintWriter getOut() {
        return out;
    }

    public ClientHandler(Socket clientSocket, long id) throws IOException {
        this.clientSocket = clientSocket;
        this.id = id;
        String name = String.format("Client@%s:%d",
                clientSocket.getInetAddress().toString().substring(1), clientSocket.getPort());
        setName(name);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void broadcast(ConcurrentHashMap<Long, ClientHandler> clients, String msg) {
        clients.forEach(
                (id, clientHandler) -> {
                    if (!id.equals(this.id)) {
                        clientHandler.getOut().println(msg);
                    }
                }
        );
    }

    public String getMessage() {
        return message;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void run() {
        try (
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("exit")) break;
                message = getName().concat(">").concat(inputLine);
                out.println(getName() + ": " + inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
