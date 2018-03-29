package xmu.ackerman.context;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午10:14 18-3-15
 */
public class HttpContext extends Context{

    @Override
    public void setContext(Selector selector, SelectionKey selectionKey){
        super.request = new HttpRequest();
        super.response = new HttpResponse();
        super.selector = selector;
        super.selectionKey = selectionKey;
    }

}
