package com.example.shouhutest.KeepActive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PatternMatcher;

import java.lang.ref.WeakReference;

public class KeepManager {
    private static KeepManager mInstance;
    KeepReceiver mKeepReceiver;
    WeakReference<Activity> mKeepActivity;

    public static KeepManager getInstance() {
        if (mInstance==null) mInstance= new KeepManager();
        return mInstance;
    }

    public void registerKeep(Context context){
        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction("*");
        filter.addDataPath(".*",android.os.PatternMatcher.PATTERN_SIMPLE_GLOB);
        filter.addDataPath(".*", PatternMatcher.PATTERN_LITERAL);
        filter.addDataPath(".*", PatternMatcher.PATTERN_PREFIX);
        filter.addDataPath("",android.os.PatternMatcher.PATTERN_SIMPLE_GLOB);
//        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        filter.addAction("vivo.intent.action.UPSLIDE_PANEL_STATE_CHANGED");
        mKeepReceiver = new KeepReceiver();
        context.registerReceiver(mKeepReceiver,filter);

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("com.example.shouhutest.alarmLoop");
        filter2.addAction(Intent.ACTION_TIME_TICK);
        context.registerReceiver(new LoopBroadCast(),filter2);
    }


    public void  unregisterKeep(Context context){
        if (mKeepReceiver != null)
            context.unregisterReceiver(mKeepReceiver);
    }


    public void startKeep(Context context){
        Intent intent = new Intent(context,KeepActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void  finishKeep(){
        if (mKeepActivity!=null){
            Activity activity = mKeepActivity.get();
            if (activity!=null)
                activity.finish();

            mKeepActivity = null;
        }
    }


    public void setKeep(KeepActivity keep){
        mKeepActivity = new WeakReference<Activity>(keep);
    }
}
