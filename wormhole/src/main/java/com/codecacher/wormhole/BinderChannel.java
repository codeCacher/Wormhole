package com.codecacher.wormhole;

import android.os.IBinder;
import android.os.RemoteException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class BinderChannel implements IChannel {

    private IIPCProxy mIPCProxy;
    private Map<String, IBinder> mBinderCache = new HashMap<>();
    private Map<String, Object> mServiceCache = new HashMap<>();

    BinderChannel(IIPCProxy proxy) {
        this.mIPCProxy = proxy;
    }

    @Override
    public <T> T getService(Class<T> clazz) {
        if (mIPCProxy == null) {
            return null;
        }
        Object service = mServiceCache.get(clazz.getName());
        if (service != null) {
            return (T) service;
        }
        IBinder binder = mBinderCache.get(clazz.getName());
        if (binder == null) {
            try {
                binder = mIPCProxy.getService(clazz.getName());
                mBinderCache.put(clazz.getName(), binder);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return asInterface(clazz, binder);
    }

    private <T> T asInterface(Class<T> clazz, IBinder binder) {
        if ((binder == null)) {
            return null;
        }
//        android.os.IInterface iin = binder.queryLocalInterface(clazz.getName());
//        if (((iin != null) && (iin.getClass() == clazz))) {
//            return ((T) iin);
//        }
        try {
            Class proxyClazz = Class.forName(clazz.getName() + "$Stub$Proxy");
            if (proxyClazz == null) {
                return null;
            }
            Constructor constructor = proxyClazz.getDeclaredConstructor(IBinder.class);
            if (constructor == null) {
                return null;
            }
            constructor.setAccessible(true);
            T service = (T) constructor.newInstance(binder);
            mServiceCache.put(clazz.getName(), service);
            return service;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
