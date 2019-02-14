package com.eldie;

import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.ThreadPerTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Created by circlee on 2019. 2. 8..
 */
public class MainServer {

    public static void main(String[] args) {


        Executor excutor = new ThreadPerTaskExecutor(new DefaultThreadFactory(MainServer.class));

        excutor.execute(() -> {
                // echo server
                new EchoServer(8800).bootServer();
        });

        excutor.execute(() -> {
            // http server
            new HttpServer(8080).bootServer();
        });



    }
}
