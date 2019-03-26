package com.luckyli.zookeeper.api.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class CuratorCRUD {

    public final static String CONNECTION_ADDRESS = "192.168.229.128:2181,192.168.229.129:2181,192.168.229.131:2181";

    public static void main(String[] args) throws Exception {

        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNECTION_ADDRESS)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .namespace("curator").build();
        curatorFramework.start();

        //结果: /curator/liyanchao/node1
        //原生api中，必须是逐层创建，也就是父节点必须存在，子节点才能创建
        curatorFramework.create().creatingParentsIfNeeded().
                withMode(CreateMode.PERSISTENT).
                forPath("/liyanchao/node1","1".getBytes());
        //删除
        //        curatorFramework.delete().deletingChildrenIfNeeded().forPath("/liyanchao/node1");

        Stat stat=new Stat();
        curatorFramework.getData().storingStatIn(stat).forPath("/liyanchao/node1");

        curatorFramework.setData().
                withVersion(stat.getVersion()).forPath("/liyanchao/node1","xx".getBytes());

        curatorFramework.close();




    }


}
