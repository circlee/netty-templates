package com.eldie.templates;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;

/**
 * Created by circlee on 2019. 2. 12..
 */
public abstract class ClientTemplate {

    private String hostName;
    private int port;

    public ClientTemplate(String hostName, int port){
        this.hostName = hostName;
        this.port = port;
    }


    public void bootServer(){
        EventLoopGroup clientGroup = new NioEventLoopGroup(1);
        try{

            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(clientGroup);

            bindBootStrap(bootstrap);

            ChannelFuture cf = bootstrap.connect(new InetSocketAddress(hostName, port)).sync();

            cf.channel().closeFuture().sync();

        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            clientGroup.shutdownGracefully();
        }
    }

    public abstract void bindBootStrap(Bootstrap bootstrap);

}
