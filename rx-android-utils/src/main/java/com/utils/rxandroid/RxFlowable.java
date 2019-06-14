package com.utils.rxandroid;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Nick Unuchek on 29.05.2017.
 */

public class RxFlowable {
    private static final String TAG = RxFlowable.class.getSimpleName();
    private final Scheduler mWorkerScheduler;
    private final Scheduler mMainScheduler;

    private List<FlowableTransformer<?, ?>> transformers;

    public static RxFlowable builder(Scheduler workerScheduler, Scheduler mainScheduler) {
        return new RxFlowable(workerScheduler, mainScheduler);
    }

    public static RxFlowable builder() {
        return new RxFlowable(Schedulers.io(), AndroidSchedulers.mainThread());
    }

    private RxFlowable(Scheduler workerScheduler, Scheduler scheduler) {
        this.transformers = new ArrayList<>();
        mWorkerScheduler = workerScheduler;
        mMainScheduler = scheduler;
    }

    public <T> FlowableTransformer<T, T> build() {
        return observable -> {
            for (int i = 0; i < transformers.size(); i++) {
                //noinspection unchecked
                observable = observable.compose(
                        (FlowableTransformer<T, T>) transformers.get(i)
                );
            }
            return observable;
        };
    }

    public RxFlowable async() {
        transformers.add(observable -> observable
                .subscribeOn(mWorkerScheduler)
                .observeOn(mMainScheduler)
        );
        return this;
    }

    public RxFlowable sync() {
        transformers.add(observable -> observable
                .subscribeOn(mMainScheduler)
                .observeOn(mWorkerScheduler)
        );
        return this;
    }

    public RxFlowable io() {
        transformers.add(observable -> observable
                .subscribeOn(mWorkerScheduler)
                .observeOn(mWorkerScheduler)
        );
        return this;
    }

    @NonNull
    public RxFlowable errorSkip() {
        transformers.add(observable -> observable
                .onErrorResumeNext(throwable -> {
                    return Flowable.empty();
                }));
        return this;
    }

    @NonNull
    public RxFlowable errorHandler() {
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
//                        return Flowable.error(err);
//                    }
                    throwable.printStackTrace();
                    return Flowable.empty();
                }));
        return this;
    }

    @NonNull
    public RxFlowable onErrorResumeNext(Function<Throwable, Flowable<?>> function) {
        transformers.add(observable -> observable
                .onErrorResumeNext(function));
        return this;
    }

    public RxFlowable progressOn(HasProgress hasProgress) {
        if (hasProgress != null) {
            transformers.add(observable -> observable
                    .doOnSubscribe(disposable -> hasProgress.showProgress())
                    .doOnError(throwable -> hasProgress.hideProgress())
                    .doOnComplete(hasProgress::hideProgress)
                    .doOnTerminate(hasProgress::hideProgress)
                    .doAfterTerminate(hasProgress::hideProgress))
            ;
        }
        return this;
    }

    public RxFlowable errorOn(HasError hasError) {
        if (hasError != null) {
            transformers.add(observable -> observable
                    .doOnSubscribe(disposable -> hasError.hideError())
                    .doOnError(hasError::showError));
        }
        return this;
    }

    public RxFlowable disable(Disableable disableable) {
        if (disableable != null) {
            transformers.add(
                    observable -> observable
                            .doOnSubscribe(disposable -> disableable.setEnabled(false))
                            .doOnError(throwable -> disableable.setEnabled(true))
                            .doOnComplete(() -> disableable.setEnabled(true))
                            .doOnTerminate(() -> disableable.setEnabled(true))
                            .doAfterTerminate(() -> disableable.setEnabled(true))
            );
        }
        return this;
    }

    public RxFlowable nonClickable(NonClickable clickable) {
        if (clickable != null) {
            transformers.add(
                    observable -> observable
                            .doOnSubscribe(disposable -> clickable.setClickable(false))
                            .doOnError(throwable -> clickable.setClickable(true))
                            .doOnComplete(() -> clickable.setClickable(true))
                            .doOnTerminate(() -> clickable.setClickable(true))
                            .doAfterTerminate(() -> clickable.setClickable(true))
            );
        }
        return this;
    }

    public RxFlowable retry(int maxRetryCount, long delay, TimeUnit unit) {
        transformers.add(observable -> observable
                .retryWhen(RxFlowable.exponentialBackoff(maxRetryCount, delay, unit))
        );
        return this;
    }

    /*
     * retry subscribe to observable one time after internet connected action
     * */
    public RxFlowable retryOnInternetConnection(InternetStateProvider baseView) {
        if (baseView != null) {
            transformers.add(observable -> observable
                    .retryWhen(source -> source
                            .flatMap(throwable -> Flowable.interval(2, TimeUnit.SECONDS)
                                    .map(aLong -> baseView.isInternetConnected())
                                    .filter(isInternetConnected -> isInternetConnected)
                                    .take(1)
                            )
                    )
            );
        }
        return this;
    }

    public static Function<Flowable<? extends Throwable>, Flowable<?>> exponentialBackoff(
            int maxRetryCount, long delay, TimeUnit unit) {
        return errors -> errors
                .zipWith(Flowable.range(1, maxRetryCount), (error, retryCount) -> retryCount)
                .flatMap(retryCount -> Flowable.timer((long) Math.pow(delay, retryCount), unit));
    }

    /* Usage
    *  Flowable<Integer> invite = mAPI.invite()
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
