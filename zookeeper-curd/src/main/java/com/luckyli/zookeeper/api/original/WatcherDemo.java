package com.luckyli.zookeeper.api.original;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class WatcherDemo {

    public final static String CONNECTION_ADDRESS = "192.168.229.128:2181,192.168.229.129:2181,192.168.229.131:2181";

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final ZooKeeper zooKeeper = new ZooKeeper(CONNECTION_ADDRESS,4000,new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState()==Event.KeeperState.SyncConnected){ //如果收到了服务端的响应事件，连接成功
                        System.out.println("连接子线程:服务端已经建立了连接,子线程通知主线程停止阻塞等待.");
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            System.out.println(zooKeeper.getState());

            zooKeeper.create("/zk-persis-liyanchao","1".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            //绑定watch事件三种形式:exists/getdata/getchildren
            //通过exists绑定事件
            Stat stat=zooKeeper.exists("/zk-persis-liyanchao", new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    System.out.println(event.getType()+"->"+event.getPath());
                    try {
                        //再一次去绑定事件
                        zooKeeper.exists(event.getPath(),true);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            //通过修改的事务类型操作来触发监听事件
            stat=zooKeeper.setData("/zk-persis-liyanchao","2".getBytes(),stat.getVersion());
            Thread.sleep(1000);

            zooKeeper.delete("/zk-persis-liyanchao",stat.getVersion());

            System.in.read();


    }


}
