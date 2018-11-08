package com.utils.rxandroid;

import android.os.Build;
import android.os.Looper;
import android.util.Log;

public class ThreadUtils {
    public static final String TAG = ThreadUtils.class.getSimpleName();

    /**
     * check that the executed command is not in Main Thread, otherwise will print it in logcat
     * */
    public static void printThread(String tag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? Looper.getMainLooper().isCurrentThread()
                : Thread.currentThread() == Looper.getMainLooper().getThread()) {
            printThreadName(tag);
        }
    }

    public static void printThreadName(String tag) {
        Log.e(TAG, tag + " is called from " + Thread.currentThread().getName() + " Thread");
    }
}
