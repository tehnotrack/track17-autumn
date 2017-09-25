package ru.track;
/**
 * TASK:
 * POST request to  https://guarded-mesa-31536.herokuapp.com/db
 * fields: name,github,email
 *
 * LIB: http://unirest.io/java.html
 *
 *
 */
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient
{
    public static void main (String[] args) throws UnirestException
    {
        Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                .field("name", "Nikita Rudenko")
                .field("github", "/nLoro")
                .field("email", "Rudenko.N@outlook.com")
                .asJson();

    }
}