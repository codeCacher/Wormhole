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

/**
 * Instrumented test, which will execute on an Android device.
 * 测试单进程多次连接同一进程
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SingleThreadSingleProcessTest {
    private long start;

    @Test
    public void connectTest() {
        final Object lock = new Object();
        String process = ProcessUtils.getProcessName() + ":b";
        Wormhole.getInstance().addConnectListener(process, new ChannelConnection<IClientChannel>() {
            @Override
            public void onChannelConnected(@NonNull final IClientChannel channel) {
                long duration = SystemClock.elapsedRealtime() - start;
                Log.e("cuishun", "duration:" + duration);
                useClientChannel(channel);
                synchronized (lock) {
                    lock.notifyAll();
                }
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
        for (int i = 0; i < 1000; i++) {
            IClientChannel clientChannel = Wormhole.getInstance().getClientChannel(process);
            if (clientChannel == null) {
                start = SystemClock.elapsedRealtime();
                Wormhole.getInstance().connect(process);
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                continue;
            }
            useClientChannel(clientChannel);
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
