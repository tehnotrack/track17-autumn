package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * TASK:
 * POST request to  https://guarded-mesa-31536.herokuapp.com/db
 * fields: name,github,email
 *
 * LIB: http://unirest.io/java.html
 *
 *
 */
public class App {

    public static void main(String[] args) {
    //    System.out.println("Hello, " + args[0]);
        HttpResponse<JsonNode> response;
        try {
            response = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
                    .field("name", "Roman" )
                    .field("github","manwithhonor")
                    .field("email", "sharapov.roman@gmail.com")
                    .asJson();
            System.out.println(response.getBody());

        } catch (UnirestException e) {
            e.printStackTrace();
            //System.out.println("attemt failed" );
        }

    }

}
