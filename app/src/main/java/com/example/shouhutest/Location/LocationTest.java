package com.example.shouhutest.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shouhutest.DialogConfirm;
import com.example.shouhutest.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class LocationTest extends AppCompatActivity {
    TextView tvMapInfo;
    List<Address> addresses;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_test);



        tvMapInfo = findViewById(R.id.tv);
        //检查是否开启权限！
        //检查是否开启权限！
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "权限不够", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            return;
        }

        SmartLocation.with(this).location().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                Log.e("TAG", "onLocationUpdated: Location:"+location.toString());
                decodeLocation(location);
            }
        });







//获取一个地址管理者，获取的方法比较特殊，不是直接new出来的
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

//使用GPS获取上一次的地址，这样获取到的信息需要多次，才能够显示出来，所以后面有动态的判断
        location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
//判断是否用户打开了GPS开关，这个和获取权限没关系
        GPSisopen(locationManager);
//显示信息，可以根据自己的传入对应的location！！！
        upLoadInfor(location);

//获取时时更新，第一个是Provider,第二个参数是更新时间1000ms，第三个参数是更新半径，第四个是监听器
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 8, new LocationListener() {

            @Override
            /*当地理位置发生改变的时候调用*/
            public void onLocationChanged(Location location) {

                upLoadInfor(location);//实时的显示信息

            }

            /* 当状态发生改变的时候调用*/
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("GPS_SERVICES", "状态信息发生改变");

            }

            /*当定位者启用的时候调用*/
            @Override
            public void onProviderEnabled(String s) {
                Log.d("TAG", "onProviderEnabled: ");

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("TAG", "onProviderDisabled: ");
            }
        });




    }

    private void decodeLocation(Location location) {

        //采取直接用匿名类的方法，构造了一个线程，但是在子线程中不能直接修改主线程的内容，否则会报错，但是！！！，当我用Android8.0模拟器测试的时候没有崩，当用Android7.0测试的时候直接崩溃，所以还是老老实实通过handler来解决这个问题！
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("Run", "A new Thread");
                try {
                    addresses = getAddress(location);
                    if (addresses != null) {
                        for (Address address : addresses) {
                            Log.e("run: ", address.toString());
                            Message message = new Message();
                            message.what = 1;//信息内容
                            handler.sendMessage(message);//发送信息
                        }
                    }
                } catch (Exception e) {
                    Log.e("Exception", "ERRPOR");
                    e.printStackTrace();
                }
            }

        }).start();


    }


    private void upLoadInfor(Location location) {
        if (location!=null) {
            Log.e("TAG", "upLoadInfor: Location:" + location.toString());
            decodeLocation(location);
        }else
            Log.e("TAG", "upLoadInfor: Location==null!!!");
    }

    //判断是否用户打开GPS开关，并作指导性操作！
    private void GPSisopen(LocationManager locationManager) {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请打开GPS", Toast.LENGTH_SHORT);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("请打开GPS连接");
            dialog.setMessage("为了获取定位服务，请先打开GPS");
            dialog.setPositiveButton("设置", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //界面跳转
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 0);
                }
            });
            dialog.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            //调用显示方法！
            dialog.show();
        }
    }
//同时获取到的只是location如果想根据location获取具体地址，可以通过Android提供的API获取具体的地点！

    //传进来一个location返回一个Address列表，这个是耗时的操作所以需要在子线程中进行！！！
//传进来一个location返回一个Address列表，这个是耗时的操作所以需要在子线程中进行！！！
//传进来一个location返回一个Address列表，这个是耗时的操作所以需要在子线程中进行！！！
    private List<Address> getAddress(Location location) {
        List<Address> result = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    //主线程中处理函数
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tvMapInfo.setText(tvMapInfo.getText() + "\n" + addresses.toString());
                    break;
                default:
                    break;
            }
        }
    };






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> refusedList = new ArrayList<>();
        /* 如果没有同意授权，将未授权的权限加入list，然后提示需要授权后使用 */
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                refusedList.add(permissions[i]);
            }
        }
        if (requestCode == 1) {
            if (refusedList.size() <= 0) {
                //Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                //调用autoCheckIn接口完成自动签到
//                getLocation();
            } else {
                showPermissionsDialog(refusedList);
            }

        } else {
//            getLocation();
        }
    }




    /**
     * 提示同意权限
     */
    private void showPermissionsDialog(final List<String> refusedList) {
        DialogConfirm.show(this, "权限操作提示", "您拒绝了录音转文字专家的权限申请，会导致后续操作无法进行。请在授权页面同意授予权限的申请！", "确定", "取消", () -> {
            String[] permissions = new String[refusedList.size()];
            refusedList.toArray(permissions);
            ActivityCompat.requestPermissions((Activity) LocationTest.this, permissions, 1);
        }, () -> {
            System.exit(0);
        });
    }


}