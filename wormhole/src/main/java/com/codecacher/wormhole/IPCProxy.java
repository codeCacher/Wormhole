package com.codecacher.wormhole;

import android.os.IBinder;
import android.os.RemoteException;

public class IPCProxy extends IIPCProxy.Stub {

    @Override
    public IBinder getService(String name) throws RemoteException {
        return Wormhole.getInstance().getServiceChannel(ChannelType.BINDER_CHANNEL).getServiceImp(name);
    }

    @Override
    public void registerService(IBinder binder) throws RemoteException {
        //TODO 远程注册
    }

    @Override
    public void registerProxy(String process, IIPCProxy proxy) throws RemoteException {
        //TODO 是否要把channel赋值放在这里？
        Wormhole.getInstance().putClientChannel(process, new BinderChannel(proxy));
    }
}
