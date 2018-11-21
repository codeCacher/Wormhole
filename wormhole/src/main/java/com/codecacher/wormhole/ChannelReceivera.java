package com.codecacher.wormhole;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

@Deprecated
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ChannelReceivera extends ChannelReceiver {

//    private ConnectCallBack mCallBack;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        if (BroadCastReceiverConnector.ACTION_REQ_CONNECT.equals(intent.getAction())) {
            //req to establish channel
            String process = intent.getStringExtra(BroadCastReceiverConnector.DATA_PROCESS_NAME);
            //TODO get component from process
            ComponentName componentName = new ComponentName(context.getPackageName(), "com.codecacher.wormhole.ChannelReceivera");
            Intent ackIntent = new Intent();
            ackIntent.setComponent(componentName);
            Bundle bundle = new Bundle();
            bundle.putBinder(BroadCastReceiverConnector.DATA_BINDER, new IPCProxy());
            bundle.putString(BroadCastReceiverConnector.DATA_PROCESS_NAME, Application.getProcessName());
            ackIntent.putExtras(bundle);
            ackIntent.setAction(BroadCastReceiverConnector.ACTION_RES_CONNECTED);
            context.sendBroadcast(ackIntent);
            return;
        }
        if (BroadCastReceiverConnector.ACTION_RES_CONNECTED.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                return;
            }
            String process = extras.getString(BroadCastReceiverConnector.DATA_PROCESS_NAME);
            IBinder binder = extras.getBinder(BroadCastReceiverConnector.DATA_BINDER);
            ChannelConnection<IClientChannel> conn = BroadCastReceiverConnector.callBacks.get(process);
            if (conn != null) {
                BroadCastReceiverConnector.callBacks.remove(process);
                IIPCProxy proxy = IIPCProxy.Stub.asInterface(binder);
                BinderChannel binderChannel = new BinderChannel(proxy);
                conn.onChannelConnected(binderChannel);
            }
        }
    }

//    @Override
//    void setCallBack(ConnectCallBack callBack) {
//        mCallBack = callBack;
//    }
}
