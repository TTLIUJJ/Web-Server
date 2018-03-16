package xmu.ackerman.handler.entityHandler.error;

import xmu.ackerman.context.Context;
import xmu.ackerman.handler.HtmlHandler;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午7:26 18-3-15
 */
public class Error404Handler extends HtmlHandler{
    private final String filename = "/error/error_404.html";

    @Override
    public void doGet(Context context){

    }

    @Override
    public String getFilename(){ return filename; }
}
