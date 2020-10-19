package com.zhm.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;


import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DistributedLock implements Lock, Watcher {

    private ZooKeeper zk = null;
    private String ROOT_LOCK = "/locks"; //定义根节点
    private String WAIT_LOCK; //等待前一个锁
    private String CURRENT_LOCK;//当前锁

    private CountDownLatch countDownLatch;

    public DistributedLock() {
        try {
            zk = new ZooKeeper("192.168.199.164:2181",4000,this);
            Stat stat = zk.exists(ROOT_LOCK,false);
            if (stat == null){
                zk.create(ROOT_LOCK,"0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
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
    public void lock() {
        if (this.tryLock()){
            System.out.println(Thread.currentThread().getName()+"->"
            + CURRENT_LOCK + "->" + "获得锁成功");
            return;
        }
        waitForLock(WAIT_LOCK);//没有获得的锁，继续等待获得锁
    }

    private boolean waitForLock(String prev){
        try {
            Stat stat = zk.exists(prev,true);//true 表示监听上一个节点
            if (stat!= null){
                System.out.println(Thread.currentThread().getName()+"->等待锁"+
                        ROOT_LOCK+"/"+prev+"释放");
                countDownLatch = new CountDownLatch(1);
                countDownLatch.await();
                System.out.println(Thread.currentThread().getName()+"->获得锁成功");
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        //创建临时有序节点
        try {
            CURRENT_LOCK = zk.create(ROOT_LOCK+"/","0".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName() + "->"
                    +CURRENT_LOCK + "尝试竞争锁");
            //获取子节点
            List<String> childrens = zk.getChildren(ROOT_LOCK,false);
            SortedSet <String> sortedSet = new TreeSet<>();//定义一个集合排序
            for (String children:childrens){
                sortedSet.add(ROOT_LOCK+"/"+children);
            }
            String fitstNode = sortedSet.first();
            //获得比当前锁小的元素
            SortedSet<String> lessThenMe = ((TreeSet<String>)sortedSet).headSet(CURRENT_LOCK);
            //获取子节点中最小的节点进行比较如果相等表示获得锁成功
            if (CURRENT_LOCK.equals(fitstNode)){
                return true;
            }
            //获得比当前节点更小的最后一个节点。设置给WAIT_LOCK
            if (!lessThenMe.isEmpty()){
                WAIT_LOCK = lessThenMe.last();
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName()+"释放锁"
        + CURRENT_LOCK);
        try {
            zk.delete(CURRENT_LOCK,-1);
            CURRENT_LOCK = null;
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

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (this.countDownLatch!= null){
            this.countDownLatch.countDown();
        }
    }
}
