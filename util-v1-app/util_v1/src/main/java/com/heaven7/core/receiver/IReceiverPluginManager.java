package com.heaven7.core.receiver;

import java.util.List;

/**
 * the plugin manager
 * @author heaven7
 * @since 1.1.7
 */
public interface IReceiverPluginManager {

    /**
     * get receiver plugins
     * @return the plugins
     */
    List<IReceiverPlugin> getPlugins();

}
