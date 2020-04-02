package com.lagou.client;

import com.lagou.register.Register;
import com.lagou.service.UserService;
import com.lagou.subscriber.Subscriber;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ClientBootStrap implements InitializingBean {

    public void afterPropertiesSet() throws Exception {
        init();
        RpcConsumer rpcConsumer = new RpcConsumer();
        UserService proxy = (UserService) rpcConsumer.createProxy(UserService.class);

        while (true){
            Thread.sleep(2000);
            System.out.println(proxy.sayHello("are you ok?"));
        }
    }

    public void init() {
        new Thread(new Runnable() {
            public void run() {
                Subscriber subscriber = new Subscriber();
                System.out.println(subscriber);
            }
        }).start();

    }

}
