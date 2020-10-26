package com.zhm.zookeeper.zk;

import com.zhm.zookeeper.rpc.RpcRequest;

import java.lang.reflect.Method;

public class RemoteInvocationHandler implements java.lang.reflect.InvocationHandler {

    private IServiceDiscovery serviceDiscovery;


    public RemoteInvocationHandler(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodeName(method.getName());
        rpcRequest.setParameters(args);

        String serviceAddress = serviceDiscovery.discover(rpcRequest.getClassName());

        TCPTransport tcpTransport = new TCPTransport(serviceAddress);
        return tcpTransport.send(rpcRequest);
    }
}
