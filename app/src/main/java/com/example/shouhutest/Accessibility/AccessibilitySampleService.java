package com.example.shouhutest.Accessibility;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.example.shouhutest.Desktop.DesktopWindow;
import com.example.shouhutest.MainActivity;
import com.example.shouhutest.MyApplication;
import com.example.shouhutest.R;



/**
 * Created by popfisher on 2017/7/6.
 */
public class AccessibilitySampleService extends AccessibilityService {
    public static final String TAG = "AccessibilityService";
    String channelID = "shouhuTest";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e(TAG, "onServiceConnected: ");
        // 通过代码可以动态配置，但是可配置项少一点
//        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
//        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED
//                | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
//                | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
//                | AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
//        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
//        accessibilityServiceInfo.notificationTimeout = 0;
//        accessibilityServiceInfo.flags = AccessibilityServiceInfo.DEFAULT;
//        setServiceInfo(accessibilityServiceInfo);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        addNotification();
        return START_NOT_STICKY;
    }

    /**
     * 添加通知
     */
    private void addNotification() {
        // 获取通知管理器
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 在API11之后构建Notification的方式
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("前台进程") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("守护前台进程运行中...") // 设置上下文内容
                .setAutoCancel(false)//设置通知不能被取消
                .setOngoing(true)//设置通知常驻通知栏
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        //如果安卓版本大于8.0则需要添加通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID,"辅助功能前台服务", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelID);
        }
        Notification notification = builder.build(); // 获取构建好的Notification
        // 将辅助服务设置为前台服务
        startForeground(0, notification);
        // 显示Notification
        if (mNotificationManager != null) {
            mNotificationManager.notify(0, notification);
        }
    }

     /**
     * 接收辅助服务事件
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return;
        }
        AccessibilityOperator.getInstance().updateEvent(this, event);
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: // 窗口状态改变
                if (event.getPackageName() != null && event.getClassName() != null) {
                    Log.e(TAG, "onAccessibilityEvent: "+event.getPackageName() + "\n" + event.getClassName());
                    if (MyApplication.desktopWindow!=null){
                        Bundle bundle = new Bundle();
                        bundle.putString("PackageName",event.getPackageName().toString());
                        bundle.putString("ClassName",event.getClassName().toString());

                        Message msg = new Message();
                        msg.what = DesktopWindow.ON_WINDOW_CHANGE_FLAG;
                        msg.obj = bundle;
                        MyApplication.desktopWindow.handler.sendMessage(msg);
                    }
                }
                break;
            default:
                break;
        }
    }



    @Override
    public void onInterrupt() {

    }
}
