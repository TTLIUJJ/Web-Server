package xmu.ackerman.controller;

import xmu.ackerman.context.Context;
import xmu.ackerman.context.HttpContext;
import xmu.ackerman.context.Response;
import xmu.ackerman.handler.HtmlHandler;
import xmu.ackerman.service.RequestMessage;
import xmu.ackerman.service.ResponseService;

import java.nio.channels.SelectionKey;

/**
 * @Author: Ackerman
 * @Description: 一个接受请求, 并处理返回结果的工作线程
 * @Date: Created in 下午5:12 18-3-15
 */
public class HTTPThread implements Runnable {

    private Context context;

    private SelectionKey key;

    private RequestMessage requestMessage;

    public HTTPThread(RequestMessage requestMessage, SelectionKey key){
        context = new HttpContext();
        this.requestMessage = requestMessage;
        this.key = key;
    }

    /**
    * @Description: 线程并发处理请求
    * @Date: 下午3:32 18-3-16
    */
    public void run(){

        context.setContext(requestMessage, key);

        ResponseService.initResponse(context);

        HtmlHandler htmlHandler = context.getResponse().getHtmlHandler();

        htmlHandler.init(context);

        ResponseService.write(context);
    }
}
