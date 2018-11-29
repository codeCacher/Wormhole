package com.codecacher.wormhole;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author cuishun
 * @since 2018/11/28.
 */
public abstract class BaseConnector<T extends IClientChannel> implements IConnector<T> {

    //retry option
    private RetryOption mRetryOption;
    private Emitter<Object> mEmitter;

    //error code
    static final int ERROR_TIME_OUT = 13;
    static final int ERROR_PROXY_NULL = 14;

    private Map<String, Integer> mConnectStates = new HashMap<>();
    private Map<String, ChannelConnectCallBack<T>> mCallBacks = new HashMap<>();

    public BaseConnector() {
        mRetryOption = new RetryOption();
        configRetryOption(mRetryOption);
    }

    @Override
    @CallSuper
    public void connect(final String node, ChannelConnectCallBack<T> conn) {
        mConnectStates.put(node, CONNECT_STATE_CONNECTING);
        mCallBacks.put(node, conn);
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                mEmitter = emitter;
                connect(node);
            }
        }).retryWhen(new RetryWithDelay(mRetryOption.RETRY_TIMES, mRetryOption.RETRY_INTERVAL))
                .timeout(mRetryOption.RETRY_TIME_OUT, TimeUnit.MILLISECONDS)
                .subscribe();
    }

    abstract void connect(String node);

    abstract void configRetryOption(RetryOption mRetryOption);

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
        if (mEmitter == null) {
            throw new IllegalStateException("you should call onConnected() in connect(String process) method!");
        }
        mEmitter.onComplete();
        for (String key : mCallBacks.keySet()) {
            if (!key.equals(node)) {
                continue;
            }
            mCallBacks.get(key).onChannelConnected(channel);
        }
    }

    void onConnectFailed(String node, int errorCode) {
        mConnectStates.put(node, CONNECT_STATE_UNCONNECT);
        if (mEmitter == null) {
            throw new IllegalStateException("you should call onConnectFailed() in connect(String process) method!");
        }
        mEmitter.onError(new Throwable("onConnectFailed errorCode:" + errorCode));
        for (String key : mCallBacks.keySet()) {
            if (!key.equals(node)) {
                continue;
            }
            mCallBacks.get(key).onConnectFailed(errorCode);
        }
    }

    static class RetryOption {
        //retry config
        int RETRY_TIMES = 2;
        int RETRY_INTERVAL = 100;
        int RETRY_TIME_OUT = 3000;
    }
}
