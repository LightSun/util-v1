package com.heaven7.core.util;

import android.graphics.Typeface;
import android.os.Parcel;
import android.support.annotation.GuardedBy;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;

import java.util.ArrayList;

/*public*/ class LeakyTypefaceStorage {
    private static final Object sLock = new Object();

    @GuardedBy("sLock")
    private static final ArrayList<Typeface> sStorage = new ArrayList<>();
    @GuardedBy("sLock")
    private static final ArrayMap<Typeface, Integer> sTypefaceMap = new ArrayMap<>();

    /**
     * Write typeface to parcel.
     *
     * You can't transfer Typeface to a different process. {@link readTypefaceFromParcel} will
     * return {@code null} if the {@link readTypefaceFromParcel} is called in a different process.
     *
     * @param typeface A {@link Typeface} to be written.
     * @param parcel A {@link Parcel} object.
     */
    public static void writeTypefaceToParcel(@Nullable Typeface typeface, @NonNull Parcel parcel) {
        parcel.writeInt(android.os.Process.myPid());
        synchronized (sLock) {
            final int id;
            final Integer i = sTypefaceMap.get(typeface);
            if (i != null) {
                id = i.intValue();
            } else {
                id = sStorage.size();
                sStorage.add(typeface);
                sTypefaceMap.put(typeface, id);
            }
            parcel.writeInt(id);
        }
    }

    /**
     * Read typeface from parcel.
     *
     * If the {@link Typeface} was created in another process, this method returns null.
     *
     * @param parcel A {@link Parcel} object
     * @return A {@link Typeface} object.
     */
    public static @Nullable Typeface readTypefaceFromParcel(@NonNull Parcel parcel) {
        final int pid = parcel.readInt();
        final int typefaceId = parcel.readInt();
        if (pid != android.os.Process.myPid()) {
            return null;  // The Typeface was created and written in another process.
        }
        synchronized (sLock) {
            return sStorage.get(typefaceId);
        }
    }
}
