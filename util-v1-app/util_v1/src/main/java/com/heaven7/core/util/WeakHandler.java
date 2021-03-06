package com.heaven7.core.util;

import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;

/**
 * Created by heaven7 on 2016/1/26.
 */
public abstract class WeakHandler<T> extends Handler {

    private final WeakReference<T> mWeakRef;

    public WeakHandler(T t) {
        this.mWeakRef = new WeakReference<T>(t);
    }

    public WeakHandler(Looper looper,T t) {
        super(looper);
        this.mWeakRef = new WeakReference<T>(t);
    }

    public WeakHandler(Looper looper,T t, Callback callback) {
        super(looper, callback);
        this.mWeakRef = new WeakReference<T>(t);
    }

    public T get(){
        return mWeakRef.get();
    }

    /**
     * post a task which can control by signal.
     * @param r the task
     * @param signal the signal
     * @since 1.1.7
     */
    public void post(Runnable r, CancellationSignal signal){
        post(new CancellationSignalTask(r, signal));
    }
}
