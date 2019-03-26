package com.luckyli.zookeeper.api.original;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper原生api调用
 */
public class ConnectionCRUD {

    public final static String CONNECTION_ADDRESS = "192.168.229.128:2181,192.168.229.129:2181,192.168.229.131:2181";

    public static void main(String[] args) {
//        getZooKeeperConnectionStatus();
//        setDataByVersion();
    }



    /**
     *
     * 查看client连接zookeeper服务器时，
     * 由于连接是异步通过一个子线程连接，
     * 建立连接需要一段时间，所以sleep前后表现不同的连接状态。
     */
    public static void getZooKeeperConnectionStatus(){
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            ZooKeeper zooKeeper = new ZooKeeper(CONNECTION_ADDRESS,4000,new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState()==Event.KeeperState.SyncConnected){ //如果收到了服务端的响应事件，连接成功
                        System.out.println("连接子线程:服务端已经建立了连接,子线程通知主线程停止阻塞等待.");
                        countDownLatch.countDown();
                    }
                }
            });
            System.out.println(zooKeeper.getState());
            Thread.sleep(3000);
            System.out.println(zooKeeper.getState());
            zooKeeper.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * update node by version
     * 通过乐观锁实现的乐观锁:版本发生变化去更新操作,是操作失败的.
     */
    public static void setDataByVersion (){
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            ZooKeeper zooKeeper = new ZooKeeper(CONNECTION_ADDRESS, 4000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState()==Event.KeeperState.SyncConnected){ //如果收到了服务端的响应事件，连接成功
                        System.out.println("连接子线程:服务端已经建立了连接,子线程通知主线程停止阻塞等待.");
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            System.out.println("zookeeper连接状态:"+zooKeeper.getState());

            //添加节点
            zooKeeper.create("/liyanchao","liyanchao".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            //watcher 参数设置null 代表此节点不设置监听事件;将stat传入,getData后,stat可以获取到 当前client获取的节点的版本信息;

            //得到当前节点的值
            Stat stat = new Stat();
            byte[] bytes = zooKeeper.getData("/liyanchao",null,stat);
            System.out.println("zooKeeper.getData(\"/liyanchao\",null,stat);的结果为:");
            System.out.println(new String(bytes));

            //修改节点值
            zooKeeper.setData("/liyanchao","123456".getBytes(),stat.getVersion());
            //set结果 从client端 去看一下 更新的结果

            byte[] bytes1 = zooKeeper.getData("/liyanchao",null,stat);
            System.out.println("更新后的值为:"+new String(bytes1));
            //这里 delete/setData 的入参都要版本号,也体现了乐观锁实现多客户端并发访问同一资源的体现;
            zooKeeper.delete("/liyanchao",stat.getVersion());

            System.in.read();
        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }


}
