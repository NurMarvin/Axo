package de.nurmarvin.axo.manager;

import de.nurmarvin.axo.utils.SimpleRateLimiter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface RateLimitManager {
    SimpleRateLimiter getRateLimiterForGuild(String guildId);

    SimpleRateLimiter getOrCreateRateLimiterForGuild(String guildId, int maxPermits, int refreshTime, CompletableFuture<Void> clearCallback);

    Map<String, SimpleRateLimiter> rateLimiters();
}
