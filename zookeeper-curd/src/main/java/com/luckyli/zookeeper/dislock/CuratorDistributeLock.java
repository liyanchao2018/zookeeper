package com.luckyli.zookeeper.dislock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 通过curator框架实现分布式锁
 */
public class CuratorDistributeLock implements Watcher {

    public final static String CONNECTION_ADDRESS = "192.168.229.128:2181,192.168.229.129:2181,192.168.229.131:2181";

    private ZooKeeper zk=null;
    private static String ROOT_LOCK="/locks"; //定义根节点
    private CountDownLatch countDownLatch; //


    public static void main(String[] args) throws Exception {

        //初始化curator
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString(CONNECTION_ADDRESS)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .namespace("curator").build();
        //启动curator
        curatorFramework.start();
        //保证每次执行 node节点都是空的
        curatorFramework.delete().deletingChildrenIfNeeded().forPath(ROOT_LOCK);
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(ROOT_LOCK,"liyanchao".getBytes());

        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //curator提供的一种锁:进程互斥锁
                    InterProcessMutex lock = new InterProcessMutex(curatorFramework,ROOT_LOCK);
                    try {
                        if (lock.acquire(20, TimeUnit.SECONDS)) {//阻塞等待获取锁
                            System.out.println(Thread.currentThread().getName() + "-> : 获得了分布式锁");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            System.out.println(Thread.currentThread().getName() + "-> : 释放了分布式锁");
                            lock.release();//释放锁
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
        System.in.read();

    }


    @Override
    public void process(WatchedEvent watchedEvent) {
        if(this.countDownLatch!=null){
            this.countDownLatch.countDown();
        }
    }

    /**
     * 执行结果:
     * Thread-5-> : 获得了分布式锁
     * Thread-5-> : 释放了分布式锁
     * Thread-20-> : 获得了分布式锁
     * Thread-20-> : 释放了分布式锁
     * Thread-2-> : 获得了分布式锁
     * Thread-2-> : 释放了分布式锁
     * Thread-11-> : 获得了分布式锁
     * Thread-11-> : 释放了分布式锁
     * Thread-17-> : 获得了分布式锁
     * Thread-17-> : 释放了分布式锁
     * Thread-18-> : 获得了分布式锁
     * Thread-18-> : 释放了分布式锁
     * Thread-9-> : 获得了分布式锁
     * Thread-9-> : 释放了分布式锁
     * Thread-6-> : 获得了分布式锁
     * Thread-6-> : 释放了分布式锁
     * Thread-8-> : 获得了分布式锁
     * Thread-8-> : 释放了分布式锁
     * Thread-7-> : 获得了分布式锁
     * Thread-7-> : 释放了分布式锁
     * Thread-14-> : 获得了分布式锁
     * Thread-14-> : 释放了分布式锁
     * Thread-13-> : 获得了分布式锁
     * Thread-13-> : 释放了分布式锁
     * Thread-4-> : 获得了分布式锁
     * Thread-4-> : 释放了分布式锁
     * Thread-15-> : 获得了分布式锁
     * Thread-15-> : 释放了分布式锁
     * Thread-12-> : 获得了分布式锁
     * Thread-12-> : 释放了分布式锁
     * Thread-1-> : 获得了分布式锁
     * Thread-1-> : 释放了分布式锁
     * Thread-3-> : 获得了分布式锁
     * Thread-3-> : 释放了分布式锁
     * Thread-10-> : 获得了分布式锁
     * Thread-10-> : 释放了分布式锁
     * Thread-16-> : 获得了分布式锁
     * Thread-16-> : 释放了分布式锁
     * Thread-19-> : 获得了分布式锁
     * Thread-19-> : 释放了分布式锁
     */

}
