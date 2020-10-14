package com.example.shouhutest.Desktop;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.shouhutest.Accessibility.AccessibilityOperator;
import com.example.shouhutest.R;
import com.example.shouhutest.Util.AppListUtil;

import java.util.List;

public class DesktopWindow {

    private View ContentView;
    private List<AppInfo> appInfos;
    private GridView mGridView;
    public Context context;
    public boolean isShowing=false;
    private WindowManager windowManager;
    private String TAG = "DesktopWindow";
    private boolean launcherFlag=false;
    private String launcherPackageName = "";

    private View progressView;
    private boolean progressViewIsShowing=false;

    public static final int ON_WINDOW_CHANGE_FLAG = 1;
    public static final int CLOSE_PROGRESSBAR_WINDOW = 2;
    public Handler handler = new Handler(msg -> {
        switch (msg.what){
            case ON_WINDOW_CHANGE_FLAG:
                Bundle bundle = (Bundle) msg.obj;
                Log.e(TAG, "Handler ON_WINDOW_CHANGE_FLAG: "+bundle.toString());
                String packageName = bundle.getString("PackageName").toLowerCase();
                String className = bundle.getString("ClassName").toLowerCase();
                Log.e(TAG, "launcherFlag: "+launcherFlag);

                if (isShowing){
                    if (packageName.equals(launcherPackageName)) {
                        DismissDesktopWindow();
                        DismissProgressBar();
                    }
                }else {

                    if (AppListUtil.isSystemApp(packageName,context) && (packageName.contains("launcher") || packageName.contains("systemui") || className.contains("launcher") || packageName.contains("recent") || className.contains("recent"))) {
                        showDesktopWindow();
//                        KeepReceiver.jumpToDesktop(context);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        AccessibilityOperator.getInstance().clickBackKey();
                    }
                   /* if (packageName.contains("recent") || className.contains("recent"))
                        AccessibilityOperator.getInstance().clickBackKey();*/
                }
                    break;
            case CLOSE_PROGRESSBAR_WINDOW:
                DismissProgressBar();
                break;
        }
        return false;
    });


    public DesktopWindow(Context context) {
        this.context = context;

        Log.e(TAG, "DesktopWindow: ");
        ContentView = LayoutInflater.from(context).inflate(R.layout.activity_desktop,null);
        mGridView=ContentView.findViewById(R.id.mgv);
        //设置背景
        WallpaperManager manager =WallpaperManager.getInstance(context);
        Drawable drawable=manager.getDrawable();
        LinearLayout linearLayout = ContentView.findViewById(R.id.desktop_linear);
        linearLayout.setBackground(drawable);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initAppList();

        ContentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DismissDesktopWindow();
                return true;
            }
        });
    }



    private void initAppList() {
        appInfos= AppListUtil.GetAppList(context);
        DeskTopGridViewBaseAdapter deskTopGridViewBaseAdapter=new DeskTopGridViewBaseAdapter(appInfos,context);
        mGridView.setAdapter(deskTopGridViewBaseAdapter);

        mGridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(appInfos.get(position).getPackageName());
            if (intent != null) {
                try {
                    intent.putExtra("type", "110");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launcherFlag = true;
                    launcherPackageName = appInfos.get(position).getPackageName();
                    showProgressBar();
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    private void showProgressBar() {
        if (progressView==null)
            progressView = LayoutInflater.from(context).inflate(R.layout.progress_dialog,null);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        // 设置宽高
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        // 设置背景透明
         layoutParams.format = PixelFormat.TRANSPARENT;
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
        windowManager.addView(progressView, layoutParams);

    /*    new Thread(()->{
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
        }).start();*/
        handler.postDelayed(()->{
            Log.e(TAG, "showProgressBar: 检查进度弹窗是否被关闭："+progressViewIsShowing);
            if (progressViewIsShowing){
                DismissProgressBar();
                Toast.makeText(context,"应用启动失败！",Toast.LENGTH_SHORT).show();
            }
        },2000);
       progressViewIsShowing=true;
        Log.e(TAG, "showProgressBar: ");
    }



    public void DismissProgressBar(){
        if (!progressViewIsShowing) return;

        windowManager.removeViewImmediate(progressView);
        launcherFlag = false;
        progressViewIsShowing = false;
    }


    public View getContentView() {
        return ContentView;
    }



    public void showDesktopWindow(){
        if (isShowing) return;
        Log.e(TAG, "showDesktopWindow: ");

//        ContentView = LayoutInflater.from(context).inflate(R.layout.activity_desktop,null);
        mGridView=ContentView.findViewById(R.id.mgv);
        //设置背景
        WallpaperManager manager =WallpaperManager.getInstance(context);
        Drawable drawable=manager.getDrawable();
        LinearLayout linearLayout = ContentView.findViewById(R.id.desktop_linear);
        linearLayout.setBackground(drawable);

//        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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
//            layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;

            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        // 设置布局弹出的动画
        //            layoutParams.windowAnimations = R.style.anim;
        windowManager.addView(ContentView, layoutParams);
        isShowing=true;
    }


    public void DismissDesktopWindow(){
        if (!isShowing) return;
        Log.e(TAG, "DismissDesktopWindow: ");
        windowManager.removeView(ContentView);
        isShowing = false;
    }


    public void UpdateWindows(WindowManager.LayoutParams layoutParams){
        windowManager.updateViewLayout(ContentView,layoutParams);
    }

    public boolean isShowing() {
        return isShowing;
    }
}