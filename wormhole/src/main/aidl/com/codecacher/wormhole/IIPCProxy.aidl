package com.codecacher.wormhole;

interface IIPCProxy {
   IBinder getService(String name);
   void registerService(IBinder binder);
   void registerProxy(String process, IIPCProxy proxy);
}
