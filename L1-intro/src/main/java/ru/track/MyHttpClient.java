package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {

    public static void main(String[] args) throws UnirestException {
        HttpResponse<String> r = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                .field("name", "Maxim Galaganov")
                .field("github", "https://github.com/darkwrat/track17-autumn")
                .field("email", "m.galaganov@corp.mail.ru")
                .asString();
        System.out.printf("code: %s; headers: %s; body: %s\n", r.getStatusText(), r.getHeaders(), r.getBody());
    }

}
