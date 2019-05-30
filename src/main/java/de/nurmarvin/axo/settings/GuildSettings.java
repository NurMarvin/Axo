package de.nurmarvin.axo.settings;

import com.google.gson.annotations.JsonAdapter;
import com.mewna.catnip.entity.misc.Emoji;
import de.nurmarvin.axo.module.modules.antiraid.AntiRaidModule;
import de.nurmarvin.axo.module.modules.modlog.ModLogChannel;
import de.nurmarvin.axo.module.modules.modlog.ModLogModule;
import de.nurmarvin.axo.module.modules.utility.UtilityModule;
import de.nurmarvin.axo.utils.ColorTypeAdapter;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Individual guild settings
 */
public class GuildSettings {
    private Commands commands = new Commands();
    private String nickname = "Axo";

    private Map<String, Integer> levels = new HashMap<>();

    private Embeds embeds = new Embeds();

    private Emojis emojis = new Emojis();

    private Modules modules = new Modules();

    public Commands commands() {
        return commands;
    }

    public String nickname() {
        return nickname;
    }

    public Map<String, Integer> levels() {
        return levels;
    }

    public Embeds embeds() {
        return embeds;
    }

    public Emojis emojis() {
        return emojis;
    }

    public Modules modules() {
        return modules;
    }

    public class Commands {
        private String prefix = "!";
        private Map<String, Integer> overrides = new HashMap<>();
        private boolean noPermissionsFeedback = false;
        private String noPermissionsMessage = "It seems like you don't have permissions to run this.";
        private boolean usageFeedback = true;
        private boolean useEmbeds = true;

        public String prefix() {
            return prefix;
        }

        public Map<String, Integer> overrides() {
            return overrides;
        }

        public boolean noPermissionsFeedback() {
            return noPermissionsFeedback;
        }

        public boolean usageFeedback() {
            return usageFeedback;
        }

        public boolean useEmbeds() {
            return useEmbeds;
        }

        public String noPermissionsMessage() {
            return noPermissionsMessage;
        }
    }

    public class Embeds {
        @JsonAdapter(ColorTypeAdapter.class)
        private Color normalColor = new Color(255, 121, 236);
        @JsonAdapter(ColorTypeAdapter.class)
        private Color errorColor = new Color(231, 76, 60);
        private String footerMessage = "Axo %VERSION% by NurMarvin#1337";
        private boolean useRoleColor = false;
        private boolean showTimestamp = false;

        public Color normalColor() {
            return normalColor;
        }

        public Color errorColor() {
            return errorColor;
        }

        public String footerMessage() {
            return footerMessage;
        }

        public boolean useRoleColor() {
            return useRoleColor;
        }

        public boolean showTimestamp() {
            return showTimestamp;
        }
    }

    public class Emojis {
        private String onlineIconEmote = "<:Online:580299209014509579>";
        private String idleIconEmote = "<:Idle:580299209090007040>";
        private String dndIconEmote = "<:DND:580299208930754560>";
        private String offlineIconEmote = "<:Offline:580299208779628596>";
        private String unknownStatusEmote = "❔";
        private String activatedEmote = "✅";
        private String deactivatedEmote = "❌";

        public String onlineIconEmote() {
            return onlineIconEmote;
        }

        public String idleIconEmote() {
            return idleIconEmote;
        }

        public String dndIconEmote() {
            return dndIconEmote;
        }

        public String offlineIconEmote() {
            return offlineIconEmote;
        }

        public String unknownStatusEmote() {
            return unknownStatusEmote;
        }

        public String activatedEmote() {
            return activatedEmote;
        }

        public String deactivatedEmote() {
            return deactivatedEmote;
        }
    }

    public class Modules {
        private UtilityModule utility = new UtilityModule();
        private AntiRaidModule antiRaid = new AntiRaidModule();
        private ModLogModule modLog = new ModLogModule();

        public UtilityModule utility() {
            return utility;
        }

        public AntiRaidModule antiRaid() {
            return antiRaid;
        }

        public ModLogModule modLog() {
            return modLog;
        }
    }
}
