package xmu.ackerman.test;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午2:36 18-3-18
 */
public class TestVariable {



    public static void main(String []args){
        try {
            C c = new C(5);
            ArrayList<C> list = new ArrayList<C>();
            list.add(c);

            Runnable r = new TestThread(list);
            Thread t = new Thread(r);
            t.start();

            TimeUnit.SECONDS.sleep(5);

            System.out.println("本地变量：" + c.getA());
            System.out.println("列表变量：" + list.get(0).getA());

        }catch (Exception e){

        }

    }
}
