package track.servlet.container;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class History extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String[]> params = req.getParameterMap();

        if (params.containsKey("limit")){
            ConversationService conv = new ConversationService();

            List<Message> msgHistory = conv.getHistory(0, System.currentTimeMillis(),Long.parseLong(req.getParameter("limit")));
            req.setAttribute("msgList", msgHistory);
            req.getRequestDispatcher("/history.jsp").forward(req, resp);
        }else {
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("<h1>Sorry, but u haven't entered limit field(</h1>");
        }
    }
}
