package com.example.shouhutest.KeepActive;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.List;

public class AlarmMangerUtils {
    private static AlarmMangerUtils IMPL = new AlarmMangerUtils();

    private final int INTERVAL = 10000;

    private AlarmManager alarmManager;

    private Context context;

    private int flag = 1;

    private PendingIntent sender;

    private AlarmMangerUtils() {}

    public static AlarmMangerUtils getInstance() { return IMPL; }

    public void cancelAlarm() {
        AlarmManager alarmManager1 = this.alarmManager;
        if (alarmManager1 != null) {
            PendingIntent pendingIntent = this.sender;
            if (pendingIntent != null)
                alarmManager1.cancel(pendingIntent);
        }
    }

    public AlarmMangerUtils getAlarmManager(Context paramContext) {
        this.context = paramContext.getApplicationContext();
        this.alarmManager = (AlarmManager)this.context.getSystemService(Context.ALARM_SERVICE);
        return this;
    }

    @SuppressLint("WrongConstant")
    public AlarmMangerUtils setPendingBroadCast() {
        if (this.context != null) {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent = new Intent();
                //定义广播类型
                intent.setAction("com.example.shouhutest.alarmLoop");
                intent.setComponent(new ComponentName(context.getPackageName(), "com.example.shouhutest.KeepActive.LoopBroadCast"));
                /* intent.setComponent(new ComponentName("com.examplehq.forhelp","com.examplehq.forhelp.MyReceiver"));*/
            } else {
                intent = new Intent(context, ForegroundService.class);
                intent.setAction("com.example.shouhutest.alarmLoop");
            }
//            intent.addFlags(0x01000000);
//            intent.setPackage(context.getPackageName());
            sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, sender);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, sender);
            }

        }else{
            Log.e("AlarmMangerUtils", "setPendingBroadCast: Context is null!!!!!!!");
        }
        return this;
    }


/*    public AlarmMangerUtils setPendingService() {
        PendingIntent pendingIntent;
        Intent intent = new Intent(this.context, ForegroundService.class);
//        pendingIntent.setFlags(Intent.FLAG_UPDATE_CURRENT);
        intent.putExtra("id", "");
        if (Build.VERSION.SDK_INT >= 26) {
            pendingIntent = PendingIntent.getForegroundService(this.context, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getService(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            if (Build.VERSION.SDK_INT >= 23) {
                Log.e("AlarmMangerUtils", "屏幕暗了");
                this.alarmManager.setExactAndAllowWhileIdle(AlarmManager., System.currentTimeMillis() + 180000L, pendingIntent);
                return this;
            }
            this.alarmManager.setExact(0, System.currentTimeMillis() + 180000L, pendingIntent);
            return this;
        }
        if (this.flag == 0) {
            this.alarmManager.set(0, System.currentTimeMillis() + 180000L, pendingIntent);
            return this;
        }
        this.alarmManager.setRepeating(0, System.currentTimeMillis(), 180000L, pendingIntent);
        return this;
    }*/



    /*
     * 判断服务是否启动,context上下文对象 ，className服务的name
     */
    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }
        Log.e("OnlineService：",className);
        for (int i = 0; i < serviceList.size(); i++) {
            Log.e("serviceName：",serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().contains(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

}
