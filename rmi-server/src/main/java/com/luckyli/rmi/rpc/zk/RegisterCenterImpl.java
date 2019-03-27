package com.luckyli.rmi.rpc.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 *
 * zk注册中心实现
 *
 */
public class RegisterCenterImpl implements IRegisterCenter{

    private CuratorFramework curatorFramework;

    {
        curatorFramework=CuratorFrameworkFactory.builder().
                connectString(ZkConfig.CONNNECTION_STR).
                sessionTimeoutMs(4000).
                retryPolicy(new ExponentialBackoffRetry(1000,
                        10)).build();
        curatorFramework.start();
    }

    /**
     * 将server端的服务注册到zookeeper节点上,同时将节点信息存放到缓存map中.
     * 持久化节点servicePath: /registrys/serviceName(product-service)
     * addressPath: /registrys/product-service/serviceAddress(192.168.229.128)
     *
     * @param serviceName
     * @param serviceAddress
     */
    @Override
    public void register(String serviceName, String serviceAddress) {
        //注册相应的服务
        String servicePath = ZkConfig.ZK_REGISTER_PATH + "/" + serviceName;
        // servicePath : /registrys/com.luckyli.rmi.rpc.IRPCHelloService
        try {
            //判断 /registrys/product-service是否存在，不存在则创建
            if(curatorFramework.checkExists().forPath(servicePath)==null){
                curatorFramework.create().creatingParentsIfNeeded().
                        withMode(CreateMode.PERSISTENT).forPath(servicePath,"0".getBytes());
            }
            String addressPath = servicePath + "/" + serviceAddress;
            //创建临时节点:ephemeral  node: /registry/product-service/192.168.229.128 value:0
            String rsNode = curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(addressPath,"0".getBytes());
            System.out.println("服务注册成功：" + rsNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
