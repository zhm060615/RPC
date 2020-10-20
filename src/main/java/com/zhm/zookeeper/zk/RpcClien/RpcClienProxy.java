package com.zhm.zookeeper.zk.RpcClien;

import com.zhm.zookeeper.zk.IServiceDiscovery;

import java.lang.reflect.Proxy;
import java.rmi.server.RemoteObjectInvocationHandler;

public class RpcClienProxy {
    private IServiceDiscovery serviceDiscovery;

    public RpcClienProxy(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T clienProxy(final  Class<T> interfacCls){
        return (T) Proxy.newProxyInstance(interfacCls.getClassLoader(),new Class[]{interfacCls},
                new RemoteObjectInvocationHandler(serviceDiscovery));
    }
}
