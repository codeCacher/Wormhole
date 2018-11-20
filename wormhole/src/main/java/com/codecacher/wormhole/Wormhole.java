package com.codecacher.wormhole;

import android.content.Context;
import android.os.IBinder;
import android.util.SparseArray;

public class Wormhole {

    public static final int CHANNEL_TYPE_SERVICE = 101;
    public static final int CHANNEL_TYPE_BROADCAST = 102;

    private static Wormhole INSTANCE = new Wormhole();

    private ServiceManager mManager = new ServiceManager();
    private SparseArray<IConnector<ProcessNode, IChannel>> connnectors = new SparseArray<>();
    private Context mContext;

    public static Wormhole getInstance() {
        return INSTANCE;
    }

    public void init(Context appContext) {
        this.mContext = appContext.getApplicationContext();
    }

    Context getContext() {
        if (mContext == null) {
            throw new IllegalStateException("ensure init wormhole before use!");
        }
        return mContext;
    }

    ServiceManager getServiceManager() {
        return mManager;
    }

    //service
    public void registerService(Class clazz, IBinder binder) {
        mManager.registerService(clazz, binder);
    }

    //client
    public void connect(String process, ChannelConnection<IChannel> conn) {
        connect(process, conn, CHANNEL_TYPE_SERVICE);
    }

    //client
    public void connect(String process, ChannelConnection<IChannel> conn, int channelType) {
        IConnector<ProcessNode, IChannel> connector = connnectors.get(channelType);
        if (connector == null) {
            switch (channelType) {
                case CHANNEL_TYPE_SERVICE:
                    connector = new ServiceConnector();
                    break;
                case CHANNEL_TYPE_BROADCAST:
                    connector = new BroadCastReceiverConnector();
                    break;
            }
            connnectors.put(CHANNEL_TYPE_SERVICE, connector);
        }
        if (connector != null) {
            connector.connect(new ProcessNode(process), conn);
        }
    }

    //client
    public void disConnect(String process) {

    }
}
