package de.nurmarvin.axo.manager.impl;

import com.google.common.collect.Maps;
import de.nurmarvin.axo.manager.RateLimitManager;
import de.nurmarvin.axo.utils.SimpleRateLimiter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DefaultRateLimitManager implements RateLimitManager {
    private Map<String, SimpleRateLimiter> rateLimiters;

    public DefaultRateLimitManager() {
        this.rateLimiters = Maps.newHashMap();
    }

    @Override
    public SimpleRateLimiter getRateLimiterForGuild(String guildId) {
        return this.getOrCreateRateLimiterForGuild(guildId, 1, 1, null);
    }

    @Override
    public SimpleRateLimiter getOrCreateRateLimiterForGuild(String guildId, int maxPermits,
                                                            int refreshTime, CompletableFuture<Void> clearCallback) {
        if(this.rateLimiters.containsKey(guildId)) return rateLimiters.get(guildId);

        this.rateLimiters.put(guildId, SimpleRateLimiter.create(maxPermits, refreshTime, TimeUnit.SECONDS, clearCallback));

        return this.getOrCreateRateLimiterForGuild(guildId, maxPermits, refreshTime, clearCallback);
    }

    @Override
    public Map<String, SimpleRateLimiter> rateLimiters() {
        return rateLimiters;
    }
}
