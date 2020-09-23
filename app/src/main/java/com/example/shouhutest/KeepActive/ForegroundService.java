package com.example.shouhutest.KeepActive;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.shouhutest.MainActivity;
import com.example.shouhutest.R;
import com.google.android.gms.common.api.Api;

public class ForegroundService extends Service {

    public ForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "ForegroundService onCreate: ");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TAG", "ForegroundService onStartCommand: ");
        String channelID = "shouhuTest";
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
            NotificationChannel channel = new NotificationChannel(channelID,"守护进程测试", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelID);
        }
        Notification notification = builder.build(); // 获取构建好的Notification
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(1,notification);

        new Thread(()->{
            while (true){
                Log.e("TAG", "onStartCommand: Thread!!!");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("TAG", "ForegroundService onBind: ");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "ForegroundService onDestroy: ");
    }
}
