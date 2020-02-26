package com.heaven7.core.receiver;

import android.content.Context;
import android.content.Intent;

/**
 * the plugin of receiver
 * @author heaven7
 * @since 1.1.7
 */
public interface IReceiverPlugin {

    /**
     * the focus action of plugin
     * @return the action
     */
    String getAction();

    /**
     * process the intent data.
     * @param context the context
     * @param intent the intent to handle
     * @return true if this action is consumed.
     */
    boolean processIntentData(Context context, Intent intent);
}