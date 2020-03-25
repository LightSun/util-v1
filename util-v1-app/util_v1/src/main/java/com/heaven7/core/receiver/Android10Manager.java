package com.heaven7.core.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

/**
 * the android 10 manager help wake-up activity in background.
 * @author heaven7
 * @see PluginReceiver
 * @since 1.1.8
 */
public final class Android10Manager implements IReceiverPluginManager {

    private final List<IReceiverPlugin> mPlugins = new ArrayList<>(5);
    private static Android10Manager sINSTANCE;
    private Context mAppContext;

    @Keep
    public static synchronized Android10Manager get(Context context) {
        if(sINSTANCE == null){
            sINSTANCE = new Android10Manager();
            sINSTANCE.mAppContext = context.getApplicationContext();
        }
        return sINSTANCE;
    }

    public static boolean isActived(){
        return sINSTANCE != null;
    }

    @Override
    public List<IReceiverPlugin> getPlugins() {
        return mPlugins;
    }

    public void addPlugin(IReceiverPlugin plugin) {
        if (!mPlugins.contains(plugin)) {
            mPlugins.add(plugin);
        }
    }
    public void sendBroadcast(String action, Bundle data){
        Intent intent = new Intent(action).putExtras(data);
        intent.putExtra(PluginReceiver.KEY_MANAGER, Android10Manager.class.getName());
        mAppContext.sendBroadcast(intent);
    }

}
