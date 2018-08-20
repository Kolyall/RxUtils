package com.utils.rxandroid.exceptions;

import rx.functions.Action1;

public class BooleanExceptionHandler implements Action1<Boolean> {

    private boolean mAssert;
    private FlowException mException;

    public BooleanExceptionHandler(boolean mAssert, FlowException exception) {
        this.mAssert = mAssert;
        this.mException = exception;
    }

    @Override
    public void call(Boolean result) {
        if (result != null && (result == mAssert)) {
            throw mException;
        }
    }
}
