package com.codecacher.wormhole;

import android.support.annotation.NonNull;

/**
 * @author cuishun
 * @since 2018/11/28.
 */
interface ChannelConnectCallBack <T extends IClientChannel> {
    void onChannelConnected(@NonNull T channel);
    void onConnectFailed(int errorCode);
}
