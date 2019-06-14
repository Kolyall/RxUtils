package com.utils.rxandroid;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Nick Unuchek on 29.05.2017.
 */

public class RxMaybe {
    private static final String TAG = RxMaybe.class.getSimpleName();
    private final Scheduler mWorkerScheduler;
    private final Scheduler mMainScheduler;

    private List<MaybeTransformer<?, ?>> transformers;

    public static RxMaybe builder(Scheduler workerScheduler, Scheduler mainScheduler) {
        return new RxMaybe(workerScheduler, mainScheduler);
    }

    public static RxMaybe builder() {
        return new RxMaybe(Schedulers.io(), AndroidSchedulers.mainThread());
    }

    private RxMaybe(Scheduler workerScheduler, Scheduler scheduler) {
        this.transformers = new ArrayList<>();
        mWorkerScheduler = workerScheduler;
        mMainScheduler = scheduler;
    }

    public <T> MaybeTransformer<T, T> build() {
        return observable -> {
            for (MaybeTransformer<?, ?> transformer : transformers) {
                //noinspection unchecked
                observable = observable.compose(
                        (MaybeTransformer<T, T>) transformer
                );
            }
            return observable;
        };
    }

    public RxMaybe async() {
        transformers.add(
                observable -> observable
                        .subscribeOn(mWorkerScheduler)
                        .observeOn(mMainScheduler)
        );
        return this;
    }

    public RxMaybe sync() {
        transformers.add(
                observable -> observable
                        .subscribeOn(mMainScheduler)
                        .observeOn(mWorkerScheduler)
        );
        return this;
    }

    public RxMaybe io() {
        transformers.add(
                observable -> observable
                        .subscribeOn(mWorkerScheduler)
                        .observeOn(mWorkerScheduler)
        );
        return this;
    }

    @NonNull
    public RxMaybe errorSkip() {
        transformers.add(
                observable -> observable
                        .onErrorResumeNext(throwable -> {
                            return Maybe.empty();
                        }));
        return this;
    }

    @NonNull
    public RxMaybe errorHandler() {
        transformers.add(
                observable -> observable
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
//                        return Maybe.error(err);
//                    }
                            throwable.printStackTrace();
                            return Maybe.empty();
                        }));
        return this;
    }

    @NonNull
    public RxMaybe onErrorResumeNext(Function<Throwable, Maybe<?>> function) {
        transformers.add(
                observable -> observable
                        .onErrorResumeNext(function));
        return this;
    }

    public RxMaybe progressOn(HasProgress hasProgress) {
        if (hasProgress != null) {
            transformers.add(
                    observable -> observable
                            .doOnSubscribe(disposable -> hasProgress.showProgress())
                            .doOnError(throwable -> hasProgress.hideProgress())
                            .doOnComplete(hasProgress::hideProgress)
                            .doOnDispose(hasProgress::hideProgress)
                            .doAfterTerminate(hasProgress::hideProgress))
            ;
        }
        return this;
    }

    public RxMaybe errorOn(HasError hasError) {
        if (hasError != null) {
            transformers.add(
                    observable -> observable
                            .doOnSubscribe(disposable -> hasError.hideError())
                            .doOnError(hasError::showError));
        }
        return this;
    }

    public RxMaybe disable(Disableable disableable) {
        if (disableable != null) {
            transformers.add(
                    observable -> observable
                            .doOnSubscribe(disposable -> disableable.setEnabled(false))
                            .doOnError(throwable -> disableable.setEnabled(true))
                            .doOnComplete(() -> disableable.setEnabled(true))
                            .doOnDispose(() -> disableable.setEnabled(true))
                            .doAfterTerminate(() -> disableable.setEnabled(true))
            );
        }
        return this;
    }

    public RxMaybe nonClickable(NonClickable clickable) {
        if (clickable != null) {
            transformers.add(
                    observable -> observable
                            .doOnSubscribe(disposable -> clickable.setClickable(false))
                            .doOnError(throwable -> clickable.setClickable(true))
                            .doOnComplete(() -> clickable.setClickable(true))
                            .doOnDispose(() -> clickable.setClickable(true))
                            .doAfterTerminate(() -> clickable.setClickable(true))
            );
        }
        return this;
    }

    /* Usage
    *  Maybe<Integer> invite = mAPI.invite()
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
