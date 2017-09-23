package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {
    public static void main(String[] args) throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                .header("accept", "application/json")
                .field("name", "Nifantova Irina")
                .field("github", "https://github.com/NifantovaIrina")
                .field("email", "nifantova98@gmail.com")
                .asJson();
    }


}
