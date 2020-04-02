package com.lagou.client;

import com.alibaba.fastjson.JSON;
import com.lagou.subscriber.ServerProxyEntity;
import com.lagou.subscriber.Subscriber;
import com.lagou.util.JSONSerializer;
import com.lagou.util.RpcEncoder;
import com.lagou.util.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcConsumer {

    //创建线程池对象
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static UserClientHandler userClientHandler;

    //1.创建一个代理对象 providerName：UserService#sayHello are you ok?
    public Object createProxy(final Class<?> serviceClass){
        //借助JDK动态代理生成代理对象
        return  Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{serviceClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                long startMills = System.currentTimeMillis();
                //（1）调用初始化netty客户端的方法

//                if(userClientHandler == null){
                 initClient();
//                }

                // 设置参数
                RpcRequest request = new RpcRequest();
                request.setClassName("com.lagou.service.UserService");
                request.setMethodName("sayHello");
                request.setParameterTypes(new Class[]{String.class});
                request.setParameters(new String[]{"are you ok?"});
                userClientHandler.setPara(request);
                Object response = executor.submit(userClientHandler).get();

                long endMills = System.currentTimeMillis();
                System.out.println(JSON.toJSONString(userClientHandler.getServerProxyEntity()));

                userClientHandler.getServerProxyEntity().setLastInvokeCostMills(endMills - startMills);
                return response;
            }
        });
    }

    //2.初始化netty客户端
    public static  void initClient() throws InterruptedException {
         userClientHandler = new UserClientHandler();

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
//                        pipeline.addLast(new StringEncoder());
//                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new RpcEncoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(userClientHandler);
                    }
                });

        Map<String, ServerProxyEntity> serverMap = Subscriber.serverMap;

        ServerProxyEntity selectIpPort = null;
        for (Map.Entry<String,ServerProxyEntity> entry : serverMap.entrySet()) {
            ServerProxyEntity serverProxyEntity = entry.getValue();
            
            if (null == selectIpPort) {
                selectIpPort = serverProxyEntity;
            }
            
            if (null == serverProxyEntity.getLastInvokeMills()) {
                selectIpPort = serverProxyEntity;
                break;
            }

            if (System.currentTimeMillis() - serverProxyEntity.getLastInvokeMills() > 5000) {
                selectIpPort = serverProxyEntity;
                break;
            }

            if(selectIpPort.getLastInvokeCostMills() > serverProxyEntity.getLastInvokeCostMills()){
                selectIpPort = serverProxyEntity;
            }
        }

        selectIpPort.setLastInvokeMills(System.currentTimeMillis());
        userClientHandler.setServerProxyEntity(selectIpPort);

        bootstrap.connect(selectIpPort.getIpAddress(),selectIpPort.getPort()).sync();
//        userClientHandler
    }

}
