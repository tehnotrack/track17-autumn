package track.servlet.container;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;

public class SendToBase extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String[]> params = req.getParameterMap();

        if (params.containsKey("username") && params.containsKey("text")) {
            if (!req.getParameter("username").equals("")) {
                ConversationService conv = new ConversationService();

                Message msg = new Message();
                msg.ownerLogin = req.getParameter("username");
                msg.text = req.getParameter("text");
                conv.store(msg);
            }else {
                resp.setContentType("text/html");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println("<h1>Username must contains at least 1 char</h1>");
            }
        }else {
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("<h1>Sorry, but u haven't entered username or text field(</h1>");
        }
    }
}
