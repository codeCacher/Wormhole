package com.codecacher.wormholedemo;

import android.app.Application;

import com.codecacher.wormhole.Wormhole;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Wormhole.getInstance().init(this);
        String processName = getProcessName();
        if (processName.endsWith("b")) {
            Wormhole.getInstance().registerService(IBService.class, new IBServiceImp());
        }
    }
}
