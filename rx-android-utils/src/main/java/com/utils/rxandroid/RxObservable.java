package com.utils.rxandroid;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Nick Unuchek on 29.05.2017.
 */

public class RxObservable {
    private static final String TAG = RxObservable.class.getSimpleName();
    private final Scheduler mWorkerScheduler;
    private final Scheduler mMainScheduler;

    private List<ObservableTransformer<?, ?>> transformers;

    public static RxObservable builder(Scheduler workerScheduler, Scheduler mainScheduler) {
        return new RxObservable(workerScheduler, mainScheduler);
    }

    public static RxObservable builder() {
        return new RxObservable(Schedulers.io(), AndroidSchedulers.mainThread());
    }

    private RxObservable(Scheduler workerScheduler, Scheduler scheduler) {
        this.transformers = new ArrayList<>();
        mWorkerScheduler = workerScheduler;
        mMainScheduler = scheduler;
    }

    public <T> ObservableTransformer<T, T> build() {
        return observable -> {
            for (int i = 0; i < transformers.size(); i++) {
                //noinspection unchecked
                observable = observable.compose(
                        (ObservableTransformer<T, T>) transformers.get(i)
                );
            }
            return observable;
        };
    }

    public RxObservable async() {
        transformers.add(observable -> observable
                .subscribeOn(mWorkerScheduler)
                .observeOn(mMainScheduler)
        );
        return this;
    }

    public RxObservable sync() {
        transformers.add(observable -> observable
                .subscribeOn(mMainScheduler)
                .observeOn(mWorkerScheduler)
        );
        return this;
    }

    public RxObservable io() {
        transformers.add(observable -> observable
                .subscribeOn(mWorkerScheduler)
                .observeOn(mWorkerScheduler)
        );
        return this;
    }

    @NonNull
    public RxObservable errorSkip() {
        transformers.add(observable -> observable
                .onErrorResumeNext(throwable -> {
                    return Observable.empty();
                }));
        return this;
    }

    @NonNull
    public RxObservable errorHandler() {
        transformers.add(observable -> observable
                .onErrorResumeNext(throwable -> {
//                    if (throwable instanceof HttpException) {
//                        String body = "";
//                        HttpException he = (HttpException) throwable;
//                        Log.i(TAG, "code: " + he.code());
//                        try {
//                            body = he.response().errorBody().string();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        Response response = gson.fromJson(body, Response.class);
//                        Throwable err = response.getError() != null ?
//                                new Throwable(response.getError()) : throwable;
//                        return Observable.error(err);
//                    }
                    throwable.printStackTrace();
                    return Observable.empty();
                }));
        return this;
    }

    @NonNull
    public RxObservable onErrorResumeNext(Function<Throwable, Observable<?>> function) {
        transformers.add(observable -> observable
                .onErrorResumeNext(function));
        return this;
    }

    public RxObservable progressOn(HasProgress hasProgress) {
        if (hasProgress != null) {
            transformers.add(observable -> observable
                    .doOnSubscribe(disposable -> hasProgress.showProgress())
                    .doOnError(throwable -> hasProgress.hideProgress())
                    .doOnComplete(hasProgress::hideProgress)
                    .doOnDispose(hasProgress::hideProgress)
                    .doOnTerminate(hasProgress::hideProgress)
                    .doAfterTerminate(hasProgress::hideProgress))
            ;
        }
        return this;
    }

    public RxObservable errorOn(HasError hasError) {
        if (hasError != null) {
            transformers.add(observable -> observable
                    .doOnSubscribe(disposable -> hasError.hideError())
                    .doOnError(hasError::showError));
        }
        return this;
    }

    public RxObservable disable(Disableable disableable) {
        if (disableable != null) {
            transformers.add(
                    observable -> observable
                            .doOnSubscribe(disposable -> disableable.setEnabled(false))
                            .doOnError(throwable -> disableable.setEnabled(true))
                            .doOnComplete(() -> disableable.setEnabled(true))
                            .doOnDispose(() -> disableable.setEnabled(true))
                            .doOnTerminate(() -> disableable.setEnabled(true))
                            .doAfterTerminate(() -> disableable.setEnabled(true))
            );
        }
        return this;
    }

    public RxObservable nonClickable(NonClickable clickable) {
        if (clickable != null) {
            transformers.add(
                    observable -> observable
                            .doOnSubscribe(disposable -> clickable.setClickable(false))
                            .doOnError(throwable -> clickable.setClickable(true))
                            .doOnComplete(() -> clickable.setClickable(true))
                            .doOnDispose(() -> clickable.setClickable(true))
                            .doOnTerminate(() -> clickable.setClickable(true))
                            .doAfterTerminate(() -> clickable.setClickable(true))
            );
        }
        return this;
    }

    public RxObservable retry(int maxRetryCount, long delay, TimeUnit unit) {
        transformers.add(observable -> observable
                .retryWhen(RxObservable.exponentialBackoff(maxRetryCount, delay, unit))
        );
        return this;
    }

    /*
     * retry subscribe to observable one time after internet connected action
     * */
    public RxObservable retryOnInternetConnection(InternetStateProvider baseView) {
        if (baseView != null) {
            transformers.add(observable -> observable
                    .retryWhen(source -> source
                            .flatMap(throwable -> Observable.interval(2, TimeUnit.SECONDS)
                                    .map(aLong -> baseView.isInternetConnected())
                                    .filter(isInternetConnected -> isInternetConnected)
                                    .take(1)
                            )
                    )
            );
        }
        return this;
    }

    public static Function<Observable<? extends Throwable>, Observable<?>> exponentialBackoff(
            int maxRetryCount, long delay, TimeUnit unit) {
        return errors -> errors
                .zipWith(Observable.range(1, maxRetryCount), (error, retryCount) -> retryCount)
                .flatMap(retryCount -> Observable.timer((long) Math.pow(delay, retryCount), unit));
    }

    /* Usage
    *  Observable<Integer> invite = mAPI.invite()
    *  .compose(
                NetworkUtils.builder(this)
                        .async()
                        .progressBar()
                        .errorToast()
                        .build())
                .subscribe(
                        it -> Log.i(TAG, "onClickTalkWithFriend it: " + it.toString()),
                        Throwable::printStackTrace,
                        () -> Log.d(TAG, "onClickTalkWithFriend() called")
                );
    * */
}
