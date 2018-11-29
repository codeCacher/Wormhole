package com.codecacher.wormhole;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;

/**
 * @author CodeCacher
 * @since 2018/11/28
 */

public class ProcessUtils {
    public static String getProcessName() {
        return Application.getProcessName();
    }

    public static ComponentName getServiceComponent(Context context, String process) {
        //TODO get service name from node
        return new ComponentName(context.getPackageName(), "com.codecacher.wormhole.ChannelServiceb");
    }

    public static ComponentName getReceiverComponent(Context context, String process) {
        //TODO get receiver name from node
        return new ComponentName(context.getPackageName(), "com.codecacher.wormhole.ChannelReceiverb");
    }

    public static boolean isProcessIllegal(String process) {
        //TODO check process
        return true;
    }
}
