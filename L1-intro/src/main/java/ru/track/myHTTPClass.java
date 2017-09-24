package ru.track;

import com.mashape.unirest.http.*;

public class myHTTPClass {
    public static void main (String[] args) throws Exception
    {
        HttpResponse<String> r = Unirest.post ("https://guarded-mesa-31536.herokuapp.com/track")
        .field ("name", "Ivanov Vasiliy Pavlovich")
        .field ("github", "vasili4396")
        .field ("email", "vasili4396@mail.ru").asString();
        System.out.println(r);

    //.asString();
    //создать pull-request
    }

}
