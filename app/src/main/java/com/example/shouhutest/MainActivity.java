package com.example.shouhutest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import com.example.shouhutest.Account.AccountHelper;
import com.example.shouhutest.Account.AuthenticationService;
import com.example.shouhutest.KeepActive.ForegroundService;
import com.example.shouhutest.KeepActive.KeepManager;
import com.example.shouhutest.Location.LocationTest;
import com.example.shouhutest.Location.LocationTest2;

public class MainActivity extends AppCompatActivity {
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

        KeepManager.getInstance().registerKeep(this);

    }



}