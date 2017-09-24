package ru.track;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;
import org.apache.http.HttpRequest;

public class MyHttpClient {

    public static void main(String[] args) throws UnirestException {
        Unirest.post("https://guarded-mesa-31536.herokuapp.com/db")
                .field("name", "Semyon")
                .field("github", "https://github.com/S3tmefree")
                .field("email", "bloya@mail.ru")
                .asString();
    }
}