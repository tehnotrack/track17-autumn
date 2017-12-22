package ru.track.chat;

import ru.track.prefork.Message;
import ru.track.prefork.database.Database;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class History extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Database database = Database.getInstance();
        
        try {
            List<Message> messages = database.getHistory(System.currentTimeMillis() - 60 * 60 * 24 * 7 * 1000, System.currentTimeMillis(), 10);
    
            for (Message message : messages) {
                System.out.println(message.getText());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        request.getRequestDispatcher("/history.jsp").forward(request, response);
    }
}
