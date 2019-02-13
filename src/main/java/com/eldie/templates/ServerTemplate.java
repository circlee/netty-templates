package com.eldie.templates;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Created by circlee on 2019. 2. 12..
 */
public abstract class ServerTemplate {

    private int port;

    public ServerTemplate(int port){
        this.port = port;
    }


    public void bootServer(){
        EventLoopGroup rootGroup = new NioEventLoopGroup(1);
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try{

            ServerBootstrap sb = new ServerBootstrap();


            ServerBootstrap serverBootstrap =  sb.group(rootGroup, childGroup);
            bindServerBootStrap(serverBootstrap);


            ChannelFuture cf = sb.bind(this.port).sync();
            cf.channel().closeFuture().sync();

        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            rootGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

    public abstract void bindServerBootStrap(ServerBootstrap serverBootstrap);

}
