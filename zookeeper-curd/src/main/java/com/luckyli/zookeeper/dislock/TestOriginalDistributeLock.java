package com.luckyli.zookeeper.dislock;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 通过调用zookeeper原生api,多线程获取zookeeper分布式锁.
 */
public class TestOriginalDistributeLock {

    public static void main(String[] args) throws InterruptedException, IOException {
        CountDownLatch countDownLatch=new CountDownLatch(10);
        for(int i=0;i<10;i++){
            new Thread(()->{
                try {
                    countDownLatch.await();
                    OriginalDistributedLock originalDistributedLock =new OriginalDistributedLock();
                    originalDistributedLock.lock(); //获得锁
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"Thread-"+i).start();
            countDownLatch.countDown();
        }
        System.in.read();

        /**
         * 运行结果:
         * Thread-0->/locks/0000000001,尝试竞争锁.
         * Thread-5->/locks/0000000002,尝试竞争锁.
         * Thread-6->/locks/0000000000,尝试竞争锁.
         * Thread-8->/locks/0000000006,尝试竞争锁.
         * Thread-1->/locks/0000000003,尝试竞争锁.
         * Thread-4->/locks/0000000004,尝试竞争锁.
         * Thread-2->/locks/0000000005,尝试竞争锁.
         * Thread-7->/locks/0000000009,尝试竞争锁.
         * Thread-9->/locks/0000000008,尝试竞争锁.
         * Thread-3->/locks/0000000007,尝试竞争锁.
         * Thread-6->/locks/0000000000->获得锁成功
         * Thread-7->等待锁/locks//locks/0000000008释放
         * Thread-2->等待锁/locks//locks/0000000004释放
         * Thread-0->等待锁/locks//locks/0000000000释放
         * Thread-5->等待锁/locks//locks/0000000001释放
         * Thread-1->等待锁/locks//locks/0000000002释放
         * Thread-4->等待锁/locks//locks/0000000003释放
         * Thread-8->等待锁/locks//locks/0000000005释放
         * Thread-9->等待锁/locks//locks/0000000007释放
         * Thread-3->等待锁/locks//locks/0000000006释放
         *
         * delete /locks/0000000000; delete /locks/0000000001;...delete /locks/0000000009;
         * 手动去client一次删除最小节点 可见下边的运行结果;
         * Thread-0->获得锁成功
         * Thread-5->获得锁成功
         * Thread-1->获得锁成功
         * Thread-4->获得锁成功
         * Thread-2->获得锁成功
         * Thread-8->获得锁成功
         * Thread-3->获得锁成功
         * Thread-9->获得锁成功
         * Thread-7->获得锁成功
         */


    }

}
