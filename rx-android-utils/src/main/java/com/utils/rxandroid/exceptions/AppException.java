package com.utils.rxandroid.exceptions;

import android.support.annotation.StringRes;

import lombok.Getter;

/**
 * Created by Nick Unuchek on 21.12.2017.
 */

public class AppException extends RuntimeException {
    @StringRes @Getter private int messageResId;
    public AppException() {
        super();
    }
    public AppException(@StringRes int messageResId) {
        super();
        this.messageResId = messageResId;
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(Throwable cause) {
        super(cause);
    }
}
