package com.eldie;

import com.eldie.handler.EchoClientHandler;
import com.eldie.templates.ClientTemplate;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.ThreadPerTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Created by circlee on 2019. 2. 8..
 */
public class MainClient {

    public static void main(String[] args) {


        Executor excutor = new ThreadPerTaskExecutor(new DefaultThreadFactory(MainClient.class));

        excutor.execute(() -> {
            // http server
            new ClientTemplate("127.0.0.1", 8800) {

                @Override
                public void bindBootStrap(Bootstrap bootstrap) {
                    bootstrap.channel(NioSocketChannel.class)
                            //.option(ChannelOption.SO_BACKLOG, 100)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel sc) throws Exception {
                                    ChannelPipeline cp = sc.pipeline();
                                    cp.addLast(new EchoClientHandler());
                                }
                            });

                }
            }.bootServer();
        });



    }
}
