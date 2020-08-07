package com.heaven7.core.util;

import android.os.Bundle;
import android.util.ArrayMap;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * save object to this storage
 * <p>Note: this class doesn't support multi process.</p>
 * @param <T> the object type.
 * @since 1.1.8
 */
public final class LeakyStorage<T> {

    private static final String KEY_PROCESS_ID = "LeakyS::process_id";
    private static final String KEY_ID = "LeakyS::id";
    private final Object mLock = new Object();

    @GuardedBy("mLock")
    private final ArrayList<T> sStorage = new ArrayList<>();
    @GuardedBy("mLock")
    private final ArrayMap<T, Integer> sTypefaceMap = new ArrayMap<T, Integer>();
    private final AtomicInteger mAliveCount = new AtomicInteger(0);

    public int getAliveCount(){
        return mAliveCount.get();
    }
    /**
     * save the key-info to bundle and save whole object to this.
     * @param obj the object to save.
     * @param parcel the bundle
     */
    public void save(@Nullable T obj, @NonNull Bundle parcel) {
        parcel.putInt(KEY_PROCESS_ID, android.os.Process.myPid());
        synchronized (mLock) {
            final int id;
            final Integer i = sTypefaceMap.get(obj);
            if (i != null) {
                id = i;
            } else {
                id = sStorage.size();
                sStorage.add(obj);
                sTypefaceMap.put(obj, id);
                mAliveCount.incrementAndGet();
            }
            parcel.putInt(KEY_ID, id);
        }
    }

    /**
     * restore the object from bundle.
     * @param parcel the bundle which saved key-info
     * @return the object.
     */
    public @Nullable T restore(@NonNull Bundle parcel) {
        return restore(parcel, true);
    }

    /**
     * restore the object from bundle.
     * @param parcel the bundle which saved key-info
     * @param removeFromStorage true if remove from storage
     * @return the object.
     * @since 1.1.8
     */
    public @Nullable T restore(@NonNull Bundle parcel, boolean removeFromStorage) {
        final int pid = parcel.getInt(KEY_PROCESS_ID);
        final int saveId = parcel.getInt(KEY_ID);
        if (pid != android.os.Process.myPid()) {
            return null;  // The object was created and written in another process.
        }
        synchronized (mLock) {
            T t = sStorage.get(saveId);
            if(removeFromStorage){
                sStorage.set(saveId, null);
                sTypefaceMap.remove(t);
                if(mAliveCount.decrementAndGet() <= 0){
                    sStorage.clear();
                    sTypefaceMap.clear();
                }
            }
            return t;
        }
    }
}
