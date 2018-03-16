package xmu.ackerman.zparse_http;

import java.nio.ByteBuffer;

public class HttpHeaderKV {

    private int headerKeyBeg;
    private int headerKeyEnd;
    private int headerValueBeg;
    private int headerValueEnd;
    private ByteBuffer buffer;

    public HttpHeaderKV(ByteBuffer buffer,
                        int headerKeyBeg,
                        int headerKeyEnd,
                        int headerValueBeg,
                        int headerValueEnd){

        this.buffer = buffer;
        this.headerKeyBeg = headerKeyBeg;
        this.headerKeyEnd = headerKeyEnd;
        this.headerValueBeg = headerValueBeg;
        this.headerValueEnd = headerValueEnd;
    }

    public int getHeaderKeyBeg() {
        return headerKeyBeg;
    }

    public void setHeaderKeyBeg(int headerKeyBeg) {
        this.headerKeyBeg = headerKeyBeg;
    }

    public int getHeaderKeyEnd() {
        return headerKeyEnd;
    }

    public void setHeaderKeyEnd(int headerKeyEnd) {
        this.headerKeyEnd = headerKeyEnd;
    }

    public int getHeaderValueBeg() {
        return headerValueBeg;
    }

    public void setHeaderValueBeg(int headerValueBeg) {
        this.headerValueBeg = headerValueBeg;
    }

    public int getHeaderValueEnd() {
        return headerValueEnd;
    }

    public void setHeaderValueEnd(int headerValueEnd) {
        this.headerValueEnd = headerValueEnd;
    }

    public String getHeaderKey(){
        try{
            StringBuilder sb = new StringBuilder();
            for(int i = headerKeyBeg; i < headerKeyEnd; ++i){
                byte b = buffer.get(i);
                sb.append((char) b);
            }
            return sb.toString();
        }catch (Exception e){
            //TODO
        }
        return null;
    }

    public String getHeaderValue(){
        try{
            StringBuilder sb = new StringBuilder();
            for(int i = headerValueBeg; i < headerValueEnd; ++i){
                byte b = buffer.get(i);
                sb.append((char) b);
            }
            return sb.toString();
        }catch (Exception e){
            //TODO
        }
        return null;
    }
}
