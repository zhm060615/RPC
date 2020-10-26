package com.zhm.zookeeper.zk;

import com.zhm.zookeeper.rpc.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPTransport {

    private String serviceAddress;

    public TCPTransport(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    private Socket newSocket(){
        System.out.println("创建一个连接");
        Socket socket;
        String[] split = serviceAddress.split(":");
        try {
            socket = new Socket(split[0],Integer.parseInt(split[1]));
            return socket;
        } catch (IOException e) {
           throw  new RuntimeException("连接失败");
        }
    }

    public Object send(RpcRequest request){
        Socket socket = null;

        socket = newSocket();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(request);
            outputStream.flush();
            ObjectInputStream  inputStream = new ObjectInputStream(socket.getInputStream());
            Object result = inputStream.readObject();
            inputStream.close();
            outputStream.close();
            return result;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("失败");
        }
    }

}
