package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class MyHttpClient {
  public static void main(String[] args) throws Exception {
    HttpResponse<String> jsonResponse = Unirest.post("https://guarded-mesa-31536.herokuapp.com/track")
               .field("name","Dmitriy Goryachiy")
               .field("github", "https://github.com/DmitriyGoryachiy/")
               .field("email", "goryachii@phystech.edu")
               .asString();
    System.out.println(jsonResponse.getBody());
  }
}