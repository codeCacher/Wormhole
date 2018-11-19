package com.codecacher.wormholedemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.codecacher.wormhole.IPCProxy;

public class ChannelServiceb extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IPCProxy();
    }
}