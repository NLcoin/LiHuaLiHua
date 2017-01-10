package com.qixiaoyi.lihualihua;

import android.os.Bundle;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class SplashActivity extends RxAppCompatActivity {

    private Subscription mSubscribe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mSubscribe = Observable.timer(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Action1)(aLong)->{
                   startAni();
                });
    }

    private void startAni() {

    }


    @Override
    public void onBackPressed() {//解开订阅
        super.onBackPressed();
        if (mSubscribe != null && !mSubscribe.isUnsubscribed()){
            mSubscribe.unsubscribe();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
