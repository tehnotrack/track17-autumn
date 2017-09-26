package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {
    public static void main(String[] args) {
        try {
            HttpResponse<JsonNode> result = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                    .field("name", "Kompaniets Ilya")
                    .field("github", "Jenkslex")
                    .field("email", "jenkslex@gmail.com").asJson();

            System.out.println(result.getBody());
         }
        catch (UnirestException e) {
            System.err.println(e.toString());
        }
    }
}
