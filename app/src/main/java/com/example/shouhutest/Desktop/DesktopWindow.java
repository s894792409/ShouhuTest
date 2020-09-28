package com.example.shouhutest.Desktop;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.shouhutest.MyApplication;
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
//        ViewGroup pointGroup = LayoutInflater.from(this).inflate(R.id.point_group,null);
        initAppList();
    }



    private void initAppList() {
        appInfos= AppListUtil.GetAppList(context);
        DeskTopGridViewBaseAdapter deskTopGridViewBaseAdapter=new DeskTopGridViewBaseAdapter(appInfos,context);
        mGridView.setAdapter(deskTopGridViewBaseAdapter);

        mGridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(appInfos.get(position).getPackageName());
            if (intent != null) {
                intent.putExtra("type", "110");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                DismissDesktopWindow();
            }

        });
    }


    public View getContentView() {
        return ContentView;
    }



    public void showDesktopWindow(Context context){
        if (isShowing) return;
        Log.e(TAG, "showDesktopWindow: ");

        ContentView = LayoutInflater.from(context).inflate(R.layout.activity_desktop,null);
        mGridView=ContentView.findViewById(R.id.mgv);
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
        windowManager.addView(ContentView, layoutParams);
        isShowing=true;
    }


    public void DismissDesktopWindow(){
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