package com.codecacher.wormhole;

import android.os.RemoteException;
import android.text.TextUtils;

/**
 * @author CodeCacher
 * @since 2018/11/28
 * BroadCastReceiverConnector 将该类发送到目的端，目的端通过该类反注册到连接端
 */

public class IBinderConnectorImp extends IBinderConnector.Stub {

    private String mProcess;
    private RegisterCallBack mRemoteRegisterCallBack;

    @Override
    public void registerProxy(String process, IIPCProxy proxy) throws RemoteException {
        if (mRemoteRegisterCallBack != null && !TextUtils.isEmpty(process) && process.equals(mProcess)) {
            mRemoteRegisterCallBack.onRegister(proxy);
        }
    }

    void setRegisterCallBack(String process, RegisterCallBack callBack) {
        this.mProcess = process;
        this.mRemoteRegisterCallBack = callBack;
    }

    interface RegisterCallBack {
        void onRegister(IIPCProxy proxy);
    }
}
