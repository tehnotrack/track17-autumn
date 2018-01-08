package ru.track.chat.utils;

import com.google.gson.Gson;
import ru.track.prefork.Message;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SendInfo {
    private static Gson gson = new Gson();
    
    public void sendMessage(List<String> errors, List<Message> messages, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(gson.toJson(new Info(errors, messages)));
    }
    
    private static class Info {
        final List<String> errors;
        final List<Message> messages;
        
        private Info(List<String> errors, List<Message> messages) {
            this.errors = errors;
            this.messages = messages;
        }
    }
}
