package com.codecacher.wormhole;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

@Deprecated
public class ChannelServiceb extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return ((BinderChannel)Wormhole.getInstance().getServiceChannel(ChannelType.BINDER_CHANNEL)).getIPCProxy().asBinder();
    }
}
