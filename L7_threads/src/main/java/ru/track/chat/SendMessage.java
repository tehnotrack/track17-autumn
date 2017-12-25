package ru.track.chat;

import com.google.gson.Gson;
import ru.track.chat.parameters.Convertible;
import ru.track.chat.parameters.Parameters;
import ru.track.prefork.Message;
import ru.track.prefork.database.Database;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

public class SendMessage extends HttpServlet {
    private static Gson gson = new Gson();
    
    private void sendMessage(Object object, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(gson.toJson(object));
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String[]> parameters = request.getParameterMap();
    
        String username = Parameters.getParameter(parameters.get("username"), "", new Convertible<String>() {
        });
        String text = Parameters.getParameter(parameters.get("text"), "", new Convertible<String>() {
        });
    
        LinkedList<String>  errors   = new LinkedList<>();
        LinkedList<Message> messages = new LinkedList<>();
    
        if (username.isEmpty()) {
            errors.add("Username is required");
        
            sendMessage(new SendInfo(errors, messages), response);
        
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
    
        sendMessage(new SendInfo(errors, messages), response);
    }
    
    private static class SendInfo {
        public LinkedList<String>  errors;
        public LinkedList<Message> messages;
        
        public SendInfo(LinkedList<String> errors, LinkedList<Message> messages) {
            this.errors = errors;
            this.messages = messages;
        }
    }
}
