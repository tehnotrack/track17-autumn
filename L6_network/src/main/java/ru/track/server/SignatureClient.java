package ru.track.server;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.security.Signature;

import static ru.track.server.SignatureUtils.*;

public class SignatureClient {

    @NotNull
    private final String host;

    private final int port;

    public SignatureClient(@NotNull String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static boolean verifyImpl(@NotNull Socket sock, @NotNull InputStream data, byte[] buffer) throws IOException {
        final OutputStream os = sock.getOutputStream();
        final Signature sig = forChecking();

        for (int n = 0; n != -1; n = data.read(buffer)) {
            update(sig, buffer, n);
            os.write(buffer, 0, n);
        }
        sock.shutdownOutput();

        final InputStream is = sock.getInputStream();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (int n = 0; n != -1; n = is.read(buffer)) {
            baos.write(buffer, 0, n);
        }
        sock.shutdownInput();

        return check(sig, baos.toByteArray());
    }

    public boolean verify(@NotNull InputStream data) {
        Socket sock = null;
        try {
            sock = new Socket(host, port);
            final byte[] buffer = new byte[4096];
            return verifyImpl(sock, data, buffer);
        } catch (IOException e) {
            throw new RuntimeException(e); // may be called from lambda
        } finally {
            IOUtils.closeQuietly(sock);
        }
    }

    public static void main(String[] args) throws Exception {
        final SignatureClient client = new SignatureClient("127.0.0.1", 8100);
        final InputStream data = new ByteArrayInputStream("hello".getBytes());
        System.out.println("Signature valid: " + client.verify(data));
    }

}
