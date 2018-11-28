package com.codecacher.wormhole;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cuishun
 * @since 2018/11/28.
 */
public class BaseConnector<T extends IClientChannel> implements IConnector<T> {

    //error code
    static final int ERROR_TIME_OUT = 13;
    static final int ERROR_PROXY_NULL = 14;

    private Map<String, Integer> mConnectStates = new HashMap<>();
    private Map<String, ChannelConnectCallBack<T>> mCallBacks = new HashMap<>();

    @Override
    @CallSuper
    public void connect(String node, ChannelConnectCallBack<T> conn) {
        mConnectStates.put(node, CONNECT_STATE_CONNECTING);
        mCallBacks.put(node, conn);
    }

    @Override
    public int getConnectState(String process) {
        Integer state = mConnectStates.get(process);
        if (state == null) {
            return CONNECT_STATE_UNCONNECT;
        }
        return state;
    }

    void onConnected(String node, @NonNull T channel) {
        mConnectStates.put(node, CONNECT_STATE_CONNECTED);
        for (String key : mCallBacks.keySet()) {
            if (!key.equals(node)) {
                continue;
            }
            mCallBacks.get(key).onChannelConnected(channel);
        }
    }

    void onConnectFailed(String node, int errorCode) {
        mConnectStates.put(node, CONNECT_STATE_UNCONNECT);
        for (String key : mCallBacks.keySet()) {
            if (!key.equals(node)) {
                continue;
            }
            mCallBacks.get(key).onConnectFailed(errorCode);
        }
    }
}
