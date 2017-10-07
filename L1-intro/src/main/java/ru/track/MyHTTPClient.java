package ru.track;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHTTPClient {

    public static void main(String[] args) throws UnirestException {
        Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                .field("name", "Yuriy Zabegaev")
                .field("github", "Yuriyzabegaev")
                .field("email", "zabegaev99@gmail.com")
                .asString();
    }

}
