package com.lagou.server;

import com.lagou.service.UserServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class ServerBootstrap implements InitializingBean {


    public void afterPropertiesSet() throws Exception {
        UserServiceImpl.startServer("127.0.0.1",8990);
        System.out.println("rpc server has started");
    }

}
