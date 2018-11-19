package com.codecacher.wormhole;

public interface IChannel<N extends INode, T> {
    void connect(N node, ChannelConnection<T> conn);
}
