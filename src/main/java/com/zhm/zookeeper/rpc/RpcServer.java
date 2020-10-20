package com.zhm.zookeeper.rpc;

import com.zhm.zookeeper.rpc.anno.RpcAnnotation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private IRegisterCenter registerCenter;
    private String serviceAddress;

    Map<String,Object> handlerMap = new HashMap<>();

    public RpcServer(IRegisterCenter registerCenter, String serviceAddress) {
        this.registerCenter = registerCenter;
        this.serviceAddress = serviceAddress;
    }

    //绑定服务名称和服务对象
    public void bind(Object... services){
        for (Object service: services){
            RpcAnnotation rpcAnnotation = service.getClass().getAnnotation(RpcAnnotation.class);
            String serviceName = rpcAnnotation.value().getName();
            handlerMap.put(serviceName,service);//绑定服务接口名称对应的服务
        }
    }

    public void publisher(){
        ServerSocket serverSocket = null;
        String[] addrs = serviceAddress.split(":");
        try {
            serverSocket = new ServerSocket(Integer.parseInt(addrs[1]));
            for (String interfaceName : handlerMap.keySet()){
                registerCenter.register(interfaceName,serviceAddress);
                System.out.println("注册服务成功：" + interfaceName + "->" + serviceAddress);
            }
            while (true){
                Socket socket = serverSocket.accept();
                executorService.execute(new ProcessorHandler(socket,handlerMap));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
