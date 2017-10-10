package ru.track.io.vendor;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLConnection;

//
//        )                       )          (
//     ( /(    (   (      (    ( /(    (     )\ )  (       )  (  (
//     )\())  ))\  )(    ))\   )\())  ))\   (()/(  )(   ( /(  )\))(  (    (     (
//    ((_)\  /((_)(()\  /((_) ((_)\  /((_)   ((_))(()\  )(_))((_))\  )\   )\ )  )\
//    | |(_)(_))   ((_)(_))   | |(_)(_))     _| |  ((_)((_)_  (()(_)((_) _(_/( ((_)
//    | ' \ / -_) | '_|/ -_)  | '_ \/ -_)  / _` | | '_|/ _` |/ _` |/ _ \| ' \))(_-<
//    |_||_|\___| |_|  \___|  |_.__/\___|  \__,_| |_|  \__,_|\__, |\___/|_||_| /__/
//                                                           |___/
//

public final class Bootstrapper extends HttpServlet {

    @NotNull
    private final String finName;

    @NotNull
    private final FileEncoder encoder;

    public Bootstrapper(@NotNull String[] args, @NotNull FileEncoder encoder) throws IllegalArgumentException {
        if (args.length < 1) {
            throw new IllegalArgumentException();
        }
        finName = args[0];
        if (encoder instanceof ReferenceTaskImplementation) {
            System.out.println("--> Cheaters must die! <--");
        }
        this.encoder = encoder;
    }

    public void bootstrap(@NotNull String pathSpec, @NotNull InetSocketAddress addr) throws Exception {
        final ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(this), pathSpec);
        final Server server = new Server(addr);
        server.setHandler(handler);
        server.start();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        final File tempFile = File.createTempFile("jetty.enc_", ".txt");
        try {
            final File encoded = encoder.encodeFile(finName, tempFile.getCanonicalPath());
            final ServletOutputStream os = resp.getOutputStream();
            try (final InputStream is = new FileInputStream(encoded)) {
                os.print("<img src=\"data:");
                os.print(URLConnection.guessContentTypeFromName(finName));
                os.print(";base64,");
                IOUtils.copy(is, os);
                os.print("\" alt=\"pretty_pic\"/>");
            }
        } finally {
            boolean deleted = tempFile.delete(); // result unused
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
