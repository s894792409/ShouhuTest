package com.example.shouhutest.Account;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class SyncService extends Service {

    private static final String TAG = "SyncService";
    private SyncAdapter syncAdapter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        syncAdapter = new SyncAdapter(getApplicationContext(),true);
    }

    public static class SyncAdapter extends AbstractThreadedSyncAdapter {
        public SyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            Log.e(TAG, "onPerformSync: 同步账户");
        }
    }
}
