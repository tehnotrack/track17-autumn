package ru.track;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {
    public static void main(String[] args) {
        try {
            Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                    .field("name", "Timur Anosov")
                    .field("github", "timuranosov")
                    .field("email", "tumyp.anosov@gmail.com")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
