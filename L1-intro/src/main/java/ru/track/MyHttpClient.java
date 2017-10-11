package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by mariia on 04.10.2017.
 */
public class MyHttpClient {
    /**
     * It is my main!
     */
    public static void main(String[] args) {
        try {
            final HttpResponse<JsonNode> jsonResponse = Unirest.post(" https://guarded-mesa-31536.herokuapp.com/track")
                    .queryString("name", "Maria")
                    .field("github", "Cheshear")
                    .field("email", "popova.mariya@phystech.edu")
                    .asJson();
            System.out.println(jsonResponse.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
