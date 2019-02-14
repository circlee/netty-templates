package com.eldie;

import com.eldie.handler.EchoServerHandler;
import com.eldie.templates.ServerTemplate;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;



/**
 * Created by circlee on 2019. 2. 8..
 */
public class EchoServer extends ServerTemplate {

    public EchoServer(int port) {
        super(port);
    }

    @Override
    public void bindServerBootStrap(ServerBootstrap serverBootstrap) {

        serverBootstrap.channel(NioServerSocketChannel.class)

                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        ChannelPipeline cp = sc.pipeline();
                        cp.addLast(new EchoServerHandler());
                    }
                });

    }
}
