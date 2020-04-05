package com.lagou.subscriber;

import com.lagou.util.ZkBase;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Subscriber implements Watcher {

    CuratorFramework client;
    static String path = "/rpc_server";

    // 创建会话
    public Subscriber() {
        client = ZkBase.getClient();

        //监视monitor节点,获取下面的所有子节点的变化情况
        try {
            List<String> serverList = client.getChildren().usingWatcher(this).forPath(path);
            refreshServerAddress(serverList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Object lock = new Object();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged){

            List<String> children;
            try {

                children = client.getChildren().forPath(path);
                refreshServerAddress(children);

            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static Map<String,ServerProxyEntity> serverMap = new ConcurrentHashMap();

    void refreshServerAddress (List<String> serverList) {
        Map<String,ServerProxyEntity> refreshedServerMap = new ConcurrentHashMap();
        for (String server : serverList) {
            String[] ipPort = server.split(":");
            ServerProxyEntity oldServer = serverMap.get(server);
            if (oldServer == null) { // 新增服务
                ServerProxyEntity newServer = new ServerProxyEntity();
                newServer.setIpAddress(ipPort[0]);
                newServer.setPort(Integer.parseInt(ipPort[1]));
                newServer.setIpPortHash(server.hashCode());
                refreshedServerMap.put(server,newServer);
            } else {
                refreshedServerMap.put(server,oldServer);
            }

        }
        serverMap = refreshedServerMap;
    }

    public static void main(String[] args) {
//        path = "/base/rpc_server/127.0.0.1";
        path = "/rpc_server";

        Subscriber subscriber = new Subscriber();
        System.out.println(subscriber);

        Object lock = new Object();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
