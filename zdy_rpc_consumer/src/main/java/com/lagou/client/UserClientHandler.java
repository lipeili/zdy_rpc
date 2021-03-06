package com.lagou.client;

import com.lagou.subscriber.ServerProxyEntity;
import com.lagou.util.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class UserClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;
    private String result;
    private RpcRequest para;
    private ServerProxyEntity serverProxyEntity;

    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
    }

    /**
     * 收到服务端数据，唤醒等待线程
     */

    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
        result = msg.toString();
        notify();
    }

    /**
     * 写出数据，开始等待唤醒
     */

    public synchronized Object call() throws InterruptedException {
        context.writeAndFlush(para);
        wait();
        return result;
    }

    /*
     设置参数
     */
    void setPara(RpcRequest para) {
        this.para = para;
    }

    public ServerProxyEntity getServerProxyEntity() {
        return serverProxyEntity;
    }

    public void setServerProxyEntity(ServerProxyEntity serverProxyEntity) {
        this.serverProxyEntity = serverProxyEntity;
    }
}
