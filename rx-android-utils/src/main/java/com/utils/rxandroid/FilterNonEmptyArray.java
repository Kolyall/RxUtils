package com.utils.rxandroid;

import io.reactivex.functions.Predicate;

/**
 * Created by Nick Unuchek on 24.10.2017.
 */

public class FilterNonEmptyArray<T> implements Predicate<T[]> {
    public static final String TAG = FilterNonEmptyArray.class.getSimpleName();

    @Override
    public boolean test(T[] list) throws Exception {
        ThreadUtils.printThread(TAG);
        return list != null && list.length!=0;
    }
}
