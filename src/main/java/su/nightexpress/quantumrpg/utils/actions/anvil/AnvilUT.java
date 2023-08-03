package su.nightexpress.quantumrpg.utils.actions.anvil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import su.nightexpress.quantumrpg.QuantumRPG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class AnvilUT {

    public static void createInputTasks(Player player, TreeMap<String, String> reqInput, Consumer<TreeMap<String, String>> callback) {
        // Initialize the required input
        List<AnvilTask> tasks = new ArrayList<>();
        for(Map.Entry<String, String> entry : reqInput.entrySet())
            tasks.add(new AnvilTask(player, entry.getKey()));

        // Run each gui until all inputs are acquired
        new BukkitRunnable() {
            AnvilTask task;

            @Override
            public void run() {
                if(task == null) {
                    task = tasks.remove(0);
                    task.open();
                    return;
                }

                if(task.isProcessCancelled()) {
                    player.sendMessage(ChatColor.RED + "▸ The input process was cancelled.");
                    player.closeInventory();
                    cancel();
                    return;
                }

                if(!task.isCancelled())
                    return;

                if(task.getResult() != null) {
                    reqInput.put(task.getObjective(), task.getResult());
                    player.closeInventory();
                }

                if(tasks.size() == 0) {
                    // return the input through callback
                    callback.accept(reqInput);
                    cancel();
                    return;
                }

                task = tasks.remove(0);
                task.open();
            }
        }.runTaskTimer(QuantumRPG.getInstance(), 1L, 5L);
    }
}
