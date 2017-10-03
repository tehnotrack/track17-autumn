package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {
    public static void main(String[] args) throws UnirestException {
        HttpResponse<JsonNode> r = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                .field("name", "Grigory Kovtun")
                .field("github", "mrgrigorii")
                .field("email", "mrgrigorii@mail.ru")
                .asJson();
        System.out.println("The response: " + r.getBody());
    }
}