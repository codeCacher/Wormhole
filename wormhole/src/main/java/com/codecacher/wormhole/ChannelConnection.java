package com.codecacher.wormhole;

public interface ChannelConnection<T extends IClientChannel> {
    void onChannelConnected(T channel);
    void onChannelDisconnected();
}
