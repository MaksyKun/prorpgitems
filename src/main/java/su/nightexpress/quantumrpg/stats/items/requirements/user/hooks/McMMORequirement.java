package su.nightexpress.quantumrpg.stats.items.requirements.user.hooks;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import mc.promcteam.engine.config.api.ILangMsg;
import mc.promcteam.engine.utils.DataUT;
import mc.promcteam.engine.utils.StringUT;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quantumrpg.Perms;
import su.nightexpress.quantumrpg.config.EngineCfg;
import su.nightexpress.quantumrpg.stats.items.ItemTags;
import su.nightexpress.quantumrpg.stats.items.requirements.api.DynamicUserRequirement;

import java.util.Arrays;
import java.util.function.Predicate;

public class McMMORequirement extends DynamicUserRequirement<String[]> {

    public McMMORequirement(@NotNull String name,
                            @NotNull String format
    ) {
        super("mcmmo-skill",
                name,
                format,
                ItemTags.PLACEHOLDER_REQ_USER_MCMMO_SKILL,
                ItemTags.TAG_REQ_USER_MCMMO_SKILL,
                DataUT.STRING_ARRAY);
    }

    @Override
    public @NotNull String getBypassPermission() {
        return Perms.BYPASS_REQ_USER_MCMMO_SKILL;
    }

    @Override
    public boolean canUse(@NotNull Player player, @NotNull ItemStack item) {
        String[] itemClass = this.getRaw(item);
        if (itemClass == null || itemClass.length == 0) return true;

        PrimarySkillType skill = PrimarySkillType.valueOf(itemClass[0].toUpperCase());
        int min       = StringUT.getInteger(itemClass[1], -1);
        int max       = StringUT.getInteger(itemClass[2], 0);

        if(UserManager.getPlayer(player) == null) return false;

        int skillLevel = ExperienceAPI.getLevel(player, skill);
        return min == max ? (skillLevel >= min) : (skillLevel >= min && skillLevel <= max);
    }

    @Override
    public @NotNull String formatValue(@NotNull ItemStack item, @NotNull String[] value) {
        PrimarySkillType skill = PrimarySkillType.valueOf(value[0].toUpperCase());
        int v1       = StringUT.getInteger(value[1], -1);
        int v2       = StringUT.getInteger(value[2], -1);
        int min = Math.min(v1, v2);
        int max = Math.max(v1, v2);
        if(min <= 0)
            return "";

        String lore;
        if (min == max) {
            lore = EngineCfg.LORE_STYLE_REQ_USER_MCMMO_SKILL_FORMAT_SINGLE.replace("%skill%", skill.name()).replace("%min%", String.valueOf(min));
        } else {
            lore = EngineCfg.LORE_STYLE_REQ_USER_MCMMO_SKILL_FORMAT_RANGE.replace("%skill%", skill.name()).replace("%max%", String.valueOf(max)).replace("%min%", String.valueOf(min));
        }
        return ChatColor.WHITE + lore;
    }

    @Override
    public @NotNull ILangMsg getDenyMessage(@NotNull Player player, @NotNull ItemStack src) {
        return plugin.lang().Module_Item_Interact_Error_McMMO_Skill;
    }
}
