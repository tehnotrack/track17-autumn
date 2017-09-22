package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {
    public static void main(String[] args){
        HttpResponse<String> r = null;
        try {
            r = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                    .field("name", "materkey")
                    .field("github", "https://github.com/materkey")
                    .field("email", "vyacheslav.kovalev@phystech.edu")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        System.out.println(r);
    }
}