package com.codecacher.wormhole;

public interface IConnector<N extends INode, T extends IChannel> {
    void connect(N node, ChannelConnection<T> conn);
}
