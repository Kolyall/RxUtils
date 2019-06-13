package com.utils.rxandroid.exceptions

import android.support.annotation.StringRes

/**
 * Created by Nick Unuchek on 21.12.2017.
 */

class AppException : RuntimeException {
    @StringRes
    var messageResId: Int = -1

    constructor() : super()

    constructor(@StringRes messageResId: Int) : super() {
        this.messageResId = messageResId
    }

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)
}
