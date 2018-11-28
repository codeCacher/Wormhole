package com.codecacher.wormhole;

import com.codecacher.wormhole.IIPCProxy;

// BroadCastReceiverConnector binder Connector
interface IBinderConnector {
     void registerProxy(String process, IIPCProxy proxy);
}
