package com.utils.rxandroid;

public interface HasError {

    void showError(Throwable throwable);

    void hideError();
}
