package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import jdk.nashorn.internal.runtime.JSONFunctions;

/**
 * Created by karina-pc on 24.09.2017.
 */
public class myHttpClient {
    public static void main (String[] args) throws Exception {
        HttpResponse<String> r = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                .field("name", "Karina")
                .field("github", "Karina1997")
                .field("email", "karinaanton77@gmail.com").asString();
            }
}
