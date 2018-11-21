package com.codecacher.wormhole;

import android.os.IBinder;
import android.os.RemoteException;

public class IPCProxy extends IIPCProxy.Stub {
    @Override
    public IBinder getService(String name) throws RemoteException {
        return Wormhole.getInstance().getChannel(ChannelType.BINDER_CHANNEL).getServiceImp(name);
    }
}
