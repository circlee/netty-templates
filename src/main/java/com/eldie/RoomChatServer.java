package com.eldie;

import com.eldie.handler.RoomChatServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by circlee on 2019. 2. 15..
 */
public class RoomChatServer {


    public static void main(String[] args) {
        new RoomChatServer().bootServer();
    }

    public void bootServer() {


        EventLoopGroup parentGroup = new NioEventLoopGroup(1);
        EventLoopGroup childGroup = new NioEventLoopGroup(4);

        try {


            Map<String, ChannelGroup> roomChannels = new ConcurrentHashMap<>();
            ServerBootstrap sb = new ServerBootstrap();

            sb.channel(NioServerSocketChannel.class);


            sb.group(parentGroup, childGroup)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new RoomChatServerHandler(roomChannels));

                        }
                    });

            ChannelFuture cf = sb.bind(10090).sync();
            cf.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }

    }
}
