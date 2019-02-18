package com.eldie.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.UUID;

/**
 * Created by circlee on 2019. 2. 15..
 */
public class RoomChatServerHandler extends ChannelInboundHandlerAdapter {


    Map<String, ChannelGroup> roomChannels;

    public RoomChatServerHandler(Map<String, ChannelGroup> roomChannels) {
        super();
        this.roomChannels = roomChannels;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof ByteBuf) {
            ByteBuf byteBufMessage = (ByteBuf) msg;
            int size = byteBufMessage.readableBytes();

            // 읽을 수 있는 바이트의 길이만큼 바이트 배열을 초기화합니다.
            byte[] byteMessage = new byte[size];
            // for문을 돌며 가져온 바이트 값을 연결합니다.
            for (int i = 0; i < size; i++) {
                byteMessage[i] = byteBufMessage.getByte(i);
            }

            // 바이트를 String 형으로 변환합니다.
            String message = new String(byteMessage);

            Channel channel = ctx.channel();

            if (message.startsWith("join->")) {
                String joinPayload = message.replace("join->", "");

                String[] payloads = joinPayload.split("/", 2);

                String joinRoom = payloads[0];
                String userName = (payloads.length == 2  ? payloads[1] : "RAN-"+ UUID.randomUUID());

                channel.attr(AttributeKey.valueOf("ROOM")).set(joinRoom);
                channel.attr(AttributeKey.valueOf("USERNAME")).set(userName);

                ChannelGroup cg = this.roomChannels.computeIfAbsent(joinRoom, (key) -> {
                   return new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                });


                cg.add(channel);

                cg.stream().forEach(c -> {
                    if (c != channel) {
                        c.writeAndFlush(Unpooled.wrappedBuffer(("new memberJoined : "+ userName).getBytes()));
                    } else {
                        c.writeAndFlush(Unpooled.wrappedBuffer(("successful joined to ROOM->" + joinRoom).getBytes()));
                    }
                });

                return;
            }

            String room = (String) channel.attr(AttributeKey.valueOf("ROOM")).get();
            String userName = (String) channel.attr(AttributeKey.valueOf("USERNAME")).get();
            System.out.println(">>> ROOM[" + room + "] message>>> " + message);


            String payloadMessage = "[user : "+userName+"]" + message;
            ChannelGroup cg = this.roomChannels.get(room);

            if (cg != null) {
                cg.stream().forEach(c -> {

                    if (c != channel) {
                        c.writeAndFlush(Unpooled.wrappedBuffer(payloadMessage.getBytes()));
                    }
                });
            }

        }


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
