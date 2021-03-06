package com.codecacher.wormhole;

import android.support.annotation.NonNull;

public interface ChannelConnection<T extends IClientChannel> {
    void onChannelConnected(@NonNull T channel);
    void onChannelDisconnected();
    void onConnectFailed(int errorCode);
}
