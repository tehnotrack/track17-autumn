package ru.track;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {

    public static void main(String[] args)throws Exception{

        Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                .queryString("name", "Noskova Elizaveta")
                .queryString("github", "https://github.com/Lizanoskova")
                .queryString("email", "enoskova.mipt@gmail.com")
                .asString();
    }
}

