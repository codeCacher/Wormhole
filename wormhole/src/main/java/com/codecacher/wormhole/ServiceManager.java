package com.codecacher.wormhole;

import android.os.IBinder;
import android.os.RemoteException;

import java.util.HashMap;
import java.util.Map;

public class ServiceManager implements IServiceManager {
    private IIPCProxy mIPCProxy;
    private Map<String, IBinder> mServiceMap = new HashMap<>();

    ServiceManager() {

    }

    //client
    void setProxy(IIPCProxy proxy) {
        this.mIPCProxy = proxy;
    }

    //service
    void registerService(Class clazz, IBinder binder) {
        mServiceMap.put(clazz.getName(), binder);
    }

    //client
    @Override
    public IBinder getService(Class clazz) {
        IBinder service = null;
        try {
            service = mIPCProxy.getService(clazz.getName());
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
        if ((service == null)) {
            return null;
        }
        return service;
    }

    //service
    public IBinder getService(String name) {
        return mServiceMap.get(name);
    }
}
