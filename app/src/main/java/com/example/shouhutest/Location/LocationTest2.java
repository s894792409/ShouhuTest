package com.example.shouhutest.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.shouhutest.DialogConfirm;
import com.example.shouhutest.R;

import java.util.ArrayList;
import java.util.List;

public class LocationTest2 extends AppCompatActivity {
    Location mLocation;
    LocationManager mLocationManager;
    long MIN_TIME = 1000 * 60;
    float MIN_DISTANCE = 0.5f;


    //网络定位监听器
    LocationListener networkListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.e("TAG", "networkListener onLocationChanged: Location:" + location.toString());
            if (isBetterLocation(location, mLocation)) {
                mLocation = location;
            }
            if (mLocation != null) {
                mLocationManager.removeUpdates(this);
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("TAG", "networkListener onStatusChanged: provider:" + provider + " status:" + status + " extras:" + extras.toString());
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("TAG", "networkListener onProviderEnabled: provider:" + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("TAG", "networkListener onProviderDisabled: provider:" + provider);
        }

    };


    //GPS定位监听器
    LocationListener gpsLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.e("TAG", "gpsLocationListener onLocationChanged: location:" + location.toString());
            if (isBetterLocation(location, mLocation)) {
                mLocationManager.removeUpdates(networkListener);
                mLocation = location;
            }
            if (mLocation != null) {
                mLocationManager.removeUpdates(this);

            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("TAG", "gpsLocationListener onProviderDisabled: provider:" + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("TAG", "gpsLocationListener onProviderEnabled: provider:" + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("TAG", "gpsLocationListener onStatusChanged: provider:" + provider + " status:" + status + " extras:" + extras.toString());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_test2);


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

        getLocation();

    }


    public void getLocation() {

        Log.e("TAG", "getLocation: " );
        if (mLocationManager==null)
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
//        criteria.setAltitudeRequired(false);//无海拔要求   criteria.setBearingRequired(false);//无方位要求
//        criteria.setCostAllowed(true);//允许产生资费   criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
//
//        // 获取最佳服务对象
//        String provider = locationManager.getBestProvider(criteria,true);
//        locationManager.getLastKnownLocation(provider);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, gpsLocationListener);
//        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, networkListener);
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

    }




    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**确定某个位置读数是否比当前位置修正更好
     * @param location 您要评估的新位置
     * @param currentBestLocation 您要与新位置比较的当前位置修复程序
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


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
                getLocation();
            } else {
                showPermissionsDialog(refusedList);
            }

        } else {
            getLocation();
        }
    }




    /**
     * 提示同意权限
     */
    private void showPermissionsDialog(final List<String> refusedList) {
        DialogConfirm.show(this, "权限操作提示", "您拒绝了录音转文字专家的权限申请，会导致后续操作无法进行。请在授权页面同意授予权限的申请！", "确定", "取消", () -> {
            String[] permissions = new String[refusedList.size()];
            refusedList.toArray(permissions);
            ActivityCompat.requestPermissions((Activity) LocationTest2.this, permissions, 1);
        }, () -> {
            System.exit(0);
        });
    }

}