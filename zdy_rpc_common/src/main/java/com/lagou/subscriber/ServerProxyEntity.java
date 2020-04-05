package com.lagou.subscriber;

public class ServerProxyEntity {

    private String serverName;

    private String ipAddress;

    private Integer port;

    private Long lastInvokeMills;
    // 为了快速判断服务信息是否有变化
    private Integer ipPortHash;

    private Long lastInvokeCostMills;

    public Long getLastInvokeCostMills() {
        return lastInvokeCostMills;
    }

    public void setLastInvokeCostMills(Long lastInvokeCostMills) {
        this.lastInvokeCostMills = lastInvokeCostMills;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Long getLastInvokeMills() {
        return lastInvokeMills;
    }

    public void setLastInvokeMills(Long lastInvokeMills) {
        this.lastInvokeMills = lastInvokeMills;
    }

    public Integer getIpPortHash() {
        return ipPortHash;
    }

    public void setIpPortHash(Integer ipPortHash) {
        this.ipPortHash = ipPortHash;
    }
}
