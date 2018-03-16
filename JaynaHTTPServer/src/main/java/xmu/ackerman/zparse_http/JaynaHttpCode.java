package xmu.ackerman.zparse_http;

public enum JaynaHttpCode {
    OK,

    PARSE_MOVING,      //TK_AGAIN

    PARSE_HTTP_PROTOCOL_EXCEPTION,
    PARSE_HTTP_HEADER_EXCEPTION,
    PARSE_HTTP_METHOD_EXCEPTION,
    PARSE_HTTP_REQUEST_EXCEPTION,
}
