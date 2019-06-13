package com.utils.rxandroid.exceptions

class FlowException : RuntimeException {
    constructor()

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)
}
