package com.example.shouhutest.Desktop;

public class DesktopManager {

    //桌面启动类型 1 Activity 2 悬浮窗
    private int LauncherType = 1;
    private static final String TAG = "DesktopManager";
    public static final int ON_WINDOW_CHANGE_FLAG = 1;
    public static final int CLOSE_PROGRESSBAR_WINDOW = 2;
/*

    public Handler handler = new Handler(msg -> {
        switch (msg.what){
            case ON_WINDOW_CHANGE_FLAG:
                Bundle bundle = (Bundle) msg.obj;
                Log.e(TAG, "Handler ON_WINDOW_CHANGE_FLAG: "+bundle.toString());
                String packageName = bundle.getString("PackageName").toLowerCase();
                String className = bundle.getString("ClassName").toLowerCase();


                if (MyApplication.desktopWindow.isShowing){
                    if (packageName.equals(MyApplication.desktopWindow.launcherPackageName)) {
                        MyApplication.desktopWindow.DismissDesktopWindow();
                        MyApplication.desktopWindow.DismissProgressBar();
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
                   *//* if (packageName.contains("recent") || className.contains("recent"))
                        AccessibilityOperator.getInstance().clickBackKey();*//*
                }
                break;
            case CLOSE_PROGRESSBAR_WINDOW:
                DismissProgressBar();
                break;
        }
        return false;
    });*/
}
