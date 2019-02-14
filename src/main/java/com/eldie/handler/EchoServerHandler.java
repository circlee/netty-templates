package com.eldie.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    private final ByteBuf firstMessage;


    public EchoServerHandler() {
        firstMessage = Unpooled.buffer(1024);
        for (int i = 0; i < firstMessage.capacity(); i++) {
            firstMessage.writeByte((byte) i);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        // ctx.writeAndFlush(firstMessage);
    }

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

            System.out.println("[SERVER][from - client]" + message);
        }

        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}