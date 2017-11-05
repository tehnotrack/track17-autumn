package ru.track.prefork;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class AloneThread implements Runnable {
    private Socket socket;
    private Map<String, Socket> map;
    private InputStream in;
    private int Msg;
    private byte[] msg = new byte[1024];
    private String str;
    private String name;

    public AloneThread(Socket s, Map<String, Socket> map) throws IOException {
        socket = s;
        this.map = map;
        in = socket.getInputStream();
    }

    public void run() {
        try {
            while (!socket.isClosed()) {
                Msg = in.read(msg);
                str = new String(msg, 0, Msg);
                System.out.println("Get from client "  + str);
                if (str == null || str.equals("exit")) {
                    socket.getOutputStream().write((str).getBytes());
                    break;
                }
                for (Map.Entry<String, Socket> entry : map.entrySet()) {
                    if (entry.getValue().equals(socket))
                        name = entry.getKey();
                }
                //System.out.println(map);
                for (Map.Entry<String, Socket> entry : map.entrySet()) {
                    if (!entry.getValue().equals(socket)) {
                        System.out.println("Sending to client " + entry.getValue().getPort() + str);
                        entry.getValue().getOutputStream().write((name + ">" + str).getBytes());
                    }
                }
            }
            System.out.println("closed conection : " + socket);
        } catch (IOException e) {
            System.err.println("IO Exception");
            map.values().remove(socket);
            e.printStackTrace();
            System.out.println(map);
        } finally {
            try {
                map.values().remove(socket);
                socket.close();
            } catch (IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }
}