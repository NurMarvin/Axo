package de.nurmarvin.axo.module.modules.antiraid;

import com.mewna.catnip.entity.guild.Guild;
import com.mewna.catnip.entity.message.Message;
import de.nurmarvin.axo.AxoDiscordBot;
import de.nurmarvin.axo.module.Module;
import de.nurmarvin.axo.utils.SimpleRateLimiter;

public final class AntiRaidModule implements Module {
    @Override
    public String name() {
        return "AntiRaidPRO";
    }

    private boolean lockChannel = true;
    private boolean raiseVerificationLevel = false;

    private transient boolean currentlyRaided = false;
    private transient Guild.VerificationLevel oldVerificationLevel;

    public boolean handleMessage(Message message) {
        SimpleRateLimiter simpleRateLimiter = AxoDiscordBot.instance().rateLimitManager().getRateLimiterForGuild(message.guildId());
        boolean rateLimited = !simpleRateLimiter.tryAcquire();

        if(rateLimited) {
            message.channel().sendMessage(String.valueOf(simpleRateLimiter.currentPermits()));
        }

        return true;
    }
}
