package xmu.ackerman.handler.entityHandler;

import xmu.ackerman.context.Context;
import xmu.ackerman.handler.HtmlHandler;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 下午7:26 18-3-15
 */
public class IndexHandler extends HtmlHandler {
    private final String fileName = "/index.html";

    @Override
    public String getFilename(){ return fileName; }

    @Override
    public void doGet(Context context){

    }
}
