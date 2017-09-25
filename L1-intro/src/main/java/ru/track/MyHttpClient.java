package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {
    public static void main(String[] args) {
        try {
            HttpResponse<String> response = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                    .field("name", "Arseny Kuskov")
                    .field("github", "github.com/kuskovars")
                    .field("email", "kuskov.as@phystech.edu")
                    .asString();
            System.out.printf("Status: %s; \n body: %s\n", response.getStatusText(), response.getBody());
        }
        catch (UnirestException err) {
            System.out.println(err.toString());
        }
    }
}
