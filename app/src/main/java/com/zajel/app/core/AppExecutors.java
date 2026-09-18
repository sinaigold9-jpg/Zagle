package com.zajel.app.core;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/** Shared bounded executors for network and lightweight background work. */
public final class AppExecutors {
    private static final AtomicInteger THREADS = new AtomicInteger();
    private static final ThreadFactory FACTORY = runnable -> {
        Thread thread = new Thread(runnable, "zajel-bg-" + THREADS.incrementAndGet());
        thread.setPriority(Thread.NORM_PRIORITY - 1);
        return thread;
    };
    private static final ExecutorService NETWORK = Executors.newFixedThreadPool(3, FACTORY);
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private AppExecutors() { }
    public static ExecutorService network() { return NETWORK; }
    public static void main(Runnable action) { MAIN.post(action); }
}
