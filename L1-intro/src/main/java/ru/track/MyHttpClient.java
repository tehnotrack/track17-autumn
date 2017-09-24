package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MyHttpClient {

    public static void main(String[] args) {

        try {
            HttpResponse<JsonNode> postResponse = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                    .header("accept", "application/json")
                    .field("name", "Gubarenko Mikhail")
                    .field("github", "dmdgik")
                    .field("email", "dmdgik@gmail.com")
                    .asJson();

            System.out.println(postResponse.getBody());
        }
        catch (UnirestException e) {
            System.out.println(e.toString());
        }

    }

}
