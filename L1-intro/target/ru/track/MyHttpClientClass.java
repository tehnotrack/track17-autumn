
package ru.track;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class MyHttpClientClass{
    public static void main(String[] args) throws UnirestException{

        Unirest.post("https://guarded-mesa-31536.herokuapp.com/track").
                field("name", "Daniil Gonchar ").
                field("github", "https://github.com/DaniilGonchar ").
                field("email", "dannygonchar@gmail.com ").
                asString();
    }
}
