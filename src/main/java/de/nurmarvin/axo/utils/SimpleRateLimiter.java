package de.nurmarvin.axo.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleRateLimiter {
    private int maxPermits;
    private int currentPermits;
    private int refreshTime;
    private TimeUnit timePeriod;
    private ScheduledExecutorService scheduler;
 
    public static SimpleRateLimiter create(int permits, int refreshTime, TimeUnit timePeriod) {
        SimpleRateLimiter limiter = new SimpleRateLimiter(permits, refreshTime, timePeriod);
        limiter.schedulePermitReplenishment();
        return limiter;
    }
 
    private SimpleRateLimiter(int permits, int refreshTime, TimeUnit timePeriod) {
        this.maxPermits = permits;
        this.currentPermits = permits;
        this.refreshTime = refreshTime;
        this.timePeriod = timePeriod;
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
        }, 0, refreshTime, timePeriod);
 
    }
}