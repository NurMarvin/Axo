package de.nurmarvin.axo.manager;

import de.nurmarvin.axo.utils.SimpleRateLimiter;

import java.util.Map;

public interface RateLimitManager {
    SimpleRateLimiter getRateLimiterForGuild(String guildId);

    Map<String, SimpleRateLimiter> rateLimiters();
}
