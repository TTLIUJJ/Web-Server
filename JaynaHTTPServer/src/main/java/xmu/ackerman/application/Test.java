package xmu.ackerman.application;


import xmu.ackerman.JaynaHttpController;
import xmu.ackerman.spring.annotation.Autowired;
import xmu.ackerman.spring.annotation.Service;

@Service
public class Test {

    @Autowired
    private Apple apple;

    private JaynaHttpController jaynaHttpController;


    public static void main(String []args){
        try {

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
