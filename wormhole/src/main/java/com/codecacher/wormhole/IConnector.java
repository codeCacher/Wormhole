package com.codecacher.wormhole;

public interface IConnector<T extends IClientChannel> {

    int CONNECT_STATE_UNCONNECT = 100;
    int CONNECT_STATE_CONNECTING = 101;
    int CONNECT_STATE_CONNECTED = 102;

    void connect(String node, ChannelConnectCallBack<T> conn);
    int getConnectState(String process);
}
