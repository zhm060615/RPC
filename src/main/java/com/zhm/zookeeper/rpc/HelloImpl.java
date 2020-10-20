package com.zhm.zookeeper.rpc;

import com.zhm.zookeeper.rpc.anno.RpcAnnotation;

@RpcAnnotation(Hello.class)
public class HelloImpl implements Hello {

    @Override
    public String sayHello(String msg) {
        return "Hello," + msg;
    }
}
