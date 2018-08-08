package xmu.ackerman.application;

import xmu.ackerman.spring.annotation.After;
import xmu.ackerman.spring.annotation.Aspect;
import xmu.ackerman.spring.annotation.Before;

@Aspect
public class Log {

    @Before(method = "xmu.ackerman.application.Apple.sayApple")
    public void beforeStart(){
        System.out.println("------- log before -----------");
    }

    @After(method = "xmu.ackerman.application.Apple.sayApple")
    public void afterEnd(){
        System.out.println(" ------- log after --------");
    }
}
