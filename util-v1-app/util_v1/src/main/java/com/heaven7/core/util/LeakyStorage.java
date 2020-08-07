package com.heaven7.core.util;

import android.os.Bundle;
import android.util.ArrayMap;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

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
        final int pid = parcel.getInt(KEY_PROCESS_ID);
        final int saveId = parcel.getInt(KEY_ID);
        if (pid != android.os.Process.myPid()) {
            return null;  // The object was created and written in another process.
        }
        synchronized (mLock) {
            return sStorage.get(saveId);
        }
    }
}
