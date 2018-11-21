package com.codecacher.wormhole;

public interface IServiceChannel<T> {
    void registerService(Class clazz, T service);
    T getServiceImp(String name);
}
