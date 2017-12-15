package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.prefork.admin.AdminConsole;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 */
public class Server {
    private static Logger logger = LoggerFactory.getLogger("logger");
    
    private int port;
    private Pool pool = new Pool();
    
    public Server(int port) {
        this.port = port;
    }
    
    public void serve() throws IOException {
        Thread admin = new Thread(this::adminConsole);
        admin.setName("admin");
        admin.start();
        
        ServerSocket serverSocket = new ServerSocket(port);
        Socket       socket;
        
        while (true) {
            socket = serverSocket.accept();
    
            pool.addClient(socket);
        }
    }
    
    private void adminConsole() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            AdminConsole admin = new AdminConsole(pool, scanner.nextLine());
            admin.execute();
        }
    }
    
    public static void main(String[] args) {
        try {
            Server server = new Server(8000);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
