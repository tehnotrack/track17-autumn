package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerIOByteProtocol extends ServerByteProtocol{

    public static Logger log = LoggerFactory.getLogger(Server.class);

    public ServerIOByteProtocol(Socket socket) {
        super(socket);
    }

    private int getIntLength() throws IOException, ServerByteProtocolException {
        InputStream input = getSocket().getInputStream();
        byte[] len = new byte[4];
        if (input.read(len, 0, 4) == -1) {
            throw new ServerByteProtocolException("end of stream");
        }
        int length = 0;
        for (int i = 0; i < 4; i++) {
            length += ((len[i] & (0xFF)) << ((3 - i) * 8));
        }
        return length;
    }

    private byte[] getByteLength(int len) {
        byte[] length = new byte[4];
        for (int i = 0; i < 4; i++) {
            length[i] = (byte) ((len >> (8 * (3 - i))) & (0xFF));
        }
        return length;
    }

    public byte[] read() throws ServerByteProtocolException {
        byte[] data = null;
        try {
            InputStream input = getSocket().getInputStream();
            int length = getIntLength();
            data = new byte[length];
            int NumberOfBytes = input.read(data);
            if (NumberOfBytes != data.length) {
                throw new ServerByteProtocolException("incorrect input");
            }
        } catch (IOException e) {
            throw new ServerByteProtocolException("reading failed", e);
        }
        return data;
    }

    public void write(byte data[]) throws ServerByteProtocolException {
        try {
            OutputStream output = getSocket().getOutputStream();
            output.write(getByteLength(data.length));
            output.write(data);
            output.flush();
        } catch (IOException e) {
            throw new ServerByteProtocolException("writing failed", e);
        }
    }

}
