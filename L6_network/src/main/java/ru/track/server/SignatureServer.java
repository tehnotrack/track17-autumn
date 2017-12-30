package ru.track.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Signature;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ru.track.server.SignatureUtils.forSigning;
import static ru.track.server.SignatureUtils.sign;
import static ru.track.server.SignatureUtils.update;

public class SignatureServer {

    private final int port;

    private final int backlog;

    @Nullable
    private final InetAddress bindAddr;

    @Nullable
    private final ExecutorService pool;

    public SignatureServer(int port, int backlog, @Nullable InetAddress bindAddr, @Nullable ExecutorService pool) {
        this.port = port;
        this.backlog = backlog;
        this.bindAddr = bindAddr;
        this.pool = pool;
    }

    private void handleImpl(@NotNull Socket sock, byte[] buffer) throws IOException {
        final InputStream is = sock.getInputStream();
        final Signature sig = forSigning(SECRET_EXPONENT);

        for (int n = 0; n != -1; n = is.read(buffer)) {
            update(sig, buffer, n);
        }
        sock.shutdownInput();

        final OutputStream os = sock.getOutputStream();
        os.write(sign(sig));
        sock.shutdownOutput();
    }

    private void handle(@NotNull ServerSocket ssock) {
        Socket sock = null;
        try {
            final byte[] buffer = new byte[4096];
            sock = ssock.accept();
            handleImpl(sock, buffer);
        } catch (IOException e) {
            throw new RuntimeException(e); // may be called from lambda
        } finally {
            IOUtils.closeQuietly(sock);
        }
    }

    public void serve() throws IOException {
        ServerSocket ssock = null;
        try {
            final ServerSocket ssockFinal = new ServerSocket(port, backlog, bindAddr);
            ssock = ssockFinal;
            //noinspection InfiniteLoopStatement
            while (true) {
                pool.execute(() -> handle(ssockFinal));
            }
        } finally {
            IOUtils.closeQuietly(ssock);
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        final SignatureServer server = new SignatureServer(8100, 10, null, pool);
        server.serve();
    }

    private static final BigInteger SECRET_EXPONENT = new BigInteger("781c74a8caa520768990b9af9c047a3221480c7f02a6a72660a09af16c3072c77c4a55452add8a26773b579e0d81dc48cbfa4cd971a0c346ef6ccff89f3c4899", 0x10);

}
