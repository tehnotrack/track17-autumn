package ru.track.chat;

import ru.track.chat.utils.SendInfo;
import ru.track.prefork.Message;
import ru.track.prefork.database.Database;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class CheckNewMessages extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Database database = Database.getInstance();
        
        long currentTime = System.currentTimeMillis();
        
        List<Message> messages = null;
        
        try {
            messages = database.getHistory(currentTime - 2 * 1000, currentTime, 10);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        LinkedList<String> errors = new LinkedList<>();
        
        SendInfo sendInfo = new SendInfo();
        
        sendInfo.sendMessage(errors, messages, response);
    }
}
