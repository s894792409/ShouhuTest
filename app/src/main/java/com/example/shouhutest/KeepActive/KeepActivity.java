package com.example.shouhutest.KeepActive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.example.shouhutest.R;

public class KeepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep);
        Log.e("TAG", "oKeepActivity nCreate: ");
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width=1;
        params.height=1;
        params.x=0;
        params.y=0;
        window.setAttributes(params);
        KeepManager.getInstance().setKeep(this);
    }
}