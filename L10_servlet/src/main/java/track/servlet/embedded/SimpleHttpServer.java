package track.servlet.embedded;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Запускаем jetty-сервер из java кода (embedded mode)
 * В качестве обработчика http запросов отдаем ему текущий класс, который extends AbstractHandler
 *
 * Можно запустить класс через main()
 *
 * Browser:
 *   localhost:8082
 *   localhost:8082?x=100&y=200
 */
public class SimpleHttpServer extends AbstractHandler {

    public static final int PORT = 8082;

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {

        String x = request.getParameter("x");
        String y = request.getParameter("y");
        if (StringUtils.isNotEmpty(x) && StringUtils.isNotEmpty(y)) {
            try {
                int r = Integer.parseInt(x) + Integer.parseInt(y);
                response.getWriter().println("<h1>r=" + r + "</h1>");
            } catch (Exception e) {
                response.getWriter().println("<h1>error</h1>");
            }
        }

        response.setContentType("text/html;charset=utf-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(HttpServletResponse.SC_OK);

        baseRequest.setHandled(true);

    }

    public static void main(String[] args) throws Exception {

        // Server из библиотеки jetty
        Server server = new Server(PORT);

        // Обработчик соединения - наш класс
        server.setHandler(new SimpleHttpServer());

        server.start();
        server.join();
    }
}