package com.rxutils

import com.utils.rxandroid.RxFlowable
import io.reactivex.Flowable
import io.reactivex.schedulers.TestScheduler
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    private val testScheduler = TestScheduler()

    @Test
    fun testCorrect() {
        Flowable.range(1, 5)
            .compose(RxFlowable.builder(testScheduler, testScheduler).async().build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertComplete()
            .assertResult(1, 2, 3, 4, 5)
            .dispose()
    }

    @Test
    fun test2Correct() {
        Flowable.range(1, 5)
            .compose(RxFlowable.builder(testScheduler, testScheduler).async().build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertComplete()
            .assertResult(1, 2, 3, 4, 5)
            .dispose()
    }
}