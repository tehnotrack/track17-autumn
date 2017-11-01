package ru.track.prefork;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.security.Signature;
import java.util.Scanner;

/**
 *
 */
public class Client {

    static Logger log = LoggerFactory.getLogger(Server.class);

    private int port;
    private String host;




    public void Connect() throws IOException {
        Socket sock = null;

        try {
            sock = new Socket(host, port);


            InputStream in = sock.getInputStream();
            OutputStream os = sock.getOutputStream();


            Scanner scan = new Scanner(System.in);

            String line = scan.nextLine();
            os.write((line + "\n").getBytes());
            os.flush();

            byte[] buffer = new byte[1024];
            int nRead = in.read(buffer);
            log.info("Client recieved: " + new String(buffer,0, nRead));
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(sock);
        }

    }


    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }


    public static void main(String[] args) throws Exception {
        Client myclient = new Client(9000, "localhost");
        try {
            myclient.Connect();
        }
        catch (IOException e){
            e.printStackTrace();
        }


    }

}
