package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class MyHttpClient {

    public static void main(String[] args) throws Exception {
        HttpResponse<JsonNode> json = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
            .field("name", "Ivakhnenko Maxim")
            .field("github", "makci97")
            .field("email", "makci.97@mail.ru")
                .asJson();

        System.out.println(json.getBody());
    }
}
