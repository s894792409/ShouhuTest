package com.example.shouhutest.KeepActive;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;


public class WakeUpUitls {
    private Context context;

    private PowerManager.WakeLock keepCpu;

    private PowerManager pm;

    private PowerManager.WakeLock wakeLock;

    private WakeUpUitls() {}

    public static WakeUpUitls getInstance() { return Holder.Impl; }

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    public void acquireWakeLock() {
        Log.e("LoopBroadCast", "屏幕熄灭时仍然获取CPU");
        if (null == keepCpu) {
            keepCpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, this.getClass().getSimpleName());
            if (null != keepCpu) {
                keepCpu.acquire();
            }
        } else {
            keepCpu.acquire();
        }
    }

    public void init(Context paramContext) {
        this.context = paramContext.getApplicationContext();
        this.pm = (PowerManager)this.context.getSystemService(Context.POWER_SERVICE);
    }

    public boolean isContextNull() { return (this.context == null); }

    public void releaseWakeLock() {
        PowerManager.WakeLock wakeLock1 = this.keepCpu;
        if (wakeLock1 != null) {
            wakeLock1.release();
            this.keepCpu = null;
        }
        wakeLock1 = this.wakeLock;
        if (wakeLock1 != null) {
            wakeLock1.release();
            this.wakeLock = null;
        }
    }

    public void wakeupAndUnLock() {
        Log.e("LoopBroadCast", "点亮屏幕");
        if (null == wakeLock) {
            //屏锁管理器
            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
            //解锁
            kl.disableKeyguard();

            //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值
            wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass().getSimpleName());
            //点亮屏幕
            if (null != wakeLock) {
                wakeLock.acquire(1);
            }
        } else {
            wakeLock.acquire(1);
        }

    }

    private static class Holder {
        private static WakeUpUitls Impl = new WakeUpUitls();
    }
}