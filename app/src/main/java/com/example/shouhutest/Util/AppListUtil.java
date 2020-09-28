package com.example.shouhutest.Util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.example.shouhutest.Desktop.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppListUtil {


    public static List<AppInfo> GetAppList(Context context){
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
