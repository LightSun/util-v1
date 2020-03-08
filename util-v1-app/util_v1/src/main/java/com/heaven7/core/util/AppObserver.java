package com.heaven7.core.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * the app observer used to observe 'front->back' or 'back->front'.
 * @author heaven7
 * @since 1.1.7
 */
public final class AppObserver implements Application.ActivityLifecycleCallbacks {

    /**
     * the callback app observer.
     */
    public interface Callback{
        /**
         * called on app back to front
         * @param activity the trigger activity.
         */
        void onAppBackToFront(Activity activity);
        /**
         * called on app front to back
         * @param activity the trigger activity.
         */
        void onAppFrontToBack(Activity activity);
    }

    private static AppObserver INSTANCE;

    public static AppObserver get(){
        if(INSTANCE == null){
            INSTANCE = new AppObserver();
        }
        return INSTANCE;
    }
    private int mActivityCount;
    private boolean mBackgroundApp;
    private final List<Item> mItems = new CopyOnWriteArrayList<Item>();

    public void addCallback(Callback callback, boolean oneShot) {
        mItems.add(new Item(callback, oneShot));
    }

    public void register(Context context){
        Application app = (Application) context.getApplicationContext();
        app.registerActivityLifecycleCallbacks(this);
    }
    public void unregister(Context context){
        Application app = (Application) context.getApplicationContext();
        app.unregisterActivityLifecycleCallbacks(this);
    }

    public void onActivityCreated(Activity activity, Bundle savedInstanceState){

    }
    public void onActivityStarted(Activity activity){
        mActivityCount ++;
        if(mBackgroundApp){
            mBackgroundApp = false;
            dispatchBackToFront(activity);
        }
    }

    public void onActivityResumed(Activity activity){

    }
    public void onActivityPaused(Activity activity){

    }
    public void onActivityStopped(Activity activity){
        mActivityCount --;
        if (mActivityCount == 0) {
            mBackgroundApp = true;
            dispatchFrontToBack(activity);
        }
    }
    public void onActivitySaveInstanceState(Activity activity, Bundle outState){

    }
    public void onActivityDestroyed(Activity activity){

    }
    private void dispatchBackToFront(Activity activity) {
        ArrayList<Item> list = new ArrayList<>(mItems);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Item item = list.get(i);
            item.callback.onAppBackToFront(activity);
            if(item.oneShot){
                mItems.remove(item);
            }
        }
    }
    private void dispatchFrontToBack(Activity activity) {
        ArrayList<Item> list = new ArrayList<>(mItems);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Item item = list.get(i);
            item.callback.onAppFrontToBack(activity);
            if(item.oneShot){
                mItems.remove(item);
            }
        }
    }
    private static class Item{
        final Callback callback;
        final boolean oneShot;

        Item(Callback callback, boolean oneShot) {
            this.callback = callback;
            this.oneShot = oneShot;
        }
    }
}
