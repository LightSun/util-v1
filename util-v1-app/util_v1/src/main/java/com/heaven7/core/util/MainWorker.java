/*
 * Copyright (C) 2015 
 *            heaven7(donshine723@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.heaven7.core.util;

import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;

/**the all post runnables will run on main thread.
 * @author heaven7
 */
public final class MainWorker {

    public static final Handler MainHandler = new Handler(Looper.getMainLooper());
	
	public static void post(Runnable r){
		post(r, null);
	}

	/**
	 * post a task which can control by target signal.
	 * @param r the real task
	 * @param signal the signal
	 * @since 1.1.7
	 */
	public static void post(Runnable r, CancellationSignal signal){
		if(currentIsMainThread()){
			r.run();
		}else{
			if(signal == null){
				MainHandler.post(r);
			}else {
				MainHandler.post(new CancellationSignalTask(r, signal));
			}
		}
	}
	public static void postAtfront(Runnable r){
		MainHandler.postAtFrontOfQueue(r);
	}

	public static boolean currentIsMainThread(){
		return Thread.currentThread() == Looper.getMainLooper().getThread();
	}
	
	public static void remove(Runnable r){
		MainHandler.removeCallbacks(r);
	}
	
	public static void postDelay(long delay,Runnable r){
		MainHandler.postDelayed(r, delay);
	}

	public static void removePreviousAndPost(Runnable r){
		MainHandler.removeCallbacks(r);
		MainHandler.post(r);
	}
	public static void removePreviousAndPostDelay(long delayMills,Runnable r){
		MainHandler.removeCallbacks(r);
		MainHandler.postDelayed(r,delayMills);
	}
}
