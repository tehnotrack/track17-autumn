package ru.track;

import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws UnirestException
    {
        HttpResponse<String> result = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
        .field("name", "Ilya Vorobev")
        .field("github", "https://github.com/PeYceBall")
        .field("email", "vorobev.iv@phystech.edu")
        .asString();
        System.out.println( result.getBody() );
    }
}
