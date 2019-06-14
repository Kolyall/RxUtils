package com.rxutils

import com.utils.rxandroid.HasError
import com.utils.rxandroid.HasProgress
import com.utils.rxandroid.RxFlowable
import com.utils.rxandroid.RxObservable
import com.utils.rxandroid.RxSingle
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(MockitoJUnitRunner::class)
class ExampleUnitTest {
    private val testScheduler = TestScheduler()

    @Test
    fun testRxFlowable1() {
        Flowable.range(1, 5)
            .compose(RxFlowable.builder(testScheduler, testScheduler).async().build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertComplete()
            .assertResult(1, 2, 3, 4, 5)
            .dispose()
    }

    @Test
    fun testRxObservable1() {
        Observable.range(1, 5)
            .compose(RxObservable.builder(testScheduler, testScheduler).async().build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertComplete()
            .assertResult(1, 2, 3, 4, 5)
            .dispose()
    }

    @Test
    fun testRxObservable2() {
        val items = arrayListOf(true, false, true, false)
        Observable.fromIterable(items)
            .compose(RxObservable.builder(testScheduler, testScheduler).async().build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertComplete()
            .assertResult(true, false, true, false)
            .dispose()
    }

    @Test
    fun testRxObservable3() {
        val items = arrayListOf(true, false, true, false)
        Observable.fromIterable(items)
            .filter { t -> t }
            .compose(RxObservable.builder(testScheduler, testScheduler).async().build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertComplete()
            .assertResult(true, true)
            .dispose()
    }

    @Test
    fun testRxObservable4() {
        val items = arrayListOf(true, false, true, false)
        Observable.fromIterable(items)
            .filter { t -> t }
            .map { t -> t.toString() }
            .toList()
            .compose(RxSingle.builder(testScheduler, testScheduler).async().build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertComplete()
            .assertResult(arrayListOf("true", "true"))
            .dispose()
    }

    @Test
    fun testRxObservable5() {
        val items = arrayListOf(true, false, true, false)
        Observable.fromIterable(items)
            .filter { t -> t }
            .map { t -> t.toString() }
            .toList()
            .toObservable()
            .compose(RxObservable.builder(testScheduler, testScheduler).async().build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertComplete()
            .assertResult(arrayListOf("true", "true"))
            .dispose()
    }

    @Test
    fun testRxObservable6() {
        val items = arrayListOf(true, false, true, false)
        Observable.fromIterable(items)
            .filter { t -> t }
            .map { t -> t.toString() }
            .toList()
            .toObservable()
            .compose(RxObservable.builder(testScheduler, testScheduler).async().build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertComplete()
            .assertResult(arrayListOf("true", "true"))
            .dispose()
    }

    @Mock val view : HasProgress= object : HasProgress{
        override fun showProgress() {
        }

        override fun hideProgress() {
        }
    }

    @Mock val errorView : HasError= object : HasError {
        override fun hideError() {
        }

        override fun showError(throwable: Throwable?) {
        }
    }

    @Test
    fun testRxObservable7() {
        val items = arrayListOf(true, false, true, false)
        Observable.fromIterable(items)
            .filter { t -> t }
            .map { t -> t.toString() }
            .toList()
            .toObservable()
            .compose(RxObservable.builder(testScheduler, testScheduler).async().progressOn(view).build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertComplete()
            .assertResult(arrayListOf("true", "true"))
            .dispose()
        verify(view, times(1)).showProgress()
        verify(view, times(1)).hideProgress()
    }

    @Test
    fun testRxObservable8() {
        val runtimeException = RuntimeException()
        Observable.error<Boolean>(runtimeException)
            .compose(RxObservable.builder(testScheduler, testScheduler).async().errorOn(errorView).build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertError(runtimeException)
            .dispose()
        verify(errorView, times(1)).hideError()
        verify(errorView, times(1)).showError(runtimeException)
    }
}