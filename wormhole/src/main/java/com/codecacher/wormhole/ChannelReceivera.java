package com.codecacher.wormhole;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

@Deprecated
public class ChannelReceivera extends ChannelReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        if (BroadCastReceiverConnector.ACTION_REQ_CONNECT.equals(intent.getAction())) {
            //req to establish channel
//            String process = intent.getStringExtra(BroadCastReceiverConnector.DATA_PROCESS_NAME);
            BinderWrapper binderWrapper = intent.getParcelableExtra(BroadCastReceiverConnector.DATA_BINDER);
            IBinder binder = binderWrapper.getBinder();
            IBinderConnector proxy = IBinderConnector.Stub.asInterface(binder);
            try {
                proxy.registerProxy(ProcessUtils.getProcessName(), ((BinderChannel) Wormhole.getInstance().getServiceChannel(ChannelType.BINDER_CHANNEL)).getIPCProxy());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return;
        }
    }
}
