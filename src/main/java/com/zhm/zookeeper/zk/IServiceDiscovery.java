package com.zhm.zookeeper.zk;

public interface IServiceDiscovery {

    //根据请求的服务地址获取调用地址
    String discover(String serviceName);
}
