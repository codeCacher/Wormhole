package com.codecacher.wormhole;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServiceConnector extends BaseConnector<IClientChannel> {

    @Override
    public void connect(final String node, final ChannelConnectCallBack<IClientChannel> conn) {
        super.connect(node, conn);
        Context context = Wormhole.getInstance().getContext();
        ComponentName componentName = ProcessUtils.getServiceComponent(context, node);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IIPCProxy proxy = IIPCProxy.Stub.asInterface(service);
                if (proxy == null) {
                    onConnectFailed(node, ERROR_TIME_OUT);
                    return;
                }
                BinderChannel binderChannel = new BinderChannel(proxy);
                onConnected(node, binderChannel);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //use linkToDeath,do nothing
            }
        }, Context.BIND_AUTO_CREATE);
    }
}
