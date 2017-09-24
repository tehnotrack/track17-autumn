package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {
    public static void main(String[] args) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                    .header("accept", "application/json")
                    .field("name", "Andrei Pago")
                    .field("github", "andpago")
                    .field("email", "andpago@gmail.com")
                    .asJson();
            System.out.println(jsonResponse.getBody());
        } catch (UnirestException e) {
            System.out.println("something went wrong");
        }
    }
}
