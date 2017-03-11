package com.example.administrator.okhttplemondemo;

import android.app.Application;

import com.example.lemonokhttp.http.OkHttpLemon;

/**
 * Created by ShuWen on 2017/3/11.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpLemon.init().initOptions();
    }
}
