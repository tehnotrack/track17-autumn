package ru.track.prefork;

import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Server {
    static Logger log = LoggerFactory.getLogger(Server.class);
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void serve() throws IOException {
        try (ServerSocket server = new ServerSocket(port, 10, InetAddress.getByName("localhost"))) {

            Socket client = server.accept();
            OutputStream out = client.getOutputStream();
            InputStream in = client.getInputStream();
            while(true)
            {
                byte[] buffer = new byte[1024];
                int count = in.read(buffer);
                log.info("Client:"+ new String(buffer,0,count));
                String line = new String(buffer,0,count);
                out.write(line.getBytes());
                out.flush();
            }

        }
        catch(IOException e)
        {
           log.error("ERROR" + e.getMessage()) ;
        }
        finally
        {
            if(client =! null)
            {
                client.close();
            }
        }
    }

    public static void main(String[] args) throws Exception{
        Server server = new Server(9000);
        server.serve();
    }
}
