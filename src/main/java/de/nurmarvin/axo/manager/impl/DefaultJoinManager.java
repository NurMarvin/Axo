package de.nurmarvin.axo.manager.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mewna.catnip.entity.guild.Member;
import de.nurmarvin.axo.manager.JoinManager;

import java.util.List;
import java.util.Map;

public class DefaultJoinManager implements JoinManager {
    private Map<String, List<Member>> joins = Maps.newHashMap();

    @Override
    public List<Member> getJoinsForGuild(String guildId) {
        if(joins.containsKey(guildId)) return joins.get(guildId);
        joins.put(guildId, Lists.newArrayList());
        return this.getJoinsForGuild(guildId);
    }

    @Override
    public void addJoin(String guildId, Member member) {
        this.getJoinsForGuild(guildId).add(member);
    }

    @Override
    public void clearJoinsForGuild(String guildId) {
        this.getJoinsForGuild(guildId).clear();
    }
}
