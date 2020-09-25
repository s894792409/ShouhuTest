package com.example.shouhutest.KeepActive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction("vivo.intent.action.UPSLIDE_PANEL_STATE_CHANGED");
        mKeepReceiver = new KeepReceiver();
        context.registerReceiver(mKeepReceiver,filter);
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
