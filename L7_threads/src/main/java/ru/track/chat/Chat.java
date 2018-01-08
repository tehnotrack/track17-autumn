package ru.track.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.chat.parameters.Converter;
import ru.track.chat.parameters.Parameters;
import ru.track.chat.parameters.StringConverter;
import ru.track.prefork.Message;
import ru.track.prefork.database.Database;
import ru.track.prefork.database.exceptions.InvalidAuthor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Chat extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger("logger");
    private Database database = Database.getInstance();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String[]> parameters = request.getParameterMap();
    
        long fromDefault = System.currentTimeMillis() - 60 * 60 * 24 * 7 * 1000;
        long toDefault = System.currentTimeMillis();
        long limitDefault = 10;
    
        LongConverter longConverter = new LongConverter();
    
        long from = Parameters.getParameter(parameters.get("from"), fromDefault, longConverter);
        long to = Parameters.getParameter(parameters.get("to"), toDefault, longConverter);
        long limit = Parameters.getParameter(parameters.get("limit"), limitDefault, longConverter);
        String user = Parameters.getParameter(parameters.get("user"), "", new StringConverter());
        
        List<Message> messages = null;
        List<String> errors = new LinkedList<>();
        
        try {
            if (user.isEmpty()) {
                messages = database.getHistory(from, to, limit);
            } else {
                messages = database.getByUser(user, limit);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
    
            errors.add(e.getMessage());
        } catch (InvalidAuthor e) {
            errors.add(e.getMessage());
        }
    
        request.setAttribute("errors", errors);
        request.setAttribute("messages", messages);
        request.getRequestDispatcher("/chat.jsp").forward(request, response);
    }
    
    private static class LongConverter implements Converter<Long> {
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
