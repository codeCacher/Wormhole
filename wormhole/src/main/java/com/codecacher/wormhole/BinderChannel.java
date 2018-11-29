package com.codecacher.wormhole;

import android.os.IBinder;
import android.os.RemoteException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BinderChannel implements IChannel<IBinder> {

    private IIPCProxy mIPCProxy;
    private OnDisconnectListener mDisconnectListener;
    private boolean mIsChannelAvailable = true;

    BinderChannel(IIPCProxy proxy) {
        try {
            proxy.asBinder().linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    mIsChannelAvailable = false;
                    if (mDisconnectListener != null) {
                        mDisconnectListener.onDisconnect();
                    }
                }
            }, 0);
        } catch (RemoteException e) {
            mIsChannelAvailable = false;
            if (mDisconnectListener != null) {
                mDisconnectListener.onDisconnect();
            }
            e.printStackTrace();
        }
        this.mIPCProxy = proxy;
    }

    /********************************************Client***********************************************/

    private Map<String, IBinder> mBinderCache = new HashMap<>();
    private Map<String, Object> mServiceCache = new HashMap<>();

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
        T serviceProxy = asInterface(clazz, binder);
        if (serviceProxy != null) {
            mServiceCache.put(clazz.getName(), serviceProxy);
        }
        return serviceProxy;
    }

    @Override
    public void setOnDisconnectListener(OnDisconnectListener listener) {
        this.mDisconnectListener = listener;
        if (!mIsChannelAvailable) {
            listener.onDisconnect();
        }
    }

    private <T> T asInterface(Class<T> clazz, IBinder binder) {
        if ((binder == null)) {
            return null;
        }
        try {
            Class<?> stubClazz = Class.forName(clazz.getName() + "$Stub");
            if (stubClazz == null) {
                return null;
            }
            Method asInterface = stubClazz.getMethod("asInterface", IBinder.class);
            if (asInterface == null) {
                return null;
            }
            return (T) asInterface.invoke(null, binder);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /********************************************Service***********************************************/

    private Map<String, IBinder> mServiceMap = new HashMap<>();

    @Override
    public void registerService(Class clazz, IBinder binder) {
        mServiceMap.put(clazz.getName(), binder);
    }

    @Override
    public IBinder getServiceImp(String name) {
        return mServiceMap.get(name);
    }

    public IIPCProxy getIPCProxy() {
        return mIPCProxy;
    }
}
