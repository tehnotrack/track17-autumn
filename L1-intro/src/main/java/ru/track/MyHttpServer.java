package ru.track;


import com.mashape.unirest.http.Unirest;

public class MyHttpServer {

    public static void main(String[] args) {
      Unirest.post("http://httpbin.org/post")
        .field("name", "Semyon")
        .field("github", "https://github.com/S3tmefree")
        .field("email", "bloya@mail.ru");
    }
}
