package ru.track;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class MyHttpClient{

    public static void myPost() throws UnirestException{
        Unirest.post("https://guarded-mesa-31536.herokuapp.com/track").
                field("name", "Semjon Glushkov").
                field("github", "https://github.com/Senbjorn").
                field("email", "semjonglushkov@yandex.ru").
                asString();
    }
}