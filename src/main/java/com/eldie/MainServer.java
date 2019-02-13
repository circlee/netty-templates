package com.eldie;

/**
 * Created by circlee on 2019. 2. 8..
 */
public class MainServer {

    public static void main(String[] args) {

        // echo server
       //new EchoServer(8800).bootServer();

        // http server
        new HttpServer(8080).bootServer();

    }
}
