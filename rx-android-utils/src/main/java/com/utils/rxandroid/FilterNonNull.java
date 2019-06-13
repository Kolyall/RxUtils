package com.utils.rxandroid;

import io.reactivex.functions.Predicate;

/**
 * Created by User on 10.04.2017.
 */

public class FilterNonNull<T> implements Predicate<T> {
    public static final String TAG = FilterNonNull.class.getSimpleName();

    @Override
    public boolean test(T item) throws Exception {
        ThreadUtils.printThread(TAG);
        return item != null;
    }
}
