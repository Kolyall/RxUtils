package com.rxutils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.utils.rxandroid.RxSingle;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Disposable subscribe = Single.just(true).compose(RxSingle.builder().async().build()).subscribe();
    }
}
