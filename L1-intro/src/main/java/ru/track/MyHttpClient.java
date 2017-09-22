package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class MyHttpClient {
    public static void main(String[] args) throws Exception {
        HttpResponse<String> r = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                    .field("name", "test ")
                    .field("github", "test ")
                    .field("email", "test")
                    .asString();
        System.out.println(r);
    }
}
