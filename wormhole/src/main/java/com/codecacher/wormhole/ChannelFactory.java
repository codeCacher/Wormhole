package com.codecacher.wormhole;

import android.support.annotation.NonNull;

/**
 * @author cuishun
 * @since 2018/11/28.
 */
class ChannelFactory {

    @NonNull
    IChannel create(ChannelType type) {
        switch (type.getType()) {
            case ChannelType.BINDER_CHANNEL_TYPE:
                return new BinderChannel(new IPCProxy());
        }
        return null;
    }
}
