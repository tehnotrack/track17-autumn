package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by ilya on 25.09.17.
 */
public class MyHttpClient {
    public static void main(String[] args) {
        try {
            System.out.println(111);
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                    .field("name", "Ilya")
                    .field("github", "Ilya1510")
                    .field("email", "ilya151098@yandex.ru")
                    .asJson();
            System.out.println("ura");
            System.out.println(jsonResponse.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
