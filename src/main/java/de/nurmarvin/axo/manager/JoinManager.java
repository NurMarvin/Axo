package de.nurmarvin.axo.manager;

import com.mewna.catnip.entity.guild.Member;

import java.util.List;

public interface JoinManager {
    List<Member> getJoinsForGuild(String guildId);

    void addJoin(String guildId, Member member);
    void clearJoinsForGuild(String guildId);
}
