package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {
    public static void main(String[] args) throws UnirestException {
        HttpResponse<JsonNode> jsonResponse =
                Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                .field("name", "Alexander Kosov")
                .field("github", "AVKrepo")
                .field("email", "kosov-av@yandex.ru")
                .asJson();
        System.out.println("The response: " + jsonResponse.getBody());
    }
}
