package com.codecacher.wormhole;

import android.support.annotation.NonNull;

/**
 * @author cuishun
 * @since 2018/11/28.
 */
class ConnectorFactory {
    @NonNull
    IConnector<IClientChannel> create(ConnectorType type) {
        switch (type) {
            case BROADCAST_CONNECTOR:
                return new BroadCastReceiverConnector();
            case SERVICE_CONNECTOR:
                return new ServiceConnector();
        }
        return null;
    }
}
