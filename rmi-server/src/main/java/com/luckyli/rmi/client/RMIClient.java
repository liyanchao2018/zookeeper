package com.luckyli.rmi.client;

import com.luckyli.rmi.server.IHelloService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIClient {
    public static void main(String[] args) {
        try {
            IHelloService helloService = (IHelloService)Naming.lookup("rmi://127.0.0.1/Hello");
            System.out.println(helloService.sayHello("liyanchao"));

        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
