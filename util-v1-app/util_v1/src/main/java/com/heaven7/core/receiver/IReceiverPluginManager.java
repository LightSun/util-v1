package com.heaven7.core.receiver;

import java.util.List;

/**
 * the plugin manager interface . you must declare a static method with a parameter 'context' named 'get' to get the instance.
 * that often used for receiver. when our process is killed. but receiver can receive intent.
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
