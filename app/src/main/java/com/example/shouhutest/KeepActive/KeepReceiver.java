package com.example.shouhutest.KeepActive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

class KeepReceiver extends BroadcastReceiver {
    String TAG = "KeepReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "onReceive: action:"+action );

        if (action.equals(Intent.ACTION_SCREEN_OFF)){
            Log.e(TAG, "onReceive: 关闭屏幕");
            KeepManager.getInstance().startKeep(context);
        }else if (action.equals(Intent.ACTION_SCREEN_ON)){
            Log.e(TAG, "onReceive: 开启屏幕");
            KeepManager.getInstance().finishKeep();
        }
    }
}
