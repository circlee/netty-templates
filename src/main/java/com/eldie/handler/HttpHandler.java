package com.eldie.handler;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.logging.InternalLogger;

import static io.netty.util.internal.logging.InternalLoggerFactory.getInstance;


public class HttpHandler extends ChannelInboundHandlerAdapter {


    InternalLogger logger = getInstance(getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) {


    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {



        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            logger.info("HttpRequest >>> {}", httpRequest.method());
            logger.info("HttpRequest >>> {}", httpRequest.uri().toString());
            logger.info("HttpRequest >>> {}", httpRequest.headers().toString());
            logger.info("HttpRequest >>> ctx : {}", ctx.hashCode());
        }



    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {


        logger.info("channelReadComplete >>> ctx : {}", ctx.hashCode());
        // HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        System.out.println(ctx.hashCode());
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1
                , HttpResponseStatus.OK
                , Unpooled.wrappedBuffer("TEST".getBytes()));
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