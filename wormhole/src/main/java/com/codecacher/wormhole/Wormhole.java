package com.codecacher.wormhole;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wormhole {

    static final int CONNECTOR_TYPE_SERVICE = 101;
    static final int CONNECTOR_TYPE_BROADCAST = 102;

    private static Wormhole INSTANCE = new Wormhole();

    private Context mContext;

    //服务端通道，按通道类型区分
    private SparseArray<IServiceChannel> mServiceChannels = new SparseArray<>();
    //客户端通道，按连接的进程区分
    private Map<String, IClientChannel> mClientChannels = new HashMap<>();
    //进程连接器，按类型区分
    private SparseArray<IConnector<IClientChannel>> mConnectors = new SparseArray<>();
    //连接回调，按连接的进程区分
    private Map<String, List<ChannelConnection<IClientChannel>>> mConnCallBacks = new HashMap<>();

    private ChannelFactory mChannelFactory;
    private ConnectorFactory mConnectorFactory;

    public static Wormhole getInstance() {
        return INSTANCE;
    }

    public void init(Context appContext) {
        this.mChannelFactory = new ChannelFactory();
        this.mConnectorFactory = new ConnectorFactory();
        this.mContext = appContext.getApplicationContext();
    }

    Context getContext() {
        if (mContext == null) {
            throw new IllegalStateException("ensure init wormhole before use!");
        }
        return mContext;
    }

    void putClientChannel(String process, IClientChannel channel) {
        mClientChannels.put(process, channel);
    }

    public <V, T extends IServiceChannel<V>, E extends ChannelType<T, V>> IServiceChannel<V> getServiceChannel(E channelType) {
        IServiceChannel channel = mServiceChannels.get(channelType.getType());
        if (channel != null) {
            return (T) channel;
        }
        channel = mChannelFactory.create(channelType);
        mServiceChannels.put(channelType.getType(), channel);
        return (T) channel;
    }

    @Nullable
    public IClientChannel getClientChannel(String process) {
        return mClientChannels.get(process);
    }

    //client
    public void connect(String process, ChannelConnection<IClientChannel> conn) {
        connect(process, conn, ConnectorType.BROADCAST_CONNECTOR);
    }

    //client
    //TODO 对外提供多种连接选择？
    private synchronized void connect(final String process, final ChannelConnection<IClientChannel> conn, ConnectorType connectorType) {
        checkProcess(process);
        //注册回调
        List<ChannelConnection<IClientChannel>> channelConnections = mConnCallBacks.get(process);
        if (channelConnections == null) {
            channelConnections = new ArrayList<>();
            mConnCallBacks.put(process, channelConnections);
        }
        channelConnections.add(conn);

        //检查通道是否已连接或正在连接
        IClientChannel clientChannel = mClientChannels.get(process);
        if (clientChannel != null || isConnectingProcess(process)) {
            //已连接或正在连接则直接返回
            if (clientChannel != null) {
                conn.onChannelConnected(clientChannel);
            }
            if (connectorType == ConnectorType.SERVICE_CONNECTOR_BIND) {
                bindService(process);
            }
            return;
        }

        //还未连接，使用指定的connector进行连接
        IConnector<IClientChannel> connector = mConnectors.get(connectorType.getType());
        if (connector == null) {
            connector = mConnectorFactory.create(connectorType);
            mConnectors.put(CONNECTOR_TYPE_SERVICE, connector);
        }
        switch (connector.getConnectState(process)) {
            case IConnector.CONNECT_STATE_CONNECTING:
                return;
            case IConnector.CONNECT_STATE_CONNECTED:
                //TODO 连接但是没有通道？不存在
                return;
        }
        connector.connect(process, new ChannelConnectCallBack<IClientChannel>() {
            @Override
            public void onChannelConnected(@NonNull IClientChannel channel) {
                mClientChannels.put(process, channel);
                final List<ChannelConnection<IClientChannel>> callbacks = mConnCallBacks.get(process);
                if (callbacks == null) {
                    return;
                }
                for (ChannelConnection<IClientChannel> conn : callbacks) {
                    conn.onChannelConnected(channel);
                }
                channel.setOnDisconnectListener(new IClientChannel.OnDisconnectListener() {
                    @Override
                    public void onDisconnect() {
                        mClientChannels.remove(process);
                        for (ChannelConnection<IClientChannel> conn : callbacks) {
                            conn.onChannelDisconnected();
                        }
                    }
                });
            }

            @Override
            public void onConnectFailed(int errorCode) {
                List<ChannelConnection<IClientChannel>> callbacks = mConnCallBacks.get(process);
                if (callbacks == null) {
                    return;
                }
                for (ChannelConnection<IClientChannel> conn : callbacks) {
                    conn.onConnectFailed(errorCode);
                }
            }
        });
    }

    //client
    public void disConnect(String process) {
        //TODO
        mClientChannels.remove(process);
        unbindService(process);
    }

    /********************************private method*******************************/

    private void bindService(String process) {
        //TODO
    }

    private void unbindService(String process) {
        //TODO
    }

    private void checkProcess(String process) {
        //TODO
    }

    //TODO
    private boolean isConnectingProcess(String process) {
        return false;
    }
}
