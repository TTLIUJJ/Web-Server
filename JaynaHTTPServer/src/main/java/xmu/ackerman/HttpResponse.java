package xmu.ackerman;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class HttpResponse {
    private SelectionKey key;
    private boolean keepAlive;
    private boolean modified;
    private int status;
    private String fileName;
    private String responseFile;
    private int fileSize;
    private ByteBuffer headerBuffer;
    private ByteBuffer resourceBuffer;

    public HttpResponse(SelectionKey key){
        this.key = key;
        keepAlive = false;
        modified = false;
        status = 200;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getResponseFile() {
        return responseFile;
    }

    public void setResponseFile(String responseFile) {
        this.responseFile = responseFile;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public ByteBuffer getHeaderBuffer() {
        return headerBuffer;
    }

    public void setHeaderBuffer(ByteBuffer headerBuffer) {
        this.headerBuffer = headerBuffer;
    }

    public ByteBuffer getResourceBuffer() {
        return resourceBuffer;
    }

    public void setResourceBuffer(ByteBuffer resourceBuffer) {
        this.resourceBuffer = resourceBuffer;
    }
}
