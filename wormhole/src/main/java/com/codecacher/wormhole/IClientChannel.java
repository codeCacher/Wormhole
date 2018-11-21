package com.codecacher.wormhole;

public interface IClientChannel {
    <T> T getService(Class<T> clazz);
}
