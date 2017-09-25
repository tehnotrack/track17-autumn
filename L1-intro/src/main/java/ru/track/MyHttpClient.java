package ru.track;

import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {
    public static void main(String[] args) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                    .header("accept", "application/json")
                    .queryString("apiKey", "123")
                    .field("name", "Egeniy Mecheryakov")
                    .field("github", "mesher-x")
                    .field("email", "mesher.x@icloud.com")
                    .asJson();
            System.out.println(jsonResponse.getBody());
        } catch (UnirestException e) {
            System.out.println(e.getMessage());
        }
    }
}