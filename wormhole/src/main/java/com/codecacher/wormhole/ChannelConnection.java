package com.codecacher.wormhole;

public interface ChannelConnection<T> {
    void onChannelConnected(T service);
    void onChannelDisconnected();
}
