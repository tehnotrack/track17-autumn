package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by dmitrybelyaev on 21.09.17.
 */
public class MyHttpClient {

    public static void main(String args[]) {
        try {
            HttpResponse httpResponse = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                    .field("name", "Dmitry Belyaev")
                    .field("github", "dmitrybl")
                    .field("email", "dmitry.bl.5967@gmail.com")
                    .asJson();
            System.out.println(httpResponse.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
