package com.example.mobilepjapp;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this, "de5d58e61ef8906dacbfa8f7e6fe0e37");
    }
}