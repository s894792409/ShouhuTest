package com.example.shouhutest.KeepActive;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.shouhutest.Accessibility.AccessibilityOperator;
import com.example.shouhutest.AppList;
import com.example.shouhutest.Desktop.Desktop;
import com.example.shouhutest.Desktop.DesktopWindow;
import com.example.shouhutest.Location.LocationTest;
import com.example.shouhutest.Location.LocationTest2;
import com.example.shouhutest.MyApplication;
import com.example.shouhutest.R;

import java.util.List;

public class KeepReceiver extends BroadcastReceiver {
    String TAG = "KeepReceiver";

    private final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            Log.e(TAG, "onReceive: action:"+action );

            if (action.equals(Intent.ACTION_SCREEN_OFF)){
                Log.e(TAG, "onReceive: 关闭屏幕");
                KeepManager.getInstance().startKeep(context);
            }else if (action.equals(Intent.ACTION_SCREEN_ON)){
                Log.e(TAG, "onReceive: 开启屏幕");
                KeepManager.getInstance().finishKeep();
            } else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {

//                jumpToDesktop(context);
                if (MyApplication.desktopWindow == null) {
                    MyApplication.desktopWindow = new DesktopWindow(context);
                    Log.e(TAG, "onReceive: 实例化DesktopWindow");
                }
                else if (MyApplication.desktopWindow.context != context){
                    if (MyApplication.desktopWindow.isShowing()) MyApplication.desktopWindow.DismissDesktopWindow();
                    MyApplication.desktopWindow = new DesktopWindow(context);
                    Log.e(TAG, "onReceive: Context不一样");
                }

                if (!MyApplication.desktopWindow.isShowing) MyApplication.desktopWindow.showDesktopWindow();

//                showDesktopWindow(context);

//                addDesktopWindow(context);
//                ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
               // jumpToDesktop(context);
//                Intent intent2 =  new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
//                context.startActivity(intent2);

               /* String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                Log.e(TAG, "onReceive: reason:"+reason );
                if (reason == null)
                    return;

                // Home键
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    Log.e(TAG, "onReceive: 按下home键");
                    Toast.makeText(context, "按了Home键", Toast.LENGTH_SHORT).show();
                    jumpToDesktop(context);

                }

                if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {// 最近任务列表键
                    Log.e(TAG, "onReceive: 按下最近任务列表");
                    Toast.makeText(context, "按了最近任务列表", Toast.LENGTH_SHORT).show();
                    jumpToDesktop(context);
                }*/

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

            }else{
                Log.e(TAG, "onReceive: 未知广播："+intent.getAction() );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void jumpToDesktop(Context context){
        Intent resultIntent = new Intent(context, Desktop.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.addCategory(Intent.CATEGORY_HOME);
        try {
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,resultIntent,0);
            pendingIntent.send();
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(resultIntent);
        }
    }



    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
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

    //添加悬浮窗版桌面
    public void addDesktopWindow(Context context){

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
        if (MyApplication.desktopWindow == null)
            windowManager.addView(MyApplication.desktopWindow.getContentView(), layoutParams);

    }



    public void showDesktopWindow(Context context){
        Log.e(TAG, "showDesktopWindow: ");

        View ContentView = LayoutInflater.from(context).inflate(R.layout.activity_desktop,null);
        GridView mGridView= ContentView.findViewById(R.id.mgv);
        //设置背景
        WallpaperManager manager =WallpaperManager.getInstance(context);
        Drawable drawable=manager.getDrawable();
        LinearLayout linearLayout = ContentView.findViewById(R.id.desktop_linear);
        linearLayout.setBackground(drawable);

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
        //if (MyApplication.desktopWindow == null)
            windowManager.addView(ContentView, layoutParams);
    }

}
