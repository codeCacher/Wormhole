package com.codecacher.wormhole;

public interface IChannel {
    <T> T getService(Class<T> clazz);
}
