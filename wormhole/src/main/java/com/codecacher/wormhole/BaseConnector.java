package com.codecacher.wormhole;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author cuishun
 * @since 2018/11/28.
 */
public abstract class BaseConnector<T extends IClientChannel> implements IConnector<T> {

    //retry option
    private RetryOption mRetryOption;

    //error code
    static final int ERROR_TIME_OUT = 13;
    static final int ERROR_PROXY_NULL = 14;

    private final Map<String, Integer> mConnectStates = new ConcurrentHashMap<>();
    private final Map<String, ChannelConnectCallBack<T>> mCallBacks = new ConcurrentHashMap<>();
    private final Map<String, ObservableEmitter<T>> mEmitters = new HashMap<>();

    public BaseConnector() {
        mRetryOption = new RetryOption();
        configRetryOption(mRetryOption);
    }

    @Override
    @CallSuper
    public void connect(final String node, ChannelConnectCallBack<T> conn) {
        mConnectStates.put(node, CONNECT_STATE_CONNECTING);
        mCallBacks.put(node, conn);
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                mEmitters.put(node, emitter);
                Log.i(Constants.TAG, this.getClass().getSimpleName() + " connect " + node);
                connect(node);
            }
        }).timeout(mRetryOption.RETRY_TIME_OUT, TimeUnit.MILLISECONDS)
                .retryWhen(new RetryWithDelay(mRetryOption.RETRY_TIMES, mRetryOption.RETRY_INTERVAL))
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(T channel) {
                        ChannelConnectCallBack<T> callBack = mCallBacks.get(node);
                        if (callBack != null) {
                            callBack.onChannelConnected(channel);
                            mCallBacks.remove(node);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(Constants.TAG, this.getClass().getSimpleName() + " connect onError:" + e);
                        mConnectStates.put(node, CONNECT_STATE_UNCONNECT);
                        int error = ERROR_TIME_OUT;
                        if (e instanceof ConnFailThrowable) {
                            error = ((ConnFailThrowable) e).getErrorCode();
                        }
                        ChannelConnectCallBack<T> callBack = mCallBacks.get(node);
                        if (callBack != null) {
                            callBack.onConnectFailed(error);
                            mCallBacks.remove(node);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
        Log.i(Constants.TAG, this.getClass().getSimpleName() + " onConnected process:" + node);
        mConnectStates.put(node, CONNECT_STATE_CONNECTED);
        ObservableEmitter<T> emitter = mEmitters.get(node);
        if (emitter == null) {
            throw new IllegalStateException("you should call onConnected() in connect(String process) method!");
        }
        if (!emitter.isDisposed()) {
            emitter.onNext(channel);
            emitter.onComplete();
        }
    }

    void onConnectFailed(String node, int errorCode) {
        Log.i(Constants.TAG, this.getClass().getSimpleName() + " onConnectFailed process:" + node + " error:" + errorCode);
        ObservableEmitter<T> emitter = mEmitters.get(node);
        if (emitter == null) {
            throw new IllegalStateException("you should call onConnectFailed() in connect(String process) method!");
        }
        if (!emitter.isDisposed()) {
            emitter.onError(new ConnFailThrowable(errorCode));
        }
    }

    static class RetryOption {
        //retry config
        int RETRY_TIMES = 2;
        int RETRY_INTERVAL = 100;
        int RETRY_TIME_OUT = 1000;
    }

    static class ConnFailThrowable extends Throwable {
        private int mErrorCode;

        ConnFailThrowable(int errorCode) {
            this.mErrorCode = errorCode;
        }

        int getErrorCode() {
            return mErrorCode;
        }
    }
}
