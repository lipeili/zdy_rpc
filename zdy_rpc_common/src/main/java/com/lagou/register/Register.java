package com.lagou.register;

import com.lagou.util.ZkBase;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.io.File;

public class Register extends ZkBase{
    // 创建会话
    public static void registe(String registerName, String registerIp, int registerPort) throws Exception {


        CuratorFramework client = ZkBase.getClient();

        System.out.println("会话2创建了");

        // 创建节点
        String path = File.separatorChar + registerName + File.separatorChar + registerIp + ":" + registerPort;
        String s = client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath(path, (registerIp + ":" + registerPort).getBytes());

        System.out.println("节点递归创建成功，该节点路径" + s);

        Object lock = new Object();
        synchronized (lock) {
            lock.wait();
        }

    }

    public static void main(String[] args) throws Exception {
        Register.registe("rpc_server", "localhost", 8990);
    }
}
