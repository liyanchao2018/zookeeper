package com.luckyli.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 如过要实现在不同进程(不同jvm)实现方法的调用,interface需要继承 java.rmi.Remote
 */
public interface IHelloService extends Remote {
    String sayHello(String msg)throws RemoteException;
}
