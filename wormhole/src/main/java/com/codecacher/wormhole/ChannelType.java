package com.codecacher.wormhole;

import android.os.IBinder;

public class ChannelType<T extends IServiceChannel, V> {
    //channel type
    static final int BINDER_CHANNEL_TYPE = 1;

    //channel type instance
    public static ChannelType<BinderChannel, IBinder> BINDER_CHANNEL = new ChannelType<>(BINDER_CHANNEL_TYPE);

    private int type;

    private ChannelType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
