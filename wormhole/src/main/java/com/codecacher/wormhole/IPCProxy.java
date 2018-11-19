package com.codecacher.wormhole;

import android.os.IBinder;
import android.os.RemoteException;

public class IPCProxy extends IIPCProxy.Stub {
    @Override
    public IBinder getService(String name) throws RemoteException {
        return Wormhole.getInstance().getServiceManager().getService(name);
    }

//    public <T> T getService(Class<T> clazz, IInterface iInterface) {
//        IBinder service = null;
//        try {
//            service = getService(clazz.getName());
//        } catch (RemoteException e) {
//            e.printStackTrace();
//            return null;
//        }
//        if (service != null) {
//            iInterface
//        }
//    }
}
