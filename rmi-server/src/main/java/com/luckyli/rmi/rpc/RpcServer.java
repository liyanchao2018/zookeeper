package com.luckyli.rmi.rpc;

import com.luckyli.rmi.rpc.anno.RpcAnnotation;
import com.luckyli.rmi.rpc.zk.IRegisterCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private IRegisterCenter registerCenter; //注册中心
    private String serviceAddress; //服务发布地址

    // 存放服务名称和服务对象之间的关系
    Map<String,Object> handlerMap=new HashMap<>();

    public RpcServer(IRegisterCenter registerCenter, String serviceAddress) {
        this.registerCenter = registerCenter;
        this.serviceAddress = serviceAddress;
    }

    /**
     * 绑定服务名称和服务对象
     * @param services
     */
    public void bind(Object... services){
        for(Object service:services){
            RpcAnnotation annotation = service.getClass().getAnnotation(RpcAnnotation.class);
            String serviceName = annotation.value().getName();//serviceName: com.luckyli.rmi.rpc.IRPCHelloService
            String version = annotation.version();
            if(version != null && !version.equals("")){
                serviceName = serviceName+"-"+version;
            }
            //handlerMap.put( com.luckyli.rmi.rpc.IRPCHelloService ,serviceImpl)
            handlerMap.put(serviceName,service);//绑定服务接口名称对应的服务
        }
    }

    /**
     * 在注册中心注册znode节点;然后轮询客户端发来的socket请求,通过线程池处理client请求;
     *
     */
    public void publisher(){
        ServerSocket serverSocket = null;

        try {
            String[] addrs = serviceAddress.split(":");
            serverSocket=new ServerSocket(Integer.parseInt(addrs[1]));  //启动一个服务监听

            for(String interfaceName : handlerMap.keySet()){
                registerCenter.register(interfaceName,serviceAddress);// interfaceName:com.luckyli.rmi.rpc.IRPCHelloService ; serviceAddress --> 127.0.0.1:8080
                System.out.println("注册服务成功："+interfaceName+"->"+serviceAddress);
            }

            while(true){
                Socket socket = serverSocket.accept();
                executorService.execute(new ProcessorHandler(socket,handlerMap));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
