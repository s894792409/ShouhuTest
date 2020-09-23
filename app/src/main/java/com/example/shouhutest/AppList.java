package com.example.shouhutest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AppList extends AppCompatActivity {
    List<AppInfo> appList = new ArrayList<>(50);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        ListView appListView = findViewById(R.id.list_view);

        new Thread(()->{
            getAppList();
            AppListAdapter adapter = new AppListAdapter(this,appList);
            runOnUiThread(()->{
                Toast.makeText(this,"App数量："+appList.size(),Toast.LENGTH_SHORT).show();
                appListView.setAdapter(adapter);
            });
        }).start();
    }

    private void getAppList() {
        PackageManager pm = getPackageManager();
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        Log.e("TAG", "getAppList: packagesCount:"+packages.size() );

        for (PackageInfo packageInfo : packages) {

            // 判断是否为非系统应用
//            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){} // 非系统应用

                AppInfo tmpInfo = new AppInfo();

                tmpInfo.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                tmpInfo.packageName = packageInfo.packageName;

                String appName = tmpInfo.appName.toLowerCase();
            if ((appName.startsWith("com.") || appName.contains("service")) || tmpInfo.packageName.startsWith("com.android.")){ // 过滤服务和插件
                continue;
            }
                tmpInfo.versionName = packageInfo.versionName;
                tmpInfo.versionCode = packageInfo.versionCode;
                tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());

                appList.add(tmpInfo);

        }
    }
}