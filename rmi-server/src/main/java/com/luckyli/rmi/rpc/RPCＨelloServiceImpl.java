package com.luckyli.rmi.rpc;

import com.luckyli.rmi.rpc.anno.RpcAnnotation;

@RpcAnnotation(IRPCHelloService.class)
public class RPCＨelloServiceImpl implements IRPCHelloService {
    @Override
    public String sayHello(String msg) {
        return "RPC调用:Hello,"+msg;
    }
}
