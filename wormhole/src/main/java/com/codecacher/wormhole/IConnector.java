package com.codecacher.wormhole;

public interface IConnector<N extends INode, T extends IClientChannel> {
    void connect(N node, ChannelConnection<T> conn);
}
