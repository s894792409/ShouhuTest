package com.example.shouhutest.Desktop;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shouhutest.R;

import java.util.ArrayList;
import java.util.List;

public class Desktop extends AppCompatActivity {

    List<AppInfo> appInfos;
    GridView mGridView;
    public static int TaskID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题拦
        setContentView(R.layout.activity_desktop);

        mGridView=findViewById(R.id.mgv);
        //设置背景
        WallpaperManager manager =WallpaperManager.getInstance(this);
        Drawable drawable=manager.getDrawable();
        LinearLayout linearLayout = findViewById(R.id.desktop_linear);
        linearLayout.setBackground(drawable);

//        ViewGroup pointGroup = LayoutInflater.from(this).inflate(R.id.point_group,null);
        initAppList();
    }


    @Override
    protected void onResume() {
        super.onResume();
        TaskID=getTaskId();
        Log.e("Desktop", "onResume: TaskID:"+TaskID);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK || keyCode==KeyEvent.KEYCODE_MENU || keyCode==KeyEvent.KEYCODE_HOME){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initAppList() {
        appInfos=GetAppList1(this);
        DeskTopGridViewBaseAdapter deskTopGridViewBaseAdapter=new DeskTopGridViewBaseAdapter(appInfos,this);
        mGridView.setAdapter(deskTopGridViewBaseAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent=appInfos.get(position).getIntent();
//                startActivity(intent);

                Intent intent = getPackageManager().getLaunchIntentForPackage(appInfos.get(position).getPackageName());
                if (intent != null) {
                    intent.putExtra("type", "110");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        });
    }




    public static List<AppInfo> GetAppList1(Context context){
        List<AppInfo> list=new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities   = pm.queryIntentActivities(mainIntent, 0);
        for(ResolveInfo info : activities){
            String packName = info.activityInfo.packageName;
            if(packName.equals(context.getPackageName())){
                continue;
            }
            AppInfo mInfo = new AppInfo();
            mInfo.setIco(info.activityInfo.applicationInfo.loadIcon(pm));
            mInfo.setName(info.activityInfo.applicationInfo.loadLabel(pm).toString());
            mInfo.setPackageName(packName);
            // 为应用程序的启动Activity 准备Intent
            Intent launchIntent = new Intent();
            launchIntent.setComponent(new ComponentName(packName,
                    info.activityInfo.name));
            mInfo.setIntent(launchIntent);
            list.add(mInfo);
        }
        return list;
    }

}