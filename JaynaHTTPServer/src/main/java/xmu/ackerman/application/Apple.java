package xmu.ackerman.application;

import xmu.ackerman.spring.annotation.Controller;
import xmu.ackerman.spring.annotation.RequestMapping;

@Controller
public class Apple {

    @RequestMapping(value = "/shit", method = RequestMapping.RequestMethod.GET)
    public String sayApple(){
        System.out.println("hello world");

        return "index";
    }
}
