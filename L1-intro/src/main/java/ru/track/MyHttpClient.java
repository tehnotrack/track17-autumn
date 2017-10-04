package ru.track;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {

    public static void main(String[] args) throws UnirestException {
        Unirest.post("https://guarded-mesa-31536.herokuapp.com/db")
                .field("name", "Semyon Cheban")
                .field("github", "https://github.com/S3tmefree")
                .field("email", "bloya@mail.ru")
                .asString();
    }
}