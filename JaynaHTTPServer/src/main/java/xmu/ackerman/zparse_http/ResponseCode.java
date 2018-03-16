package xmu.ackerman.zparse_http;

public enum ResponseCode {

    OK(200, "OK", ""),
    NOT_FOUND(404, "Not Found", "/error/error_404.html"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "/error/error_500.html");

    private int status;
    private String msg;
    private String filename;

    private ResponseCode(int status, String msg, String filename){
        this.status = status;
        this.msg = msg;
        this.filename = filename;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
