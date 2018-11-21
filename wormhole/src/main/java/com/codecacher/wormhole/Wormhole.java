package com.codecacher.wormhole;

import android.content.Context;
import android.util.SparseArray;

public class Wormhole {

    public static final int CONNECTOR_TYPE_SERVICE = 101;
    public static final int CONNECTOR_TYPE_BROADCAST = 102;

    private static Wormhole INSTANCE = new Wormhole();

    private SparseArray<IClientChannel> mChannels = new SparseArray<>();
    private SparseArray<IConnector<ProcessNode, IClientChannel>> connnectors = new SparseArray<>();
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

    public <V, T extends IServiceChannel<V>, E extends ChannelType<T, V>> IServiceChannel<V> getChannel(E channelType) {
        IClientChannel channel = mChannels.get(channelType.getType());
        if (channel != null) {
            return (T) channel;
        }
        switch (channelType.getType()) {
            case ChannelType.BINDER_CHANNEL_TYPE:
                channel = new BinderChannel(new IPCProxy());
                break;
        }
        mChannels.put(channelType.getType(), channel);
        return (T) channel;
    }

    //client
    public void connect(String process, ChannelConnection<IClientChannel> conn) {
        connect(process, conn, CONNECTOR_TYPE_SERVICE);
    }

    //client
    public void connect(String process, ChannelConnection<IClientChannel> conn, int channelType) {
        IConnector<ProcessNode, IClientChannel> connector = connnectors.get(channelType);
        if (connector == null) {
            switch (channelType) {
                case CONNECTOR_TYPE_SERVICE:
                    connector = new ServiceConnector();
                    break;
                case CONNECTOR_TYPE_BROADCAST:
                    connector = new BroadCastReceiverConnector();
                    break;
            }
            connnectors.put(CONNECTOR_TYPE_SERVICE, connector);
        }
        if (connector != null) {
            connector.connect(new ProcessNode(process), conn);
        }
    }

    //client
    public void disConnect(String process) {

    }
}
