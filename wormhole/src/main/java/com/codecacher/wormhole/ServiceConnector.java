package com.codecacher.wormhole;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServiceConnector implements IConnector<ProcessNode, IClientChannel> {
    @Override
    public void connect(ProcessNode node, final ChannelConnection<IClientChannel> conn) {
        Context context = Wormhole.getInstance().getContext();
        //TODO get service name from node
        ComponentName componentName = new ComponentName(context.getPackageName(), "com.codecacher.wormholedemo.ChannelServiceb");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IIPCProxy proxy = IIPCProxy.Stub.asInterface(service);
                BinderChannel binderChannel = new BinderChannel(proxy);
                conn.onChannelConnected(binderChannel);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                conn.onChannelDisconnected();
            }
        }, Context.BIND_AUTO_CREATE);
    }
}
