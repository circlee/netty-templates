package com.eldie.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.logging.InternalLogger;

import static io.netty.util.internal.logging.InternalLoggerFactory.getInstance;


public class HttpHandler extends ChannelInboundHandlerAdapter {




    @Override
    public void channelActive(ChannelHandlerContext ctx) {


    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        InternalLogger logger = getInstance(getClass());

        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            logger.info(httpRequest.headers().toString());
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

        ctx.write(response);
        ctx.flush();
        ctx.close();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}