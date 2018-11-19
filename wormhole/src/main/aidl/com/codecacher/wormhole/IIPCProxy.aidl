package com.codecacher.wormhole;

interface IIPCProxy {
   IBinder getService(String name);
}
