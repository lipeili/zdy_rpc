package com.lagou.handler;

import com.lagou.service.UserService;
import com.lagou.service.UserServiceImpl;
import com.lagou.util.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class UserServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 判断是否符合约定，符合则调用本地方法，返回数据
        // msg:  UserService#sayHello#are you ok?
//        if(msg.toString().startsWith("UserService")){
//            UserServiceImpl userService = new UserServiceImpl();
//            String result = userService.sayHello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
//            ctx.writeAndFlush(result);
        if (null != msg) {
            RpcRequest request = (RpcRequest) msg;
            Class clazz = Class.forName(request.getClassName());
            UserService userService = (UserService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("调用成功--参数 "+args[0]);
                    return "success";
                }
            });

            String result = (String) clazz.getMethod(request.getMethodName(),request.getParameterTypes()).invoke(userService, request.getParameters());
            ctx.writeAndFlush(result);
        }


    }
}
