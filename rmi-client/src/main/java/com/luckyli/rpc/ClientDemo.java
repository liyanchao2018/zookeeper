package com.luckyli.rpc;

import com.luckyli.rpc.zk.IServiceDiscovery;
import com.luckyli.rpc.zk.ServiceDiscoveryImpl;
import com.luckyli.rpc.zk.ZkConfig;

public class ClientDemo {
    public static void main(String[] args) throws InterruptedException {
        IServiceDiscovery serviceDiscovery = new ServiceDiscoveryImpl(ZkConfig.CONNNECTION_STR);

        RpcClientProxy rpcClientProxy = new RpcClientProxy(serviceDiscovery);

        for(int i=0;i<10;i++) {
            IRPCHelloService irpcHelloService = rpcClientProxy.clientProxy(IRPCHelloService.class, null);
            System.out.println(irpcHelloService.sayHello("李岩超"+i));
            Thread.sleep(1000);
        }
    }
}
