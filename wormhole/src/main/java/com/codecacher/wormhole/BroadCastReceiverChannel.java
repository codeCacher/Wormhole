package com.codecacher.wormhole;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class BroadCastReceiverChannel implements IChannel<ProcessNode, ServiceManager> {
    //cmd action
    private static final String SUFIX = Wormhole.getInstance().getContext().getPackageName();
    public static final String ACTION_REQ_CONNECT = SUFIX + "action_req_connect";
    public static final String ACTION_RES_CONNECTED = SUFIX + "action_res_connected";

    //data
    public static final String DATA_PROCESS_NAME = "data_process_name";
    public static final String DATA_BINDER = "data_binder";

    @Override
    public void connect(ProcessNode node, final ChannelConnection<ServiceManager> conn) {
        Context context = Wormhole.getInstance().getContext();
        ChannelReceiver processReceiver = ProcessHelper.getProcessReceiver();
        processReceiver.setCallBack(new ConnectCallBack() {
            @Override
            public void onConnect(IBinder binder) {
                ServiceManager serviceManager = null;
                IIPCProxy proxy = IIPCProxy.Stub.asInterface(binder);
                if (proxy != null) {
                    serviceManager = Wormhole.getInstance().getServiceManager();
                    serviceManager.setProxy(proxy);
                }
                conn.onChannelConnected(serviceManager);
            }
        });
        context.registerReceiver(processReceiver, new IntentFilter());
        //TODO get receiver name from node
        ComponentName componentName = new ComponentName(context.getPackageName(), "com.codecacher.wormhole.ChannelReceiverb");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.setAction(ACTION_REQ_CONNECT);
        intent.putExtra(DATA_PROCESS_NAME, Application.getProcessName());
        context.sendBroadcast(intent);
    }
}
