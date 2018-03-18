package xmu.ackerman.test;

import xmu.ackerman.thread.TimeMonitorThread;

import java.util.ArrayList;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午2:34 18-3-18
 */
public class TestThread implements Runnable{

    private ArrayList<C> list;

    public TestThread(ArrayList<C> list){
        this.list = list;
    }

    public void run(){
        C c = list.get(0);
        c.setA(100);
    }


}