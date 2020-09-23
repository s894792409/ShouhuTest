package com.example.shouhutest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

class AppListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<AppInfo> appInfoList;

    public AppListAdapter(Context context, List<AppInfo> appInfoList) {
        this.context = context;
        this.appInfoList = appInfoList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return appInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView!=null && convertView.getTag()!=null)
            holder = (ViewHolder) convertView.getTag();
        else {
            convertView = inflater.inflate(R.layout.app_list_item, null);
            holder = new ViewHolder(convertView);
        }

        AppInfo appInfo = appInfoList.get(position);
        holder.appIcon.setImageDrawable(appInfo.appIcon);
        holder.appName.setText(appInfo.appName);
        holder.versionName.setText(appInfo.versionName);
        holder.versionCode.setText(String.valueOf(appInfo.versionCode));
        holder.packageName.setText(appInfo.packageName);

        return convertView;
    }

    public static class ViewHolder {
        public View rootView;
        public ImageView appIcon;
        public TextView appName;
        public Switch enableButton;
        public TextView versionName;
        public TextView versionCode;
        public TextView packageName;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.appIcon = (ImageView) rootView.findViewById(R.id.app_icon);
            this.appName = (TextView) rootView.findViewById(R.id.app_name);
            this.enableButton = (Switch) rootView.findViewById(R.id.enable_button);
            this.versionName = (TextView) rootView.findViewById(R.id.version_name);
            this.versionCode = (TextView) rootView.findViewById(R.id.version_code);
            this.packageName = (TextView) rootView.findViewById(R.id.package_name);
        }

    }
}
