package com.example.shouhutest;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.shouhutest.Desktop.DesktopWindow;

public class MyApplication extends Application {

    public static DesktopWindow desktopWindow;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
     //   desktopWindow = new DesktopWindow(this);
//        PushManager.getInstance().initialize(this);
//        PushManager.getInstance().setDebugLogger(this, new IUserLoggerInterface() {
//            @Override
//            public void log(String s) {
//                Log.e("PUSH_LOG", "log: "+s);
//            }
//        });
        Log.e("TAG", "onCreate: Application");
        context=this;
    }

}
