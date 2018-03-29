package xmu.ackerman.context;

import xmu.ackerman.handler.HtmlHandler;

import java.nio.channels.SelectionKey;

/**
 * @Author: Ackerman
 * @Description:
 * @Date: Created in 上午10:15 18-3-15
 */
public class HttpResponse implements Response {
    private int StatusCode;

    private String contentType;

    private String StatusMsg;

    private String filePath;

    private HtmlHandler htmlHandler;

    private int contentLength;

    //用来识别 返回回复头的类型
    //默认为异常... 有点奇怪吧 目前这样组织代码比较方便
    private boolean exception = true;

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStatusMsg() {
        return StatusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        StatusMsg = statusMsg;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public HtmlHandler getHtmlHandler() {
        return this.htmlHandler;
    }

    public void setHandler(HtmlHandler handler) {
        this.htmlHandler = handler;
    }

    public boolean isException() {
        return exception;
    }

    public void setException(boolean exception) {
        this.exception = exception;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
}
