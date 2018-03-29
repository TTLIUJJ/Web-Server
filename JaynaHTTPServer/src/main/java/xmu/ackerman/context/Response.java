package xmu.ackerman.context;

import xmu.ackerman.handler.HtmlHandler;

import java.nio.channels.SelectionKey;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午10:08 18-3-15
 */
public interface Response {

    public static final String SERVER_NAME = "Jayna";

    public int getStatusCode();
    public void setStatusCode(int statusCode);

    public String getContentType();
    public void setContentType(String contentType);

    public String getStatusMsg();
    public void setStatusMsg(String statusMsg);

    public String getFilePath();
    public void setFilePath(String filePath);

    public HtmlHandler getHtmlHandler();
    public void setHandler(HtmlHandler handler);

    public int getContentLength();
    public void setContentLength(int contentLength);

    public boolean isException();
    public void setException(boolean exception);

}
