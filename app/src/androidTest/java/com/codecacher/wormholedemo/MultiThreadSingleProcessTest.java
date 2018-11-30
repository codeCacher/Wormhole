package com.codecacher.wormholedemo;

import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.codecacher.wormhole.ChannelConnection;
import com.codecacher.wormhole.IClientChannel;
import com.codecacher.wormhole.ProcessUtils;
import com.codecacher.wormhole.Wormhole;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Instrumented test, which will execute on an Android device.
 * 测试多个线程同时连接同一个进程
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MultiThreadSingleProcessTest {

    private final int THREAD_COUNT = 10;
    private final CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
    private long start;

    @Test
    public void connectTest() {
        start = SystemClock.elapsedRealtime();
        for (int i = 0; i < THREAD_COUNT; i++) {
            new ConnectThread("thread" + i).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class ConnectThread extends Thread {

        ConnectThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            super.run();
            String process = ProcessUtils.getProcessName() + ":b";
            Wormhole.getInstance().addConnectListener(process, new ChannelConnection<IClientChannel>() {
                @Override
                public void onChannelConnected(@NonNull final IClientChannel channel) {
                    Log.e("cuishun", getName() + " duration:" + (SystemClock.elapsedRealtime() - start));
                    useClientChannel(channel);
                    countDownLatch.countDown();
                }

                @Override
                public void onChannelDisconnected() {
                }

                @Override
                public void onConnectFailed(int errorCode) {
                    Log.e("cuishun", "onConnectFailed:" + errorCode);
                    throw new IllegalStateException("onConnectFailed:" + errorCode);
                }
            });
            Wormhole.getInstance().connect(process);
        }
    }

    private void useClientChannel(IClientChannel channel) {
        IBService service = channel.getService(IBService.class);
        if (service == null) {
            Log.e("cuishun", "IClientChannel is null");
            throw new IllegalStateException("IClientChannel is null");
        }
        try {
            int id = new Random().nextInt();
            String name = service.getName(id);
            if (!String.valueOf(id).equals(name)) {
                Log.e("cuishun", "not equel name:" + name + " id:" + id);
                throw new IllegalStateException("RemoteException");
            }
        } catch (RemoteException e) {
            Log.e("cuishun", "RemoteException");
            throw new IllegalStateException("RemoteException");
        }
    }
}
