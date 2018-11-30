package com.codecacher.wormhole;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

@Deprecated
public class ChannelReceiverb extends ChannelReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.TAG, getClass().getSimpleName() + "onReceive");
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            Log.w(Constants.TAG, getClass().getSimpleName() + "onReceive intent or action is null, return");
            return;
        }
        if (BroadCastReceiverConnector.ACTION_REQ_CONNECT.equals(intent.getAction())) {
            //req to establish channel
//            String process = intent.getStringExtra(BroadCastReceiverConnector.DATA_PROCESS_NAME);
            Log.i(Constants.TAG, getClass().getSimpleName() + "onReceive ACTION_REQ_CONNECT");
            BinderWrapper binderWrapper = intent.getParcelableExtra(BroadCastReceiverConnector.DATA_BINDER);
            IBinder binder = binderWrapper.getBinder();
            IBinderConnector proxy = IBinderConnector.Stub.asInterface(binder);
            try {
                proxy.registerProxy(ProcessUtils.getProcessName(), ((BinderChannel) Wormhole.getInstance().getServiceChannel(ChannelType.BINDER_CHANNEL)).getIPCProxy());
            } catch (RemoteException e) {
                Log.e(Constants.TAG, getClass().getSimpleName() + "RemoteException:" + e);
            }
            return;
        }
    }
}
