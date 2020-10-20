package com.zhm.zookeeper.rpc;

import java.io.IOException;

public class ServerDemo {

    public static void main(String[] args) throws IOException {
        Hello hello = new HelloImpl();
        IRegisterCenter registerCenter = new RegisterCenterImpl();
        RpcServer rpcServer = new RpcServer(registerCenter,"127.0.0.1:8080");
        rpcServer.bind(hello);
        rpcServer.publisher();
        System.in.read();
    }
}
