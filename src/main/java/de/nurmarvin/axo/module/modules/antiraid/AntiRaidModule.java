package de.nurmarvin.axo.module.modules.antiraid;

import com.mewna.catnip.entity.guild.Member;
import com.mewna.catnip.entity.guild.Role;
import de.nurmarvin.axo.AxoDiscordBot;
import de.nurmarvin.axo.manager.JoinManager;
import de.nurmarvin.axo.module.Module;
import de.nurmarvin.axo.settings.GuildSettings;
import de.nurmarvin.axo.utils.SimpleRateLimiter;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public final class AntiRaidModule implements Module {
    @Override
    public String name() {
        return "AntiRaidPRO";
    }

    private boolean countNewAccountsOnly = false;
    private long newAccountEpoch = 604800L;
    private int maxMemberJoinsCount = 5;
    private int maxMemberJoinsDelayInSeconds = 20;
    private String assignRole = "";

    public boolean handleGuildJoin(Member member) {
        JoinManager joinManager = AxoDiscordBot.instance().joinManager();
        CompletableFuture<Void> clearCallback = CompletableFuture.runAsync(() -> {});

        SimpleRateLimiter simpleRateLimiter = AxoDiscordBot
                .instance().rateLimitManager()
                .getOrCreateRateLimiterForGuild(member.guildId(),
                                                maxMemberJoinsCount,
                                                maxMemberJoinsDelayInSeconds, clearCallback);

        clearCallback.thenAccept(ignored -> joinManager.clearJoinsForGuild(member.guildId()));

        long accountAge = Instant.now().toEpochMilli() - member.user().creationTime().toInstant().toEpochMilli();

        if(countNewAccountsOnly && accountAge > newAccountEpoch * 1000) return false;

        joinManager.addJoin(member.guildId(), member);

        boolean rateLimited = !simpleRateLimiter.tryAcquire();

        if(rateLimited) {
            GuildSettings guildSettings = AxoDiscordBot.instance().guildSettingsManager()
                                                       .getGuildSetting(member.guildId());

            if(!assignRole.isEmpty()) {
                Role role = member.guild().role(assignRole);

                if(role != null) {
                    joinManager.getJoinsForGuild(member.guildId())
                               .forEach(oldMember -> member.guild().addRoleToMember(role, oldMember));
                    member.guild().addRoleToMember(role, member);
                }
            }

            if (guildSettings.modules().modLog() != null) {
                guildSettings.modules().modLog()
                             .handleRaid(member, joinManager.getJoinsForGuild(member.guildId()).size(),
                                         maxMemberJoinsDelayInSeconds);
            }
            return true;
        }
        return false;
    }
}
