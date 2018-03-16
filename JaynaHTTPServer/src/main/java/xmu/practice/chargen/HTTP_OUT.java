package xmu.practice.chargen;

import java.nio.channels.SelectionKey;

public class HTTP_OUT {

    private SelectionKey key;
    private boolean keepAlive;
    private boolean modified;
    private JaynaHTTPCode code;

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

    public JaynaHTTPCode getCode() {
        return code;
    }

    public void setCode(JaynaHTTPCode code) {
        this.code = code;
    }
}
