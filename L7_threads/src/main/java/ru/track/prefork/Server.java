package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.prefork.exceptions.NoThreadSpecified;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

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
        Socket socket;

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
            if (!checkSubCommands(commandLines, "list command does not any arguments", 0)) {
                return;
            }

            Set<String> users = pool.getUsers();

            if (users.isEmpty()) {
                System.out.println("No users connected!");
            } else {
                System.out.println("Connected users:");
            }

            users.forEach(System.out::println);
        }

        private void drop(String[] commandLines) {
            if (!checkSubCommands(commandLines, "drop command takes only 1 argument: id to drop the client", 1)) {
                return;
            }

            int id = Integer.parseInt(commandLines[1]);

            try {
                if (pool.dropClient(id)) {
                    System.out.println("Dropped client #" + id);
                } else {
                    System.out.println("No client with id " + 1);
                }
            } catch (IOException | NoThreadSpecified e) {
                logger.error(e.getMessage());

                System.out.println("Could not drop client! Try one more time...");
            }
        }

        private boolean checkSubCommands(String[] commandLines, String errorMessage, int count) {
            if ((count + 1) != commandLines.length) {
                System.out.println(errorMessage);

                return false;
            }

            return true;
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
