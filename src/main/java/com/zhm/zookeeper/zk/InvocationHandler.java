package com.zhm.zookeeper.zk;

import java.lang.reflect.Method;

public interface InvocationHandler {

    Object invoke(Object proxy, Method method, Object[] args);
}
