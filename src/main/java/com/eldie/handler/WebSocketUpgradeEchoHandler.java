package com.eldie.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.internal.logging.InternalLogger;

import static io.netty.util.internal.logging.InternalLoggerFactory.getInstance;


public class WebSocketUpgradeEchoHandler extends ChannelInboundHandlerAdapter {


    InternalLogger logger = getInstance(getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) {


    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {


        if(msg instanceof HttpRequest) {

            HttpRequest httpRequest = (HttpRequest) msg;

            HttpHeaders headers =  httpRequest.headers();

            String connection = headers.get(HttpHeaderNames.CONNECTION);
            String upgrade = headers.get(HttpHeaderNames.UPGRADE);

            if(HttpHeaderNames.UPGRADE.contentEqualsIgnoreCase(connection) && "websocket".equalsIgnoreCase(upgrade)) {

                ctx.pipeline().replace(this, "websocketHandler", new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) {

                        if (msg instanceof WebSocketFrame) {
                            System.out.println("This is a WebSocket frame");
                            System.out.println("Client Channel : " + ctx.channel());
                            if (msg instanceof BinaryWebSocketFrame) {
                                System.out.println("BinaryWebSocketFrame Received : ");
                                System.out.println(((BinaryWebSocketFrame) msg).content());
                            } else if (msg instanceof TextWebSocketFrame) {
                                System.out.println("TextWebSocketFrame Received : ");
                                ctx.channel().writeAndFlush(
                                        new TextWebSocketFrame("Message recieved : " + ((TextWebSocketFrame) msg).text()));
                                System.out.println(((TextWebSocketFrame) msg).text());
                            } else if (msg instanceof PingWebSocketFrame) {
                                System.out.println("PingWebSocketFrame Received : ");
                                System.out.println(((PingWebSocketFrame) msg).content());
                            } else if (msg instanceof PongWebSocketFrame) {
                                System.out.println("PongWebSocketFrame Received : ");
                                System.out.println(((PongWebSocketFrame) msg).content());
                            } else if (msg instanceof CloseWebSocketFrame) {
                                System.out.println("CloseWebSocketFrame Received : ");
                                System.out.println("ReasonText :" + ((CloseWebSocketFrame) msg).reasonText());
                                System.out.println("StatusCode : " + ((CloseWebSocketFrame) msg).statusCode());
                            } else {
                                System.out.println("Unsupported WebSocketFrame");
                            }
                        }
                    }
                });

                System.out.println("WebSocketHandler added to the pipeline");
                System.out.println("Opened Channel : " + ctx.channel());
                System.out.println("Handshaking....");
                //Do the Handshake to upgrade connection from HTTP to WebSocket protocol
                handleHandshake(ctx, httpRequest);
                System.out.println("Handshake is done");

            }

        }




    }


    protected void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(getWebSocketURL(req), null, true);
       WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    protected String getWebSocketURL(HttpRequest req) {
        System.out.println("Req URI : " + req.uri());
        String url =  "ws://" + req.headers().get("Host") + req.uri();
        System.out.println("Constructed URL : " + url);
        return url;
    }
}