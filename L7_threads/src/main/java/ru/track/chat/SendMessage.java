package ru.track.chat;

import ru.track.chat.parameters.Convertible;
import ru.track.chat.parameters.Parameters;
import ru.track.chat.utils.SendInfo;
import ru.track.prefork.Message;
import ru.track.prefork.database.Database;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

public class SendMessage extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SendInfo sendInfo = new SendInfo();
        
        Map<String, String[]> parameters = request.getParameterMap();
    
        String username = Parameters.getParameter(parameters.get("username"), "", new Convertible<String>() {
        });
        String text = Parameters.getParameter(parameters.get("text"), "", new Convertible<String>() {
        });
    
        LinkedList<String>  errors   = new LinkedList<>();
        LinkedList<Message> messages = new LinkedList<>();
    
        if (username.isEmpty()) {
            errors.add("Username is required");
    
            sendInfo.sendMessage(errors, messages, response);
        
            return;
        }
    
        Message message = new Message(username, text, System.currentTimeMillis());
    
        Database database = Database.getInstance();
    
        try {
            database.store(message);
        } catch (Exception e) {
            e.printStackTrace(); // TODO: improve this
        }
    
        messages.add(message);
    
        sendInfo.sendMessage(errors, messages, response); // TODO: exception here, improve it
    }
    
}
