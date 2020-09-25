package com.example.shouhutest.KeepActive;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.shouhutest.Accessibility.AccessibilityOperator;
import com.example.shouhutest.Account.AccountHelper;
import com.example.shouhutest.Account.AuthenticationService;
import com.example.shouhutest.AppList;
import com.example.shouhutest.Desktop.Desktop;
import com.example.shouhutest.Location.LocationTest;
import com.example.shouhutest.Location.LocationTest2;
import com.example.shouhutest.MainActivity;
import com.example.shouhutest.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class KeepReceiver extends BroadcastReceiver {
    String TAG = "KeepReceiver";

    private final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            Log.e(TAG, "onReceive: action:"+action );

            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

    /*    List<ActivityManager.RunningAppProcessInfo> amList = activityManager.getRunningAppProcesses();
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfoList){
            if (runningTaskInfo.baseIntent.getPackage().equals(context.getPackageName()))
        }*/

      /*  List<ActivityManager.AppTask> list = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : list){
            Intent s = appTask.getTaskInfo().baseIntent;
            if (s.getPackage().equals(context.getPackageName())){

                activityManager.moveTaskToFront(appTask.getTaskInfo().taskId, ActivityManager.MOVE_TASK_WITH_HOME);
            }
        }*/

            if (action.equals(Intent.ACTION_SCREEN_OFF)){
                Log.e(TAG, "onReceive: 关闭屏幕");
                KeepManager.getInstance().startKeep(context);
            }else if (action.equals(Intent.ACTION_SCREEN_ON)){
                Log.e(TAG, "onReceive: 开启屏幕");
                KeepManager.getInstance().finishKeep();
            } else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                Log.e(TAG, "onReceive: reason:"+reason );
                if (reason == null)
                    return;

                // Home键
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    Log.e(TAG, "onReceive: 按下home键");
                    Toast.makeText(context, "按了Home键", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, Desktop.class));
    //                addWindow(context);
    //                context.startActivity(new Intent(context, MainActivity.class));
                }

                if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {// 最近任务列表键
                    Log.e(TAG, "onReceive: 按下最近任务列表");
                    Toast.makeText(context, "按了最近任务列表", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, Desktop.class));
                    //sendKeyCode1(KeyEvent.KEYCODE_BACK);
                   /* new Thread(()->{
                        // 可以不用在 Activity 中增加任何处理，各 Activity 都可以响应
                        Instrumentation inst = new Instrumentation();
                        inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
                                MotionEvent.ACTION_DOWN, 200, 200, 0));
                        inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
                                MotionEvent.ACTION_UP, 200, 200, 0));
                    }).start();*/

                 //  addWindow(context);
                    Log.e(TAG, "onReceive: isServiceRunning:"+AccessibilityOperator.getInstance().isServiceRunning() );
                  //  AccessibilityOperator.getInstance().clickBackKey();

                }

            }else if (action.equals("vivo.intent.action.UPSLIDE_PANEL_STATE_CHANGED")){
                //Log.e(TAG, "onReceive: vivo.intent.action.UPSLIDE_PANEL_STATE_CHANGED");
                /*Bundle bundle = intent.getExtras();
                Set<String> keyset = bundle.keySet();
                for (String key : keyset){
                    Log.e(TAG, "onReceive: Key:"+key+" Value:"+ bundle.get(key));
                }*/
                if (intent.getExtras().getString("panel_state").equals("starting_expand")){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    AccessibilityOperator.getInstance().clickBackKey();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 用Runtime模拟按键操作
     *
     * @param keyCode 按键事件(KeyEvent)的按键值
     */
    private void sendKeyCode1(int keyCode) {
        try {
            String keyCommand = "input keyevent " + keyCode;
            // 调用Runtime模拟按键操作
            Runtime.getRuntime().exec(keyCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //添加悬浮窗
    public void addWindow(Context context){

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        // 设置宽高
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        // 设置背景透明
        // layoutParams.format = PixelFormat.TRANSPARENT;
        // 设置屏幕左上角为起始点
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        // FLAG_LAYOUT_IN_SCREEN：将window放置在整个屏幕之内,无视其他的装饰(比如状态栏)； FLAG_NOT_TOUCH_MODAL：不阻塞事件传递到后面的窗口
        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            // 设置窗体显示类型(TYPE_TOAST:与toast一个级别)
            layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        // 设置布局弹出的动画
        //            layoutParams.windowAnimations = R.style.anim;
        // 添加视图
        View contentView = LayoutInflater.from(context).inflate(R.layout.activity_main, null);


        Button getAppListBtn = contentView.findViewById(R.id.get_app_list);
        getAppListBtn.setOnClickListener(v->context.startActivity(new Intent(context, AppList.class)));
        Button locTest1Btn = contentView.findViewById(R.id.location_test);
        locTest1Btn.setOnClickListener(v->context.startActivity(new Intent(context, LocationTest.class)));
        Button locTest2Btn = contentView.findViewById(R.id.location_test2);
        locTest2Btn.setOnClickListener(v->context.startActivity(new Intent(context, LocationTest2.class)));
        Button foregroundServiceBtn = contentView.findViewById(R.id.foreground_service);
        foregroundServiceBtn.setOnClickListener(v->{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //android8.0以上通过startForegroundService启动service
                context.startForegroundService(new Intent(context, ForegroundService.class));
            }else{
                context.startService(new Intent(context, ForegroundService.class));
            }
        });

  /*      Button addAccountBtn = findViewById(R.id.add_account);
        addAccountBtn.setOnClickListener(v->{
            startService(new Intent(this, AuthenticationService.class));
            AccountHelper.addAccount(this);
            AccountHelper.autoSync();
        });

        Button toSettingBtn = findViewById(R.id.goto_setting);
        toSettingBtn.setOnClickListener(v->{
            Intent intent =  new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
            startActivity(intent);
        });*/

        windowManager.addView(contentView, layoutParams);

    }
}
