package com.example.shouhutest;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.shouhutest.Accessibility.AccessibilityNormalSample;
import com.example.shouhutest.Accessibility.AccessibilityOperator;
import com.example.shouhutest.Accessibility.OpenAccessibilitySettingHelper;
import com.example.shouhutest.Account.AccountHelper;
import com.example.shouhutest.Account.AuthenticationService;
import com.example.shouhutest.Desktop.Desktop;
import com.example.shouhutest.Desktop.DesktopWindow;
import com.example.shouhutest.KeepActive.AlarmMangerUtils;
import com.example.shouhutest.KeepActive.ForegroundService;
import com.example.shouhutest.KeepActive.KeepManager;
import com.example.shouhutest.KeepActive.LoopBroadCast;
import com.example.shouhutest.Location.LocationTest;
import com.example.shouhutest.Location.LocationTest2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate: SHA1:"+sHA1(this));
        Button getAppListBtn = findViewById(R.id.get_app_list);
        getAppListBtn.setOnClickListener(v->startActivity(new Intent(MainActivity.this,AppList.class)));
        Button locTest1Btn = findViewById(R.id.location_test);
        locTest1Btn.setOnClickListener(v->startActivity(new Intent(MainActivity.this, LocationTest.class)));
        Button locTest2Btn = findViewById(R.id.location_test2);
        locTest2Btn.setOnClickListener(v->startActivity(new Intent(MainActivity.this, LocationTest2.class)));
        Button foregroundServiceBtn = findViewById(R.id.foreground_service);
        foregroundServiceBtn.setOnClickListener(v->{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //android8.0以上通过startForegroundService启动service
                startForegroundService(new Intent(MainActivity.this, ForegroundService.class));
            }else{
                startService(new Intent(MainActivity.this, ForegroundService.class));
            }

            AlarmMangerUtils.getInstance().getAlarmManager(this).setPendingBroadCast();
        });

        Button addAccountBtn = findViewById(R.id.add_account);
        addAccountBtn.setOnClickListener(v->{
            startService(new Intent(this,AuthenticationService.class));
            AccountHelper.addAccount(this);
            AccountHelper.autoSync();
        });

        Button toSettingBtn = findViewById(R.id.goto_setting);
        toSettingBtn.setOnClickListener(v->{
            /*Intent intent =  new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
            startActivity(intent);*/

            Intent intent =  new Intent(MainActivity.this, AccessibilityNormalSample.class);
            startActivity(intent);
        });

        Button toMyDesktop = findViewById(R.id.goto_my_desktop);
        toMyDesktop.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, Desktop.class));
        });

        Button toAccessibilityBtn = findViewById(R.id.goto_accessibility);
        toAccessibilityBtn.setOnClickListener(v-> OpenAccessibilitySettingHelper.jumpToSettingPage(MainActivity.this));

        AccessibilityOperator.getInstance().init(this);
        KeepManager.getInstance().registerKeep(this);



        // 1安卓8.0以上需要申请显示在应用上层权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "onCreate: 显示在应用上层权限："+Settings.canDrawOverlays(this));
        }

        //2 检查并开启悬浮窗权限
        if(!checkFloatPermission(this)){
            Toast.makeText(this,"未开启悬浮窗权限！",Toast.LENGTH_SHORT).show();
            requestFloatPermission(this, 1);
        }
        Log.e(TAG, "onCreate: 悬浮窗权限："+checkFloatPermission(this));




        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);

        MyApplication.desktopWindow=new DesktopWindow(this);
//        HomeKeyEventBroadCastReceiver receiver = new HomeKeyEventBroadCastReceiver();
//        registerReceiver(receiver, new IntentFilter(
//                Intent. ACTION_CLOSE_SYSTEM_DIALOGS));

    }



    /***
     * 检查悬浮窗开启权限
     * @param context
     * @return
     */
    public boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return Settings.canDrawOverlays(context) || mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }

    /**
     * 悬浮窗开启权限
     * @param context
     * @param requestCode
     */
    public void requestFloatPermission(Activity context, int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivityForResult(intent, requestCode);
    }


    PendingIntent sender;
    int INTERVAL = 10000;
    int flag = 1;

    public void startAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent();
            //定义广播类型
            intent.setAction("com.bgyapp.dingweix0zhuszt.alarmLoop");
            intent.setComponent(new ComponentName(getPackageName(), "com.bgyapp.dingweix0zhuszt.alarm.LoopBroadCast"));
            /* intent.setComponent(new ComponentName("com.examplehq.forhelp","com.examplehq.forhelp.MyReceiver"));*/
        } else {
            intent = new Intent(this, LoopBroadCast.class);
            intent.setAction("com.bgyapp.dingweix0zhuszt.alarmLoop");
        }
        sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, sender);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, sender);
            }
        } else {
            if (flag == 0) {    //一次性闹钟
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, sender);
            } else {            //重复闹钟
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), INTERVAL, sender);
            }
        }

    }


    public static String sHA1(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


}