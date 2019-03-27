package com.luckyli.rmi;

import com.luckyli.rmi.server.HelloserviceImpl;
import com.luckyli.rmi.server.IHelloService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * 通过 com.luckyli.rmi.server/com.luckyli.rmi.client实现两个进程的调用;
 * 1.先启动RMIServerStart
 * 2.后启动RMIClient进行调用
 */
public class RMIServerStart {
    /**
     * 发布服务到对应的网络上
     * @param args
     */
    public static void main(String[] args)  {
        try {
            IHelloService helloService = new HelloserviceImpl();//new完之后,代表:已经发布了一个远程对象
            LocateRegistry.createRegistry(1099);//注册中心 注册端口
            Naming.rebind("rmi://127.0.0.1/Hello",helloService);//绑定rmi协议地址,把 helloService代理过去
            System.out.println("服务启动成功!");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}
