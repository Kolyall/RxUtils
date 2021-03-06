package com.rxutils

import com.utils.rxandroid.HasError
import com.utils.rxandroid.HasProgress
import com.utils.rxandroid.RxObservable
import com.utils.rxandroid.RxSingle
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
class RxObservableUnitTests {
    private val testScheduler = TestScheduler()

    @Test
    fun test01() {
        Observable.range(1, 5)
            .compose(RxObservable.builder(testScheduler, testScheduler).async().build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertComplete()
            .assertResult(1, 2, 3, 4, 5)
            .dispose()
    }

    @Test
    fun test02() {
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
    fun test03() {
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
    fun test04() {
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
    fun test05() {
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
    fun test06() {
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

    @Mock
    val view: HasProgress = object : HasProgress {
        override fun showProgress() {
        }

        override fun hideProgress() {
        }
    }

    @Mock
    val errorView: HasError = object : HasError {
        override fun hideError() {
        }

        override fun showError(throwable: Throwable?) {
        }
    }

    @Test
    fun test07() {
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
    fun test08() {
        val runtimeException = RuntimeException()
        Observable.error<Boolean>(runtimeException)
            .compose(RxObservable.builder(testScheduler, testScheduler).async().progressOn(view).errorOn(errorView).build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertError(runtimeException)
            .dispose()
        verify(view, times(1)).showProgress()
        verify(view, times(1)).hideProgress()
        verify(errorView, times(1)).hideError()
        verify(errorView, times(1)).showError(runtimeException)
    }

    @Test
    fun test09() {
        val array = arrayOf(1, 2, 3)
        Observable.just(array)
            .filter(Array<Int>::isNotEmpty)
            .compose(RxObservable.builder(testScheduler, testScheduler).async().progressOn(view).errorOn(errorView).build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertResult(array)
            .dispose()
    }
    @Test
    fun test10() {
        val array = listOf("", "2", "3")
        Observable.just(array)
            .filter(List<String>::isNotEmpty)
            .flatMap { Observable.fromIterable(it) }
            .filter(CharSequence::isNotEmpty)
            .toList()
            .toObservable()
            .compose(RxObservable.builder(testScheduler, testScheduler).async().progressOn(view).errorOn(errorView).build())
            .test()
            .also { testScheduler.triggerActions() }
            .assertResult(listOf("2", "3"))
            .dispose()
    }
}