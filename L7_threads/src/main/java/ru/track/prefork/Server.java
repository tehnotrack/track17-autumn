package ru.track.prefork;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 */
public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void serve() throws IOException {
        Thread admin = new Thread(this::adminConsole);
        admin.setName("admin");
        admin.start();

        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket;
        Pool pool = new Pool();

        while (true) {
            socket = serverSocket.accept();

            pool.addClient(socket);
        }
    }

    private void adminConsole() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            AdminConsole admin = new AdminConsole(scanner.nextLine());
            admin.execute();
        }
    }

    private class AdminConsole {
        private final String[] COMMANDS = {"list", "drop"};

        private String command;

        public AdminConsole(String command) {
            this.command = command;
        }

        public void execute() {
            String[] splitCommands = command.split(" ");

            String mainCommand = splitCommands[0];
            boolean commandFound = false;

            for (String availableCommand : COMMANDS) {
                if (mainCommand.equals(availableCommand)) {
                    commandFound = true;

                    try {
                        Method commandMethod = this.getClass().getDeclaredMethod(availableCommand, String[].class);

                        commandMethod.invoke(this, (Object) splitCommands);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        System.out.println(e.toString());
                        commandFound = false;
                    }
                }
            }

            if (!commandFound) {
                System.out.println("admin: command '" + mainCommand + "' not found!");
            }
        }

        private void list(String[] commandLines) {
            System.out.println("list command");
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
