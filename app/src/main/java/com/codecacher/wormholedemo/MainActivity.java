package com.codecacher.wormholedemo;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codecacher.wormhole.BinderChannel;
import com.codecacher.wormhole.ChannelConnection;
import com.codecacher.wormhole.IChannel;
import com.codecacher.wormhole.Wormhole;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    IChannel binderChannel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService(new Intent(this, BService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);

        findViewById(R.id.getService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wormhole.getInstance().connect(Application.getProcessName() + ".b", new ChannelConnection<IChannel>() {
                    @Override
                    public void onChannelConnected(IChannel channel) {
                        binderChannel = channel;
                        IBService service = binderChannel.getService(IBService.class);
                        if (service == null) {
                            Log.e("cuishun", "get service failed!");
                            return;
                        }
                        try {
                            String name = service.getName(10220);
                            Log.e("cuishun", name);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChannelDisconnected() {
                        binderChannel = null;
                    }
                }, Wormhole.CHANNEL_TYPE_SERVICE);
            }
        });
    }
}
