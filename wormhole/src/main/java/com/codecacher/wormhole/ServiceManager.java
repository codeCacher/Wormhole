package com.codecacher.wormhole;

import android.os.IBinder;
import android.os.RemoteException;

import java.util.HashMap;
import java.util.Map;

public class ServiceManager {
    private Map<String, IBinder> mServiceMap = new HashMap<>();

    ServiceManager() {

    }

    //service
    void registerService(Class clazz, IBinder binder) {
        mServiceMap.put(clazz.getName(), binder);
    }

    //service
    public IBinder getService(String name) {
        return mServiceMap.get(name);
    }
}
