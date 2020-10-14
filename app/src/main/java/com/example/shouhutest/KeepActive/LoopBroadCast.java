package com.example.shouhutest.KeepActive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class LoopBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.shouhutest.alarmLoop".equals(intent.getAction()) || intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            Log.e("AlarmMangerUtils", "拉起广播 action:"+intent.getAction());

            AlarmMangerUtils.getInstance().getAlarmManager(context).setPendingBroadCast();
            startLocateService(context);

            boolean contextNull = WakeUpUitls.getInstance().isContextNull();
            if (contextNull) {
                WakeUpUitls.getInstance().init(context);
            }
            //获取cpu
            WakeUpUitls.getInstance().acquireWakeLock();
            //点亮屏幕
//            WakeUpUitls.getInstance().wakeupAndUnLock();
        }
    }




    private void startLocateService(Context paramContext) {
        Log.e("TAG", "startLocateService: 定位服务是否正在运行:"+AlarmMangerUtils.isServiceRunning(paramContext,ForegroundService.class.getName()) );
        if (!AlarmMangerUtils.isServiceRunning(paramContext,ForegroundService.class.getName())) {
            Intent intent = new Intent(paramContext, ForegroundService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                paramContext.startForegroundService(intent);
                return;
            }
            paramContext.startService(intent);
        }
    }
}
