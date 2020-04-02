package com.lagou.server;

import com.lagou.register.Register;
import com.lagou.service.UserServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ServerBootstrap implements InitializingBean {

    @Autowired
    private ConfigurableEnvironment env;

    public void init() {
        new Thread(new Runnable() {
            public void run() {
                String hostName = env.getProperty("hostName");
                String port = env.getProperty("port");

                if (StringUtils.isEmpty(hostName)) {
                    hostName = "127.0.0.1";
                }

                if (StringUtils.isEmpty(port)) {
                    port = "8080";
                }

                try {
                    UserServiceImpl.startServer(hostName, Integer.parseInt(port));
                    System.out.println("rpc server has started");
                    Register.registe("rpc_server", hostName, Integer.parseInt(port));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void afterPropertiesSet() {
        init();
    }
}
