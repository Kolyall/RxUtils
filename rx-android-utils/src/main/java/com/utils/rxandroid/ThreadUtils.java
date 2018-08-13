package com.utils.rxandroid;

import android.util.Log;

public class ThreadUtils {
    public static final String TAG = ThreadUtils.class.getSimpleName();

    public static void printThread(String tag) {
        String threadName = Thread.currentThread().getName();
        if (threadName.equals("main")) {
            Log.e(TAG, tag + " is called from " + threadName + " Thread");
        }
    }

    public static void printThreadName(String tag) {
        String threadName = Thread.currentThread().getName();
        Log.e(TAG, tag + " is called from " + threadName + " Thread");
    }
}
