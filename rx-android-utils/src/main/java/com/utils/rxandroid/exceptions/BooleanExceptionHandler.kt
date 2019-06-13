package com.utils.rxandroid.exceptions


import io.reactivex.functions.Consumer

class BooleanExceptionHandler(
    private val mAssert: Boolean,
    private val mException: RuntimeException
) : Consumer<Boolean> {

    @Throws(Exception::class)
    override fun accept(result: Boolean?) {
        if (result != null && result != mAssert) {
            throw mException
        }
    }
}
