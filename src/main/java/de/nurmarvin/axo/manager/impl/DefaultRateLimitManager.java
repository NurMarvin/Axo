package de.nurmarvin.axo.manager.impl;

import com.google.common.collect.Maps;
import de.nurmarvin.axo.manager.RateLimitManager;
import de.nurmarvin.axo.utils.SimpleRateLimiter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DefaultRateLimitManager implements RateLimitManager {
    private Map<String, SimpleRateLimiter> rateLimiters;

    public DefaultRateLimitManager() {
        this.rateLimiters = Maps.newHashMap();
    }

    @Override
    public SimpleRateLimiter getRateLimiterForGuild(String guildId) {
        if(this.rateLimiters.containsKey(guildId)) return rateLimiters.get(guildId);

        this.rateLimiters.put(guildId, SimpleRateLimiter.create(5, 20, TimeUnit.SECONDS));

        return this.getRateLimiterForGuild(guildId);
    }

    @Override
    public Map<String, SimpleRateLimiter> rateLimiters() {
        return rateLimiters;
    }
}
