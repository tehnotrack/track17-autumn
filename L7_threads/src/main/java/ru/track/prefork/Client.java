package ru.track.prefork;
import ru.track.prefork.Protocol.Message;
import ru.track.prefork.Protocol.Protocol;
import ru.track.prefork.Protocol.MySerializationProtocol;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client {
    private static Protocol<Message> protocol = new MySerializationProtocol<>();
    public boolean myError;
    static Logger log = LoggerFactory.getLogger(Client.class);
    private int port;
    private String host;
    public  Socket socket;
    public String exNextLine ="";

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
        if (message.equals("exit")   )
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
                        if ( amountOfBytes >= 0) {
                           Message message = protocol.decode(myBuffer);
                           if ( message.toString().equals("Your connection was terminated, sorry")){
                               System.out.println(message.toString());
                               socket.close();
                               break;
                           }
                        }
                        log.info("Your connection was terminated, sorry");
                        myError = true;
                        break;
                    }
                    Message message = protocol.decode(myBuffer);
                    System.out.println(message.toString());

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
