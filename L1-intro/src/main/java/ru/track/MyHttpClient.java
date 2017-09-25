package ru.track;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.*;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class MyHttpClient {
    public static void main(String[] args) {
        try {
            HttpResponse<String> jsonResponse = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                    .field("name", "Robert Voropaev")
                    .field("github", "RobertVoropaev")
                    .field("email", "robertvoropaev11@gmail.com")
                    .asString();
        }
        catch (UnirestException ex){

        }
    }
}
