package com.luckyli.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *  表示 当前的对象发布成远程访问的对象,必须要extends UnicastRemoteObject,
 *  才能保证客户端访问这个远程对象,通过socket方式将这个对象的自身拷贝返回给客户端;
 */
public class HelloserviceImpl extends UnicastRemoteObject implements IHelloService {


    public HelloserviceImpl() throws RemoteException {
    }

    @Override
    public String sayHello(String msg) throws RemoteException {
        return "Hello," + msg;
    }
}
