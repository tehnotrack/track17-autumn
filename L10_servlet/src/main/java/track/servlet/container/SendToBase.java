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
    private String[] param = {"username", "text"};
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String[]> params = req.getParameterMap();

        if (params.containsKey(param[0]) && params.containsKey(param[1])) {
            if (!req.getParameter(param[0]).equals("")) {
                ConversationService conv = new ConversationService();

                Message msg = new Message();
                msg.ownerLogin = req.getParameter(param[0]);
                msg.text = req.getParameter(param[1]);
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
