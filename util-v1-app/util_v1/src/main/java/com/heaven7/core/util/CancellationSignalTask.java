package com.heaven7.core.util;

import android.os.CancellationSignal;

/**
 * signal task
 * @since 1.1.7
 */
/*public*/ class CancellationSignalTask implements Runnable {

    private final CancellationSignal signal;
    private final Runnable real;

    public CancellationSignalTask(Runnable real, CancellationSignal signal) {
        this.signal = signal;
        this.real = real;

        if(real instanceof CancellationSignal.OnCancelListener){
            signal.setOnCancelListener((CancellationSignal.OnCancelListener) real);
        }
    }
    @Override
    public void run() {
        if(!signal.isCanceled()){
            real.run();
        }
    }
}
