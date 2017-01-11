package com.qixiaoyi.lihualihua;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import network.RetrofitHelper;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SplashActivity extends RxAppCompatActivity {

    @BindView(R.id.splash_iv)
    ImageView mSplashImage;

    @BindView(R.id.splash_logo)
    ImageView mSplashLogo;

    @BindView(R.id.splash_default_iv)
    ImageView mSplashDefaultIv;

    private Subscription mSubscribe;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mUnbinder = ButterKnife.bind(this);//依赖注入
    }

    @Override
    protected void onResume()
    {

        super.onResume();
//        setUpSplash();
    }
    private void setUpSplash()
    {

        RetrofitHelper.getSplashApi()
                .getSplashImage()
                .compose(bindToLifecycle())
                .map(splashInfo -> splashInfo.getData().get(0).getThumbUrl())//变换  将splashInfo转换成String
                .observeOn(AndroidSchedulers.mainThread())//UI线程消费线程
                .flatMap(new Func1<String,Observable<Long>>()
                {

                    @Override
                    public Observable<Long> call(String s)
                    {

                        loadImageUrl(s);
                        return Observable.timer(2000, TimeUnit.MILLISECONDS);
                    }
                })
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())//IO线程发生事件
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {

                    finishTask();
                }, throwable -> {

                    Observable.timer(2, TimeUnit.SECONDS)
                            .compose(bindToLifecycle())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(aLong -> {
                                finishTask();
                            });
                });
    }

    private void loadImageUrl(String s)
    {

        Glide.with(SplashActivity.this)
                .load(s)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mSplashImage);
        mSplashLogo.setVisibility(View.GONE);
        mSplashDefaultIv.setVisibility(View.GONE);
    }

    private void finishTask()
    {


        SplashActivity.this.finish();
        //切换activity的动画设置
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        mUnbinder.unbind();
    }
}
