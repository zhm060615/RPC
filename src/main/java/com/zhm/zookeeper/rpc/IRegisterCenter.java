package com.zhm.zookeeper.rpc;

public interface IRegisterCenter {
    //注册服务名称和服务地址
    void register(String serviceName, String serviceAddress);
}
