package com.eldie;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by circlee on 2019. 2. 15..
 */
public class RoomChatClient {

    public static void main(String[] args) {
        new RoomChatClient().bootServer();
    }

    public void bootServer() {


        EventLoopGroup parentGroup = new NioEventLoopGroup(1);
        Executor executor = Executors.newFixedThreadPool(10);

        try {
            Bootstrap bs = new Bootstrap();
            bs.group(parentGroup);

            bs.channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            System.out.println("INIT Channel");

                            ch.pipeline()
                                    .addLast(new ChannelInboundHandlerAdapter(){

                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) {

                                            if(msg instanceof ByteBuf) {
                                                ByteBuf byteBufMessage = (ByteBuf)msg;
                                                int size = byteBufMessage.readableBytes();

                                                // 읽을 수 있는 바이트의 길이만큼 바이트 배열을 초기화합니다.
                                                byte [] byteMessage = new byte[size];
                                                // for문을 돌며 가져온 바이트 값을 연결합니다.
                                                for(int i = 0 ; i < size; i++){
                                                    byteMessage[i] = byteBufMessage.getByte(i);
                                                }

                                                // 바이트를 String 형으로 변환합니다.
                                                String message = new String(byteMessage);

                                                System.out.println("[from-server] -> " + message);

                                            }


                                        }
                                    });
                        }
                    });


            ChannelFuture cf = bs.connect(new InetSocketAddress("127.0.0.1", 10090)).sync();

            Channel channel = cf.channel();

            executor.execute(() -> {

                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String line = "";
                    try {
                        line = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (line == null) {
                        break;
                    }


                    if ("bye".equals(line.toLowerCase())) {
                        try {
                            channel.closeFuture().sync();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                    try {
                        System.out.println("input --> " + line);
                        channel.writeAndFlush(Unpooled.wrappedBuffer((line).getBytes())).sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            });


            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            parentGroup.shutdownGracefully();
        }


    }
}
