package ru.track;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {

    public static void main(String[] args)throws UnirestException{

        Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                .field("name", "Noskova Elizaveta")
                .field("github", "https://github.com/Lizanoskova")
                .field("email", "enoskova.mipt@gmail.com")
                .asString();
    }
}

