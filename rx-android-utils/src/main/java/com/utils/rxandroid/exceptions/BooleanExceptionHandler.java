package com.utils.rxandroid.exceptions;

import rx.functions.Action1;

public class BooleanExceptionHandler implements Action1<Boolean> {

    private final boolean mAssert;
    private final FlowException mException;

    public BooleanExceptionHandler(final boolean mAssert, final FlowException exception) {
        this.mAssert = mAssert;
        this.mException = exception;
    }

    @Override
    public void call(final Boolean result) {
        if (result != null && (result != mAssert)) {
            throw mException;
        }
    }
}
