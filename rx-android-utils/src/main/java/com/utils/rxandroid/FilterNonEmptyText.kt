package com.utils.rxandroid

import android.text.TextUtils

import io.reactivex.functions.Predicate

/**
 * Created by User on 10.04.2017.
 */

class FilterNonEmptyText : Predicate<CharSequence> {

    @Throws(Exception::class)
    override fun test(text: CharSequence): Boolean {
        ThreadUtils.printThread(TAG)
        return !TextUtils.isEmpty(text)
    }

    companion object {
        val TAG = FilterNonEmptyText::class.java.simpleName
    }
}
