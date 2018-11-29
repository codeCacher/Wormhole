package com.codecacher.wormhole;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class BroadCastReceiverConnector extends BaseConnector<IClientChannel> {

    //cmd action
    private static final String SUFIX = Wormhole.getInstance().getContext().getPackageName();
    public static final String ACTION_REQ_CONNECT = SUFIX + "action_req_connect";

    //data
    public static final String DATA_PROCESS_NAME = "data_process_name";
    public static final String DATA_BINDER = "data_binder";

    public static Map<String, ChannelConnection<IClientChannel>> callBacks = new HashMap<>();

    @Override
    public void connect(final String node, final ChannelConnectCallBack<IClientChannel> conn) {
        super.connect(node, conn);
        Context context = Wormhole.getInstance().getContext();
        ComponentName componentName = ProcessUtils.getReceiverComponent(context, node);

        final IBinderConnectorImp binderConnector = new IBinderConnectorImp();
        binderConnector.setRegisterCallBack(node, new IBinderConnectorImp.RegisterCallBack() {
            @Override
            public void onRegister(IIPCProxy remoteProxy) {
                if (remoteProxy == null) {
                    onConnectFailed(node, ERROR_PROXY_NULL);
                    return;
                }
                BinderChannel binderChannel = new BinderChannel(remoteProxy);
                onConnected(node, binderChannel);
            }
        });

        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.setAction(ACTION_REQ_CONNECT);
        intent.putExtra(DATA_PROCESS_NAME, ProcessUtils.getProcessName());
        intent.putExtra(DATA_BINDER, new BinderWrapper(binderConnector.asBinder()));
        context.sendBroadcast(intent);
    }
}
