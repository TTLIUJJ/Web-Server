package xmu.ackerman.context;

import xmu.ackerman.service.RequestMessage;
import xmu.ackerman.service.ResponseService;

import java.nio.channels.SelectionKey;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午10:14 18-3-15
 */
public class HttpContext extends Context{
    private Request request;
    private Response response;


    @Override
    public void setContext(RequestMessage requestMessage, SelectionKey key){
        request = new HttpRequest(requestMessage);
        response = new HttpResponse(key);

        super.request = request;
        super.response = response;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

}
