package xmu.practice.chargen;

public enum ParseHttpBodyState {
    SW_START,
    SW_KEY,
    SW_SPACES_BEFORE_COLON,
    SW_SPACES_AFTER_COLON,
    SW_VALUE,
    SW_CR,
    SW_CRLF,
    SW_CRLFCR
}
