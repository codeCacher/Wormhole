package com.codecacher.wormhole;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServiceChannel implements IChannel<ProcessNode, ServiceManager> {
    @Override
    public void connect(ProcessNode node, final ChannelConnection<ServiceManager> conn) {
        Context context = Wormhole.getInstance().getContext();
        //TODO get service name from node
        ComponentName componentName = new ComponentName(context.getPackageName(), "com.codecacher.wormholedemo.ChannelServiceb");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IIPCProxy proxy = IIPCProxy.Stub.asInterface(service);
                ServiceManager serviceManager = null;
                if (proxy != null) {
                    serviceManager = Wormhole.getInstance().getServiceManager();
                    serviceManager.setProxy(proxy);
                }
                conn.onChannelConnected(serviceManager);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                conn.onChannelDisconnected();
            }
        }, Context.BIND_AUTO_CREATE);
    }
}
