package de.nurmarvin.axo.module.modules.utility;

import com.google.common.collect.Lists;
import com.mewna.catnip.entity.guild.Guild;
import com.mewna.catnip.entity.guild.Member;
import com.mewna.catnip.entity.guild.Role;
import com.mewna.catnip.entity.user.VoiceState;
import de.nurmarvin.axo.module.Module;

import java.util.List;

public final class UtilityModule implements Module {
    private List<String> autoRoles = Lists.newArrayList();
    private List<String> voiceChatRoles = Lists.newArrayList();

    @Override
    public String name() {
        return "Utility";
    }

    public void handleGuildJoin(Member member) {
        if(autoRoles == null) return;

        autoRoles.forEach(roleId -> {
            Role role = member.guild().role(roleId);

            if(role != null)
                member.guild().addRoleToMember(role, member);
        });
    }

    public void handleVoiceChatJoin(VoiceState voiceState) {
        if(voiceChatRoles == null) return;

        Guild guild = voiceState.guild();
        Member member = voiceState.member();

        if(guild == null) return;
        if(member == null) return;

        voiceChatRoles.forEach(roleId -> {
            Role role = guild.role(roleId);

            if(role != null) {
                if(!member.roleIds().contains(roleId) && voiceState.channel() != null) {
                    role.guild().addRoleToMember(role, member);
                } else if(voiceState.channel() == null) {
                    role.guild().removeRoleFromMember(role, member);
                }
            }
        });
    }
}
