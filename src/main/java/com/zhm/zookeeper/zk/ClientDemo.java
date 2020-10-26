package com.zhm.zookeeper.zk;

import com.zhm.zookeeper.rpc.Zkconfig;
import com.zhm.zookeeper.zk.RpcClien.RpcClienProxy;
import com.zhm.zookeeper.rpc.Hello;


public class ClientDemo {



    public static void main(String[] args) {

        IServiceDiscovery serviceDiscovery = new ServiceDiscoveryImpl(Zkconfig.CONNECTION_STR);

        RpcClienProxy rpcClienProxy = new RpcClienProxy(serviceDiscovery);

        Hello hello = rpcClienProxy.clienProxy(Hello.class);

        System.out.println(hello.sayHello("mic"));
    }



}
