package xmu.ackerman.application;

import xmu.ackerman.spring.annotation.After;
import xmu.ackerman.spring.annotation.Aspect;
import xmu.ackerman.spring.annotation.Before;

import java.util.Date;

@Aspect
public class Time {
    @Before(method = "xmu.ackerman.application.Apple.sayApple")
    public void before(){
        System.out.println("timeBefore: " + new Date());
    }

    @After(method = "xmu.ackerman.application.Apple.sayApple")
    public void after(){
        System.out.println("timeAfter: " + new Date());
    }

}
