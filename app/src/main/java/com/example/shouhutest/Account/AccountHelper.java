package com.example.shouhutest.Account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class AccountHelper {
    public static final String AccountType = "ShouhuAccount";
    public  static final String TAG = "AccountHelper";


    /**
     * 添加账号
     */
    public static void addAccount(Context context){
        Log.e(TAG, "addAccount: 添加账户");
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        //获得此类型的账户
        Account[] accounts = accountManager.getAccountsByType(AccountType);

        if (accounts.length>0){
            Log.e(TAG, "addAccount: 账户已存在！");
            return;
        }

        Account account = new Account("enjoy",AccountType);
        // 给这个账户类型添加一个账户
        accountManager.addAccountExplicitly(account,"psw",new Bundle());

    }


    /**
     * 设置账户同步
     */
    public static void autoSync(){
        Log.e(TAG, "autoSync: 设置账户同步");
        Account account = new Account("enjoy",AccountType);
        //下面三个都需要同一个权限

        //设置同步
        ContentResolver.setIsSyncable(account,"com.example.shouhutest.provider",1);

        //自动同步
        ContentResolver.setSyncAutomatically(account,"com.example.shouhutest.provider",true);

        //设置同步周期
        ContentResolver.addPeriodicSync(account,"com.example.shouhutest.provider",new Bundle(),1);
    }
}
