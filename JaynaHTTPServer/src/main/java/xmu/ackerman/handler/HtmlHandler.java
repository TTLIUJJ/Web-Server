package xmu.ackerman.handler;

import xmu.ackerman.context.Context;
import xmu.ackerman.context.Request;
import xmu.ackerman.service.ResponseService;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午5:25 18-3-15
 */
public class HtmlHandler implements Handler {
    protected Context context;

    public void init(Context context){
        this.context = context;
        this.service(context);
    }

    /**
    * @Description: 每个html请求对应的handler, 在返回之前处理数据
    * @Date: 下午3:34 18-3-16
    */
    public void service(Context context){
        String method = context.getRequest().getMethod();
        if(method.equals(Request.GET)){
            this.doGet(context);
        }
        else if(method.equals(Request.POST)){
            this.doPost(context);
        }
    }

    public void doGet(Context context){

    }

    public void doPost(Context context){

    }

    public void destroy(Context context){
        context = null;
    }


    public String getFilename(){ return ""; }
}
