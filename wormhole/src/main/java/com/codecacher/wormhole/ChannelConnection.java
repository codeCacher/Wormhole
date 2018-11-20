package com.codecacher.wormhole;

public interface ChannelConnection<T extends IChannel> {
    void onChannelConnected(T channel);
    void onChannelDisconnected();
}
