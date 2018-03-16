package xmu.ackerman.handler;


import xmu.ackerman.utils.ClassUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Ackerman
 * @Description: 根据文件名, 找到对应的实现类
 * @Date: Created in 下午7:07 18-3-15
 */
public class HandlerAdapter {

    //文件名----实现类
    // </error/error_404.html, xmu.ackerman.handler.entityHandler.error.Error404Handler>
    private Map<String, HtmlHandler> handlers = new HashMap<String, HtmlHandler>();

    private volatile static HandlerAdapter handlerAdapter = null;

    private HandlerAdapter(){
        if(handlerAdapter == null){
            synchronized (HandlerAdapter.class){
                if(handlerAdapter == null){
                    try {
                        Set<Class<?>> classSet = ClassUtil.getAllAssignClasses(HtmlHandler.class);
                        for(Class<?> c : classSet){
                            HtmlHandler htmlHandler = (HtmlHandler)c.newInstance();
                            String filename = htmlHandler.getFilename();
                            handlers.put(filename, htmlHandler);
                        }
                    }catch (Exception e){
                        //TODO
                    }
                }
            }
        }
    }


    public static HandlerAdapter getHandlerAdapter(){
        if(handlerAdapter == null){
            handlerAdapter = new HandlerAdapter();
        }

        return handlerAdapter;
    }

    public HtmlHandler getEntityHandler(String uri){
        return handlers.get(uri);
    }


//    public static void main(String []args){
//        HandlerAdapter handlerAdapter = HandlerAdapter.getHandlerAdapter();
//    }
}
