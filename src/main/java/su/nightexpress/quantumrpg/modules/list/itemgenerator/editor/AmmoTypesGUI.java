package su.nightexpress.quantumrpg.modules.list.itemgenerator.editor;

import mc.promcteam.engine.config.api.JYML;
import mc.promcteam.engine.manager.api.gui.ContentType;
import mc.promcteam.engine.manager.api.gui.GuiClick;
import mc.promcteam.engine.manager.api.gui.GuiItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quantumrpg.modules.list.itemgenerator.ItemGeneratorManager;
import su.nightexpress.quantumrpg.stats.items.ItemStats;
import su.nightexpress.quantumrpg.stats.items.attributes.AmmoAttribute;

import java.util.ArrayList;
import java.util.List;

public class AmmoTypesGUI extends AbstractEditorGUI {
    private String listening = null;

    public AmmoTypesGUI(@NotNull ItemGeneratorManager itemGeneratorManager, ItemGeneratorManager.GeneratorItem itemGenerator) {
        super(itemGeneratorManager, itemGenerator, 54);
        setTitle("[&d"+itemGenerator.getId()+"&r] editor/"+EditorGUI.ItemType.AMMO_TYPES.getTitle());
    }

    @Override
    protected void onCreate(@NotNull Player player, @NotNull Inventory inventory, int page) {
        JYML cfg = this.itemGenerator.getConfig();
        List<AmmoAttribute> list = new ArrayList<>(ItemStats.getAmmos());
        int totalPages = Math.max((int) Math.ceil(list.size()*1.0/42), 1);
        final int currentPage = page < 1 ? totalPages : Math.min(page, totalPages);
        this.setUserPage(player, currentPage, totalPages);
        GuiClick guiClick = (player1, type, clickEvent) -> {
            this.setUserPage(player1, currentPage, totalPages);
            if (type == null) { return; }
            Class<?> clazz = type.getClass();
            if (clazz.equals(ContentType.class)) {
                ContentType type2 = (ContentType) type;
                switch (type2) {
                    case RETURN:
                        new EditorGUI(itemGeneratorManager, itemGenerator).open(player1, 1);
                        break;
                    case EXIT: {
                        player1.closeInventory();
                        break;
                    }
                    case NEXT: {
                        saveAndReopen(currentPage+1);
                        break;
                    }
                    case BACK: {
                        saveAndReopen(currentPage-1);
                        break;
                    }
                }
                return;
            }
            if (type == EditorGUI.ItemType.AMMO_TYPES) {
                GuiItem guiItem = this.getButton(player1, clickEvent.getSlot());
                if (guiItem == null) { return; }
                switch (clickEvent.getClick()) {
                    case DROP: case CONTROL_DROP: {
                        this.itemGenerator.getConfig().remove(EditorGUI.ItemType.AMMO_TYPES.getPath()+'.'+guiItem.getId());
                        saveAndReopen(currentPage);
                        break;
                    }
                    default: {
                        sendSetMessage(guiItem.getId(), String.valueOf(cfg.getDouble(EditorGUI.ItemType.AMMO_TYPES.getPath()+'.'+guiItem.getId(), 0)));
                        break;
                    }
                }
            }
        };
        for (int ammoIndex = (currentPage-1)*42, last = Math.min(list.size(), ammoIndex+42), invIndex = 1;
             ammoIndex < last; ammoIndex++, invIndex++) {
            if ((invIndex)%9 == 8) { invIndex += 2; }
            AmmoAttribute ammoAttribute = list.get(ammoIndex);
            Material material;
            switch (ammoAttribute.getType()) {
                case SNOWBALL: {
                    material = Material.SNOWBALL;
                    break;
                }
                case EGG: {
                    material = Material.EGG;
                    break;
                }
                case FIREBALL: {
                    material = Material.FIRE_CHARGE;
                    break;
                }
                case WITHER_SKULL: {
                    material = Material.WITHER_SKELETON_SKULL;
                    break;
                }
                case SHULKER_BULLET: {
                    material = Material.SHULKER_SHELL;
                    break;
                }
                case LLAMA_SPIT: {
                    material = Material.LLAMA_SPAWN_EGG;
                    break;
                }
                case ENDER_PEARL: {
                    material = Material.ENDER_PEARL;
                    break;
                }
                case EXP_POTION: {
                    material = Material.EXPERIENCE_BOTTLE;
                    break;
                }
                default: {
                    material = Material.ARROW;
                    break;
                }
            }
            String id = ammoAttribute.getId().toUpperCase();
            this.addButton(this.createButton(id, EditorGUI.ItemType.AMMO_TYPES, material,
                                             "&e"+ammoAttribute.getName(), List.of(
                                                     "&bCurrent: &a"+cfg.getDouble(EditorGUI.ItemType.AMMO_TYPES.getPath()+'.'+id, 0),
                                                     "&6Left-Click: &eSet",
                                                     "&6Drop: &eRemove"), invIndex, guiClick));
        }
        this.addButton(this.createButton("prev-page", ContentType.BACK, Material.ENDER_PEARL, "&dPrevious Page", List.of(), 0, guiClick));
        this.addButton(this.createButton("next-page", ContentType.NEXT, Material.ENDER_PEARL, "&dNext Page", List.of(), 8, guiClick));
        this.addButton(this.createButton("return", ContentType.RETURN, Material.BARRIER, "&c&lReturn", List.of(), 53, guiClick));
    }

    private void sendSetMessage(String ammoType, String currentValue) {
        this.listening = ammoType;
        this.player.closeInventory();
        player.sendMessage("▸ Enter the desired chance for "+ammoType+" ammo type, or \"cancel\" to go back");
        if (currentValue != null) {
            BaseComponent component = new TextComponent("[Current value]");
            component.setColor(ChatColor.GOLD);
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, currentValue));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Enter the current value to chat")));
            player.spigot().sendMessage(component);
        }
    }

    @Override
    public void onChat(AsyncPlayerChatEvent event) {
        if (this.listening == null) { return; }
        event.setCancelled(true);
        String ammoType = this.listening;
        this.listening = null;
        String message = event.getMessage().strip();
        if (message.equalsIgnoreCase("cancel")) {
            saveAndReopen(getUserPage(this.player, 0));
            return;
        }
        double chance;
        try {
            chance = Double.parseDouble(message);
        } catch (NumberFormatException e) {
            plugin.lang().ItemGenerator_Cmd_Editor_Error_InvalidInput.replace("%input%", message).replace("%value%", "chance").send(player);
            saveAndReopen(getUserPage(this.player, 0));
            return;
        }
        if (chance == 0) {
            this.itemGenerator.getConfig().remove(EditorGUI.ItemType.AMMO_TYPES.getPath()+'.'+ammoType);
        } else {
            this.itemGenerator.getConfig().set(EditorGUI.ItemType.AMMO_TYPES.getPath()+'.'+ammoType, chance);
        }
        saveAndReopen(getUserPage(this.player, 0));
    }
}
