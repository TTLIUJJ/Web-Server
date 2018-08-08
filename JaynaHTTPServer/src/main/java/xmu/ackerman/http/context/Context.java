package xmu.ackerman.http.context;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午10:09 18-3-15
 */
public abstract class Context {
    protected Request request;
    protected Response response;
    protected Selector selector;
    protected SelectionKey selectionKey;
    /**
    * @Description: 设置当前连接通道的上下文
    * @Date: 上午10:10 18-3-15
    */
    public abstract void setContext(Selector selector, SelectionKey key);

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

    /**
    * @Description: 获取注册器
    * @Date: 下午7:30 18-3-28
    */
    public Selector getSelector() {
        return selector;
    }

    /**
    * @Description: 获取上下文工作的通道key
    * @Date: 下午7:19 18-3-28
    */
    public SelectionKey getSelectionKey() {
        return selectionKey;
    }


}
