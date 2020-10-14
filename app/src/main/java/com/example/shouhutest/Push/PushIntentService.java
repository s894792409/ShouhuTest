package com.example.shouhutest.Push;

import android.content.Context;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

public class PushIntentService extends GTIntentService {

    private static final String TAG="PushIntentService";
    @Override
    public void onReceiveServicePid(Context context, int i) {
        Log.e(TAG, "onReceiveServicePid: "+i);
    }

//    接收cid
    @Override
    public void onReceiveClientId(Context context, String s) {
        Log.e(TAG, "onReceiveClientId: "+s );
    }

//    处理透传信息
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
        Log.e(TAG, "onReceiveMessageData: " +
                "appid = " + gtTransmitMessage.getAppid() +
                "\ntaskid = " + gtTransmitMessage.getTaskId() +
                "\nmessageid = " + gtTransmitMessage.getMessageId() +
                "\npkg = " + gtTransmitMessage.getPkgName() +
                "\ncid = " + gtTransmitMessage.getClientId()+
                "\nplayload = "+ new String(gtTransmitMessage.getPayload()));
    }

//    cid离线上线通知
    @Override
    public void onReceiveOnlineState(Context context, boolean b) {
        Log.e(TAG, "onReceiveOnlineState: "+b );
    }

//    各种事件处理回执
    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {
        Log.e(TAG, "onReceiveCommandResult: "+gtCmdMessage.toString() );
    }

//    通知到达，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {
        Log.e(TAG, "onNotificationMessageArrived: "+gtNotificationMessage );
    }

//    通知点击，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {
        Log.e(TAG, "onNotificationMessageClicked: "+gtNotificationMessage );
    }
}
