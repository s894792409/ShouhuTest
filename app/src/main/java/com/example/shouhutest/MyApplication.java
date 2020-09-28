package com.example.shouhutest;

import android.app.Application;

import com.example.shouhutest.Desktop.DesktopWindow;

public class MyApplication extends Application {

    public static DesktopWindow desktopWindow;


    @Override
    public void onCreate() {
        super.onCreate();
    }
}
