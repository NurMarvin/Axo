package de.nurmarvin.axo.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleRateLimiter {
    private int maxPermits;
    private int currentPermits;
    private CompletableFuture<Void> clearCallback;
    private int refreshTime;
    private TimeUnit timePeriod;
    private ScheduledExecutorService scheduler;

    public static SimpleRateLimiter create(int permits, int refreshTime, TimeUnit timePeriod) {
        return create(permits, refreshTime, timePeriod, null);
    }

    public static SimpleRateLimiter create(int permits, int refreshTime, TimeUnit timePeriod, CompletableFuture<Void> clearCallback) {
        SimpleRateLimiter limiter = new SimpleRateLimiter(permits, refreshTime, timePeriod, clearCallback);
        limiter.schedulePermitReplenishment();
        return limiter;
    }
 
    private SimpleRateLimiter(int permits, int refreshTime, TimeUnit timePeriod, CompletableFuture<Void> clearCallback) {
        this.maxPermits = permits;
        this.currentPermits = permits;
        this.refreshTime = refreshTime;
        this.timePeriod = timePeriod;
        this.clearCallback = clearCallback;
    }
 
    public boolean tryAcquire() {
        currentPermits--;
        return currentPermits > 0;
    }

    public int currentPermits() {
        return this.currentPermits;
    }

    public void stop() {
        scheduler.shutdownNow();
    }
 
    public void schedulePermitReplenishment() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            this.currentPermits = maxPermits;
            System.out.println("Resetting to " + maxPermits + "; " + currentPermits);
            this.clearCallback.complete(null);
        }, 0, refreshTime, timePeriod);
 
    }
}