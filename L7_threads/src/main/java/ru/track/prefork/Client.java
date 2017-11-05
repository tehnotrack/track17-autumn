package ru.track.prefork;
import java.security.Signature;
import java.util.Scanner;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.workers.NioClient;
import java.io.*;
import java.net.Socket;

/**
 *
 */
public class Client {
    private boolean myError;
    static Logger log = LoggerFactory.getLogger(Client.class);
    private int port;
    private String host;
    Socket socket;

    public Client( @NotNull String host, int port) {
        this.myError = false;
        this.port = port;
        this.host = host;
        this.socket = null;

    }


    public void workInCycle() {


        try {
            socket = new Socket(String.valueOf(host), port);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        myClientReader myClientReader = new myClientReader();
        myClientReader.start();

        try (
                Scanner myScanner = new Scanner(System.in);
                OutputStream out = socket.getOutputStream();
        ) {
            while (true) {
                String myNextLine = myScanner.nextLine();
                if (myExit(myNextLine))
                    myError = true;
                if (myError) {
                    break;
                }
                out.write(myNextLine.getBytes());
                out.flush();

            }
        } catch (Exception e) {
            myError = true;
            System.out.println(e.getMessage());
        }

    }


    private boolean myExit(String message) {
        if (message.equals("exit"))
            return true;
        return false;
    }

    private class myClientReader extends Thread {

        InputStream myStream;

        private myClientReader() {
            myStream = null;
        }

        @Override
        public void run() {
            myClientReaderFunc();
        }

        private void myClientReaderFunc() {
            try {
                while (true) {
                    myStream = socket.getInputStream();
                    byte[] myBuffer = new byte[1024];
                    int amountOfBytes = myStream.read(myBuffer);
                    if ( myError || !(amountOfBytes >= 0) ) {
                        log.info("Error has occured! Please contact adminstrator");
                        myError = true;
                        break;
                    }
                    System.out.println(new String(myBuffer, 0, amountOfBytes));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                myError = true;
            }
            finally {
                try {
                    myStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        final Client myClient = new Client("localhost", 8000);
        myClient.workInCycle();
    }

}
