package com.utils.rxandroid.exceptions

open class FlowException : RuntimeException {
    constructor()

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)
}
