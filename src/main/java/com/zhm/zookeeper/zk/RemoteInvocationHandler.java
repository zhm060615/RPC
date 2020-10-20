package com.zhm.zookeeper.zk;

import com.zhm.zookeeper.rpc.RpcRequest;
import sun.rmi.transport.tcp.TCPTransport;

import java.lang.reflect.Method;

public class RemoteInvocationHandler implements InvocationHandler {

    private IServiceDiscovery serviceDiscovery;

    private String host;
    private int port;

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

        TCPTransport tcpTransport = new TCPTransport(this.host,this.port);
        return tcpTransport.send(rpcRequest);
    }
}
