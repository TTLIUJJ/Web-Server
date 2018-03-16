package xmu.practice.chargen;

public enum ParseHttpRequestLineState {
    SW_START,
    SW_METHOD,
    SW_SPACES_BEFORE_URI,
    SW_AFTER_SLASH_IN_URI,
    SW_HTTP,
    SW_HTTP_H,
    SW_HTTP_HT,
    SW_HTTP_HTT,
    SW_HTTP_HTTP,
    SW_FIRST_MAJOR_DIGIT,
    SW_MAJOR_DIGIT,
    SW_FIRST_MINOR_DIGIT,
    SW_MINOR_DIGIT,
    SW_SPACES_AFTER_DIGIT,
    SW_ALMOST_DONE,
}
