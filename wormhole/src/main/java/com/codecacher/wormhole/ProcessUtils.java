package com.codecacher.wormhole;

import android.app.Application;

/**
 * @author CodeCacher
 * @since 2018/11/28
 */

public class ProcessUtils {
    public static String getProcessName() {
        return Application.getProcessName();
    }
}
