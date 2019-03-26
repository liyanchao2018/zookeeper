package com.luckyli.zookeeper.dislock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.awt.geom.CubicCurve2D;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class OriginalDistributedLock implements Lock, Watcher {

    public final static String CONNECTION_ADDRESS = "192.168.229.128:2181,192.168.229.129:2181,192.168.229.131:2181";

    private ZooKeeper zk=null;
    private String ROOT_LOCK="/locks"; //定义根节点
    private String WAIT_LOCK; //等待前一个锁
    private String CURRENT_LOCK; //表示当前的锁

    private CountDownLatch countDownLatch; //


    public OriginalDistributedLock() {
        try {
            zk=new ZooKeeper(CONNECTION_ADDRESS,
                    4000,this);
            //判断根节点是否存在
            Stat stat=zk.exists(ROOT_LOCK,false);
            if(stat==null){
                zk.create(ROOT_LOCK,"0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean tryLock() {
        try {
            //创建临时有序节点
            CURRENT_LOCK = zk.create(ROOT_LOCK+"/","0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName()+"->" + CURRENT_LOCK + ",尝试竞争锁.");
            List<String> childrens = zk.getChildren(ROOT_LOCK,false); //获取根节点下的所有子节点
            SortedSet<String> sortedSet = new TreeSet<String>();//定义一个集合进行排序
            for(String chidren : childrens){
                sortedSet.add(ROOT_LOCK+"/"+chidren);
            }
            String firstNode = sortedSet.first();//获得当前所有子节点中最小的节点
            SortedSet<String > lessThenMe = sortedSet.headSet(CURRENT_LOCK);//获取sortedSet中比CURRENT_LOCK小的集合
            if(CURRENT_LOCK.equals(firstNode)){//通过当前的节点和子节点中最小的节点进行比较，如果相等，表示获得锁成功
                return true;
            }
            if(!lessThenMe.isEmpty()){
                WAIT_LOCK = lessThenMe.last();//获得比当前节点更小的最后一个节点，设置给WAIT_LOCK
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void lock() {
        if(this.tryLock()){//如果获得锁成功
            System.out.println(Thread.currentThread().getName()+"->"+CURRENT_LOCK+"->获得锁成功");
            return;
        }
        try {
            waitForLock(WAIT_LOCK);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private boolean waitForLock(String prev) throws KeeperException, InterruptedException {
        Stat stat=zk.exists(prev,true);//监听当前节点的上一个节点;
        // watch 设置为true代表通过重写process方法,当监听的node发生变化后,会调用process方法;
        if(stat!=null){
            System.out.println(Thread.currentThread().getName()+"->等待锁"+ROOT_LOCK+"/"+prev+"释放");
            countDownLatch=new CountDownLatch(1);
            countDownLatch.await();
            //TODO  watcher触发以后，还需要再次判断当前等待的节点是不是最小的
            System.out.println(Thread.currentThread().getName()+"->获得锁成功");
        }
        return true;
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }



    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName()+"->释放锁"+CURRENT_LOCK);
        try {
            zk.delete(CURRENT_LOCK,-1);//version -1 表示 无论什么版本 都删除
            CURRENT_LOCK=null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    /**
     * waitForLock 方法中, zk.exist(path,watch);watch 为true时默认监听处理方法调用此处
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        if(this.countDownLatch!=null){
            this.countDownLatch.countDown();
        }
    }
}
