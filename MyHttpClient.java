package ru.track;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by kirill on 25.09.17.
 */
public class MyHttpClient {
    public static void main(String[] args) throws UnirestException{

        Unirest.post("https://guarded-mesa-31536.herokuapp.com/track").
                field("name", "Akimov Kirill ").
                field("github", "https://github.com/AtlasKirill ").
                field("email", "akimowkirill@gmail.com ").
                asString();
    }
}
