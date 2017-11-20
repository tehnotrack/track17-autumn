package ru.track.prefork;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class Worker {
    private static Logger log = LoggerFactory.getLogger(Server.class);
    boolean deadSession = false;
    final long id;
    final Socket socket;
    private InputStream inputStream;
    OutputStream outputStream;

    Worker(long id, Socket socket) {
        this.id = id;
        this.socket = socket;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Nullable
    Message listen() {
        byte[] buffer = new byte[2048];
        Message message = null;
        try {
            int nRead = inputStream.read(buffer);
            if (nRead > 0) {
                message = new Message(buffer, id, nRead);
            }
            if (nRead <= 0 || deadSession) {
                deadSession = true;
                log.info("Disconnected");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return message;
    }

    void endSession() {
        try {
            deadSession = true;
            inputStream.close();   //
            outputStream.close();  //  necessery?
            socket.close();
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    void drop() {
        deadSession = true;
    }
}
