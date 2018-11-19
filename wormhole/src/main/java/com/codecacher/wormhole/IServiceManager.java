package com.codecacher.wormhole;

import android.os.IBinder;

public interface IServiceManager {
    public IBinder getService(Class clazz);
}
