package com.luckyli.rmi.rpc.zk;

/**
 *
 * zk注册中心
 *
 */
public interface IRegisterCenter {

    /**
     * 注册服务名称和服务地址
     * @param serviceName
     * @param serviceAddress
     */
    void register(String serviceName, String serviceAddress);
}
