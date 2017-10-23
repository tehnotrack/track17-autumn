package ru.track.io.vendor;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

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

public final class Bootstrapper extends AbstractHandler {

    @NotNull
    private final String finName;

    @Nullable
    private final String foutName;

    @NotNull
    private final FileEncoder encoder;

    @NotNull
    private final Mustache m = (new DefaultMustacheFactory()).compile(new StringReader(TEMPLATE_DATA), "dummy");

    public Bootstrapper(String[] args, @NotNull FileEncoder encoder) throws IllegalArgumentException {
        if (args.length < 1) {
            throw new IllegalArgumentException();
        }
        finName = args[0];
        foutName = (args.length >= 2) ? args[1] : null;
        if (encoder instanceof ReferenceTaskImplementation) {
            System.out.println("--> Cheaters must die! <--");
        }
        this.encoder = encoder;
    }

    public void bootstrap(int port) {
        final Server server = new Server(new InetSocketAddress("127.0.0.1", port));
        server.setHandler(this);
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final String TEMPLATE_DATA = "<img src=\"data:{{contentType}};base64,{{data}}\" alt=\"pretty_pic\"/>";

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        httpServletResponse.setContentType("text/html;charset=utf8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

        final File encoded = encoder.encodeFile(finName, foutName);

        final Map<String, Object> scope = new HashMap<>();
        scope.put("contentType", URLConnection.guessContentTypeFromName(finName));
        scope.put("data", FileUtils.readFileToString(encoded, Charset.defaultCharset()));
        m.execute(httpServletResponse.getWriter(), scope);

        request.setHandled(true);
    }

}
