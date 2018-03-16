package xmu.ackerman.context;

import xmu.ackerman.service.RequestMessage;

import java.nio.channels.SelectionKey;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午10:09 18-3-15
 */
public abstract class Context {
    protected Request request;
    protected Response response;

    /**
    * @Description: 设置当前连接通道的上下文
    * @Date: 上午10:10 18-3-15
    */
    public abstract void setContext(RequestMessage requestMessage, SelectionKey key);

    /**
    * @Description: 获取Request
    * @Date: 上午10:11 18-3-15
    */
    public Request getRequest(){ return request; }

    /**
    * @Description: 获取Response
    * @Date: 上午10:12 18-3-15
    */
    public Response getResponse() { return response; }
}
