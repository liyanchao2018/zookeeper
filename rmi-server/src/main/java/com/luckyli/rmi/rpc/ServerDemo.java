package com.luckyli.rmi.rpc;

import com.luckyli.rmi.rpc.zk.IRegisterCenter;
import com.luckyli.rmi.rpc.zk.RegisterCenterImpl;

import java.io.IOException;

/**
 * 启动rpc服务器 将本地服务注册到注册中心中,并将服务发布到
 */
public class ServerDemo {
    public static void main(String[] args) throws IOException {
        IRPCHelloService irpcHelloService = new RPCＨelloServiceImpl();
        IRegisterCenter registerCenter = new RegisterCenterImpl();

        RpcServer rpcServer=new RpcServer(registerCenter,"127.0.0.1:8080");
        rpcServer.bind(irpcHelloService);
        rpcServer.publisher();
        System.in.read();

    }
}
