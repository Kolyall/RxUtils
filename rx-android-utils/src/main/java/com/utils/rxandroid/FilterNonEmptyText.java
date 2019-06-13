package com.utils.rxandroid;

import android.text.TextUtils;

import io.reactivex.functions.Predicate;

/**
 * Created by User on 10.04.2017.
 */

public class FilterNonEmptyText implements Predicate<CharSequence> {
    public static final String TAG = FilterNonEmptyText.class.getSimpleName();

    @Override
    public boolean test(CharSequence text) throws Exception {
        ThreadUtils.printThread(TAG);
        return !TextUtils.isEmpty(text);
    }
}
