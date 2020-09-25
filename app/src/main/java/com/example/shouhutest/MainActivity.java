package com.example.shouhutest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import com.example.shouhutest.Accessibility.AccessibilityNormalSample;
import com.example.shouhutest.Accessibility.AccessibilityOpenHelperActivity;
import com.example.shouhutest.Accessibility.AccessibilityOperator;
import com.example.shouhutest.Accessibility.OpenAccessibilitySettingHelper;
import com.example.shouhutest.Account.AccountHelper;
import com.example.shouhutest.Account.AuthenticationService;
import com.example.shouhutest.Desktop.Desktop;
import com.example.shouhutest.KeepActive.ForegroundService;
import com.example.shouhutest.KeepActive.KeepManager;
import com.example.shouhutest.Location.LocationTest;
import com.example.shouhutest.Location.LocationTest2;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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



        // 安卓8.0以上需要申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }



        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);

//        HomeKeyEventBroadCastReceiver receiver = new HomeKeyEventBroadCastReceiver();
//        registerReceiver(receiver, new IntentFilter(
//                Intent. ACTION_CLOSE_SYSTEM_DIALOGS));

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.e(TAG, "onUserLeaveHint: ");
    }
}