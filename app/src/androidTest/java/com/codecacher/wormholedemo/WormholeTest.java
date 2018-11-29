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

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class WormholeTest {
    private long totalTime = 0;

    @Test
    public void connectTest() {
        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getTargetContext();
        final Random random = new Random();
        final Object lock = new Object();
        for (int i = 0; i < 5000; i++) {
            synchronized (lock) {
                final long start = SystemClock.elapsedRealtime();
                Wormhole.getInstance().connect(ProcessUtils.getProcessName() + ":b", new ChannelConnection<IClientChannel>() {
                    @Override
                    public void onChannelConnected(@NonNull final IClientChannel channel) {
                        long duration = SystemClock.elapsedRealtime() - start;
                        totalTime += duration;
                        Log.e("cuishun", "duration:" + duration);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                IBService service = channel.getService(IBService.class);
                                if (service == null) {
                                    Log.e("cuishun", "IClientChannel is null");
                                    throw new IllegalStateException("IClientChannel is null");
                                }
                                try {
                                    int id = random.nextInt();
                                    String name = service.getName(id);
                                    assertEquals(name, String.valueOf(id));
                                } catch (RemoteException e) {
                                    Log.e("cuishun", "RemoteException");
                                    throw new IllegalStateException("RemoteException");
                                }
                                synchronized (lock) {
                                    lock.notifyAll();
                                }
                            }
                        }).start();
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
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("cuishun", "totleTime:" + totalTime);
    }
}
