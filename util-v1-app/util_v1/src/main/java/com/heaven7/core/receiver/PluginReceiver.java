package com.heaven7.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * the plug receiver.
 * @author heaven7
 * @since 1.1.7
 */
public final class PluginReceiver extends BroadcastReceiver {

    public static final String KEY_MANAGER = "__Android10_acm";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null){
            return;
        }
        String cn = intent.getStringExtra(KEY_MANAGER);
        final IReceiverPluginManager acm;
        try {
            Class<?> clazz = Class.forName(cn);
            acm = (IReceiverPluginManager) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            System.err.println("can't find IReceiverPluginManager for classname = " + cn);
            return;
        } catch (IllegalAccessException|InstantiationException|ClassCastException  e) {
            System.err.println("can't create IReceiverPluginManager for classname = " + cn);
            return;
        }
        List<IReceiverPlugin> plugins = acm.getPlugins();
        if(plugins.isEmpty()){
            return;
        }
        for (int i = 0, size = plugins.size() ; i < size ; i ++){
            IReceiverPlugin plugin = plugins.get(i);
            if(plugin.getAction().equals(intent.getAction())){
                if(plugin.processIntentData(context, intent)){
                    break;
                }
            }
        }
    }
}
