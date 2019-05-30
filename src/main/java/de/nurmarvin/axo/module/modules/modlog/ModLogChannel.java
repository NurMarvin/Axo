package de.nurmarvin.axo.module.modules.modlog;

import com.google.common.collect.Sets;

import java.util.Set;

public class ModLogChannel {
    private boolean useEmbeds = false;
    private Set<ModLogActions> include = Sets.newHashSet();
    private Set<ModLogActions> exclude = Sets.newHashSet();

    public boolean useEmbeds() {
        return useEmbeds;
    }

    public Set<ModLogActions> include() {
        return include;
    }

    public Set<ModLogActions> exclude() {
        return exclude;
    }
}
