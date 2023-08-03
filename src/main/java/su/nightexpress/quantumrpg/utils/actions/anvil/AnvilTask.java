package su.nightexpress.quantumrpg.utils.actions.anvil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.InventoryBlockStartEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quantumrpg.QuantumRPG;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class AnvilTask implements BukkitTask, Listener {


    private Player player;


    private String title;
    private Inventory inventory;
    private ItemStack searchItem;
    private ItemStack confirmItem;
    private ItemStack cancelItem;

    private String objective;
    private String result;

    private List<Function<String, ?>> requirements;
    private boolean isCancelled = false;
    private boolean isProcessCancelled = false;


    public AnvilTask(Player player, String objective) {
        this.player = player;
        this.objective = objective;
        this.title = String.format("Insert %s", objective);
        this.inventory = Bukkit.createInventory(player, InventoryType.ANVIL, title);
        this.searchItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        this.confirmItem = new ItemStack(Material.NAME_TAG);
        this.cancelItem = new ItemStack(Material.BARRIER);

        this.requirements = new ArrayList<>();
        create();

        QuantumRPG.instance.getPluginManager().registerEvents(this, QuantumRPG.getInstance());
    }

    public AnvilTask(Player player, String objective, Material searchItemType) {
        this.player = player;
        this.objective = objective;
        this.title = String.format("Insert %s", objective);
        this.inventory = Bukkit.createInventory(player, InventoryType.ANVIL, title);
        this.searchItem = new ItemStack(searchItemType);
        this.confirmItem = new ItemStack(Material.NAME_TAG);

        this.requirements = new ArrayList<>();
        create();

        QuantumRPG.instance.getPluginManager().registerEvents(this, QuantumRPG.getInstance());
    }

    public void setRequirement(Function<String, ?> requirement) {
        this.requirements.add(requirement);
    }

    private void create() {
        ItemMeta searchMeta = searchItem.getItemMeta();
        searchMeta.setDisplayName("\n");
        searchItem.setItemMeta(searchMeta);

        ItemMeta confirmMeta = confirmItem.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "\n");
        confirmMeta.setLore(List.of(ChatColor.GREEN + "Confirm"));
        confirmItem.setItemMeta(confirmMeta);

        ItemMeta cancelMeta = cancelItem.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
        cancelItem.setItemMeta(cancelMeta);

        inventory.setItem(0, searchItem);
        inventory.setItem(1, cancelItem);
        inventory.setItem(2, confirmItem);
    }

    public String getObjective() {
        return objective;
    }
    public String getResult() {
        return result;
    }

    public void open() {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onAnvilClick(PrepareAnvilEvent event) {
        if(event.getView().getTopInventory() != inventory) return;

        result = event.getInventory().getRenameText();
        ItemMeta confirmMeta = event.getResult().getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + event.getInventory().getRenameText());
        confirmMeta.setLore(List.of(ChatColor.GREEN + "Confirm"));
        event.getResult().setItemMeta(confirmMeta);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getClickedInventory() != inventory) return;
        event.setCancelled(true);
        int slot = event.getSlot();

        confirmItem = event.getInventory().getItem(2);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        result = confirmMeta.getDisplayName();
        confirmMeta.setDisplayName(ChatColor.GREEN + result);
        confirmMeta.setLore(List.of(ChatColor.GREEN + "Confirm"));
        confirmItem.setItemMeta(confirmMeta);
        event.getInventory().setItem(2, confirmItem);

        if(slot == 1) {
            // Cancel Input
            isProcessCancelled = true;
        } else if(slot == 2) {
            // Check input
            if(result == null || result.trim().isEmpty()) {
                player.sendMessage(ChatColor.RED + "▸ The input cannot be empty.");
                return;
            }
            for(Function<String, ?> req : requirements) {
                if (req.apply(result) == null) {
                    player.sendMessage(ChatColor.RED + "▸ The input is invalid for your requirement.");
                    return;
                }
            }
            player.sendMessage(ChatColor.GREEN + "▸ Input was successful!");
            isCancelled = true;
        }
    }


    @Override
    public int getTaskId() {
        return 0;
    }

    @NotNull
    @Override
    public Plugin getOwner() {
        return QuantumRPG.getInstance();
    }

    @Override
    public boolean isSync() {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }

    public boolean isProcessCancelled() {
        return isProcessCancelled;
    }
}
