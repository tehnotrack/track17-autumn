package ru.track.prefork;



import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import ru.track.prefork.protocol.Message;
import ru.track.prefork.protocol.Protocol;
import ru.track.prefork.protocol.JavaSerializationProtocol;
import ru.track.prefork.protocol.ProtocolException;





/**
 *
 */
public class Client {


    static Logger log = LoggerFactory.getLogger(Server.class);


    private int port;
    private String host;
    private Protocol<Message> protocol;


    public Client(int port, String host, Protocol<Message> protocol){
        this.port = port;
        this.host = host;
        this.protocol = protocol;

    }



    public void Connect() throws IOException {
        Socket socket = null;

        try {
            socket = new Socket(host, port);

            final InputStream in = socket.getInputStream();
            final OutputStream os = socket.getOutputStream();

            Scanner scan = new Scanner(System.in);
            Thread scannerThread = new Thread(() -> {
                try {
                    while (true) {
                        String line = scan.nextLine();
                        Message msg = new Message(System.currentTimeMillis(), line);
                        msg.username = "Daniil";
                        os.write(protocol.encode(msg));
                        os.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }

            });

            scannerThread.start();


            byte[] buf = new byte[1024];
            while (true) {
                int nRead = in.read(buf);
                if (nRead != -1) {
                    protocol.decode(buf);
                } else {
                    log.error("Connection failed");
                    return;
                }
            }

        }
        catch (IOException e){
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(socket);
        }

    }



    public static void main(String[] args) throws Exception {
        Client myclient = new Client(9000, "localhost", new JavaSerializationProtocol());
        try {
            myclient.Connect();
        }
        catch (IOException e){
            e.printStackTrace();
        }


    }

}
