package com.utils.rxandroid;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Nick Unuchek on 29.05.2017.
 */

public class RxSingle {
    private static final String TAG = RxSingle.class.getSimpleName();
    private final Scheduler mWorkerScheduler;
    private final Scheduler mMainScheduler;

    private List<SingleTransformer<?, ?>> transformers;

    public static RxSingle builder(Scheduler workerScheduler, Scheduler mainScheduler) {
        return new RxSingle(workerScheduler, mainScheduler);
    }

    public static RxSingle builder() {
        return new RxSingle(Schedulers.io(), AndroidSchedulers.mainThread());
    }

    private RxSingle(Scheduler workerScheduler, Scheduler scheduler) {
        this.transformers = new ArrayList<>();
        mWorkerScheduler = workerScheduler;
        mMainScheduler = scheduler;
    }

    public <T> SingleTransformer<T, T> build() {
        return single -> {
            for (SingleTransformer<?, ?> transformer : transformers) {
                //noinspection unchecked
                single = single.compose(
                        (SingleTransformer<T, T>) transformer
                );
            }
            return single;
        };
    }

    public RxSingle async() {
        transformers.add(
                single -> single
                        .subscribeOn(mWorkerScheduler)
                        .observeOn(mMainScheduler)
        );
        return this;
    }

    public RxSingle sync() {
        transformers.add(
                single -> single
                        .subscribeOn(mMainScheduler)
                        .observeOn(mWorkerScheduler)
        );
        return this;
    }

    public RxSingle io() {
        transformers.add(
                single -> single
                        .subscribeOn(mWorkerScheduler)
                        .observeOn(mWorkerScheduler)
        );
        return this;
    }

    public RxSingle progressOn(HasProgress hasProgress) {
        if (hasProgress != null) {
            transformers.add(
                    single -> single
                            .doOnSubscribe(disposable -> hasProgress.showProgress())
                            .doOnError(throwable -> hasProgress.hideProgress())
                            .doOnDispose(hasProgress::hideProgress)
                            .doOnSuccess(o -> hasProgress.hideProgress()))
            ;
        }
        return this;
    }

    public RxSingle errorOn(HasError hasError) {
        if (hasError != null) {
            transformers.add(
                    single -> single
                            .doOnSubscribe(disposable -> hasError.hideError())
                            .doOnError(hasError::showError));
        }
        return this;
    }

    public RxSingle disable(Disableable disableable) {
        if (disableable != null) {
            transformers.add(
                    single -> single
                            .doOnSubscribe(disposable -> disableable.setEnabled(false))
                            .doOnError(throwable -> disableable.setEnabled(true))
                            .doOnDispose(() -> disableable.setEnabled(true))
                            .doOnSuccess(o -> disableable.setEnabled(true))
            );
        }
        return this;
    }

    public RxSingle nonClickable(NonClickable clickable) {
        if (clickable != null) {
            transformers.add(
                    single -> single
                            .doOnSubscribe(disposable -> clickable.setClickable(false))
                            .doOnError(throwable -> clickable.setClickable(true))
                            .doOnDispose(() -> clickable.setClickable(true))
                            .doOnSuccess(o -> clickable.setClickable(true))
            );
        }
        return this;
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
