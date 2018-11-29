package com.codecacher.wormhole;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Wormhole {

    static final int CONNECTOR_TYPE_SERVICE = 101;
    static final int CONNECTOR_TYPE_BROADCAST = 102;

    private static Wormhole INSTANCE = new Wormhole();

    private Context mContext;

    //服务端通道，按通道类型区分
    private final SparseArray<IServiceChannel> mServiceChannels = new SparseArray<>();
    //客户端通道，按连接的进程区分
    private final Map<String, IClientChannel> mClientChannels = new ConcurrentHashMap<>();
    //进程连接器，按类型区分
    private final SparseArray<IConnector<IClientChannel>> mConnectors = new SparseArray<>();
    //连接回调，按连接的进程区分
    private final Map<String, List<ChannelConnection<IClientChannel>>> mConnCallBacks = new HashMap<>();

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

    @SuppressWarnings("unchecked cast")
    public <V, T extends IServiceChannel<V>, E extends ChannelType<T, V>> IServiceChannel<V> getServiceChannel(E channelType) {
        synchronized (mServiceChannels) {
            IServiceChannel channel = mServiceChannels.get(channelType.getType());
            if (channel != null) {
                return (T) channel;
            }
            channel = mChannelFactory.create(channelType);
            mServiceChannels.put(channelType.getType(), channel);
            return (T) channel;
        }
    }

    @Nullable
    public IClientChannel getClientChannel(String process) {
        return mClientChannels.get(process);
    }

    public void addConnectListener(String process, ChannelConnection<IClientChannel> conn) {
        synchronized (mConnCallBacks) {
            //注册回调
            List<ChannelConnection<IClientChannel>> channelConnections = mConnCallBacks.get(process);
            if (channelConnections == null) {
                channelConnections = new ArrayList<>();
                mConnCallBacks.put(process, channelConnections);
            }
            channelConnections.add(conn);
        }

        //检查通道是否已连接
        IClientChannel clientChannel = mClientChannels.get(process);
        if (clientChannel != null) {
            //已连接则回调一次
            conn.onChannelConnected(clientChannel);
        }
    }

    public void removeConnectListener(String process, ChannelConnection<IClientChannel> conn) {
        synchronized (mConnCallBacks) {
            List<ChannelConnection<IClientChannel>> channelConnections = mConnCallBacks.get(process);
            if (channelConnections == null) {
                return;
            }
            channelConnections.remove(conn);
        }
    }

    //client
    public void connect(String process) {
        connect(process, ConnectorType.BROADCAST_CONNECTOR, false);
    }

    //client
    public void connect(String process, boolean bind) {
        connect(process, ConnectorType.BROADCAST_CONNECTOR, bind);
    }

    //client
    //TODO 对外提供多种连接选择？
    private synchronized void connect(final String process, ConnectorType connectorType, boolean bind) {
        checkProcess(process);

        //检查通道是否已连接或正在连接
        IClientChannel clientChannel = mClientChannels.get(process);
        if (clientChannel != null || isConnectingProcess(process)) {
            //已连接或正在连接则直接返回
            if (connectorType == ConnectorType.BROADCAST_CONNECTOR && bind) {
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
        if (connector.getConnectState(process) != IConnector.CONNECT_STATE_UNCONNECT) {
            return;
        }
        connector.connect(process, new ChannelConnectCallBack<IClientChannel>() {
            @Override
            public void onChannelConnected(@NonNull IClientChannel channel) {
                mClientChannels.put(process, channel);
                notifyChannelConnected(process, channel);
                channel.setOnDisconnectListener(new IClientChannel.OnDisconnectListener() {
                    @Override
                    public void onDisconnect() {
                        mClientChannels.remove(process);
                        notifyChannelDisConnect(process);
                    }
                });
            }

            @Override
            public void onConnectFailed(int errorCode) {
                notifyChannelConnectFailed(process, errorCode);
            }
        });
    }

    /********************************private method*******************************/

    private void bindService(String process) {
        //TODO bindService
        Context context = getContext();
        if (context == null) {
            throw new IllegalStateException("ensure init wormhole before use!");
        }
        ComponentName serviceComponent = ProcessUtils.getServiceComponent(context, process);
        Intent intent = new Intent();
        intent.setComponent(serviceComponent);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    private void checkProcess(String process) {
        if (!ProcessUtils.isProcessIllegal(process)) {
            throw new IllegalArgumentException("process:" + process + " is illegal");
        }
    }

    //TODO isConnectingProcess
    private boolean isConnectingProcess(String process) {
        return false;
    }

    private void notifyChannelConnected(String process, IClientChannel channel) {
        synchronized (mConnCallBacks) {
            final List<ChannelConnection<IClientChannel>> callbacks = mConnCallBacks.get(process);
            if (callbacks == null) {
                return;
            }
            for (ChannelConnection<IClientChannel> conn : callbacks) {
                conn.onChannelConnected(channel);
            }
        }
    }

    private void notifyChannelDisConnect(String process) {
        synchronized (mConnCallBacks) {
            final List<ChannelConnection<IClientChannel>> callbacks = mConnCallBacks.get(process);
            if (callbacks == null) {
                return;
            }
            for (ChannelConnection<IClientChannel> conn : callbacks) {
                conn.onChannelDisconnected();
            }
        }
    }

    private void notifyChannelConnectFailed(String process, int error) {
        synchronized (mConnCallBacks) {
            final List<ChannelConnection<IClientChannel>> callbacks = mConnCallBacks.get(process);
            if (callbacks == null) {
                return;
            }
            for (ChannelConnection<IClientChannel> conn : callbacks) {
                conn.onConnectFailed(error);
            }
        }
    }
}
