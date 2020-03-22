package com.lagou.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    private Serializer serializer;

    private volatile int len=0;

    public RpcDecoder(Class<?> clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        int length = len > 0 ? len : (byteBuf.readableBytes() >= 4 ? byteBuf.readInt() : 0);
        if(byteBuf.readableBytes()>=length&&length>0) {

            byte[] bytes = new byte[length];
            byteBuf.readBytes(bytes);
            out.add(serializer.deserialize(clazz, bytes));
            len=0;
        }else {
            len = length;
        }

    }
}
