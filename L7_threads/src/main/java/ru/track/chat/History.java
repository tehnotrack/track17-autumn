package ru.track.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.chat.parameters.Convertible;
import ru.track.chat.parameters.Parameters;
import ru.track.prefork.Message;
import ru.track.prefork.database.Database;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class History extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger("logger");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Database              database   = Database.getInstance();
        Map<String, String[]> parameters = request.getParameterMap();
    
        long fromDefault  = System.currentTimeMillis() - 60 * 60 * 24 * 7 * 1000;
        long toDefault    = System.currentTimeMillis();
        long limitDefault = 10;
    
        long from  = Parameters.getParameter(parameters.get("from"), fromDefault, new LongConverter());
        long to    = Parameters.getParameter(parameters.get("to"), toDefault, new LongConverter());
        long limit = Parameters.getParameter(parameters.get("limit"), limitDefault, new LongConverter());
    
        List<Message> messages = null;
        try {
            messages = database.getHistory(from, to, limit);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    
        request.setAttribute("messages", messages);
        request.getRequestDispatcher("/history.jsp").forward(request, response);
    }
    
    private static class LongConverter implements Convertible<Long> {
        @Override
        public Long convert(String value, Long defaultValue) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    }
}
