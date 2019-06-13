package com.utils.rxandroid;

import java.util.Collection;

import io.reactivex.functions.Predicate;

/**
 * Created by Nick Unuchek on 24.10.2017.
 */

public class FilterNonEmptyList<T> implements Predicate<Collection<T>> {
    public static final String TAG = FilterNonEmptyList.class.getSimpleName();

    @Override
    public boolean test(Collection<T> list) throws Exception {
        ThreadUtils.printThread(TAG);
        return list != null && !list.isEmpty();
    }
}