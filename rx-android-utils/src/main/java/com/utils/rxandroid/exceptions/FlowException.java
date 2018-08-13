package com.utils.rxandroid.exceptions;

public class FlowException extends RuntimeException {
    public FlowException() {
    }

    public FlowException(String message) {
        super(message);
    }

    public FlowException(Throwable cause) {
        super(cause);
    }
}
