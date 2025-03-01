package su.nightexpress.quantumrpg.modules.list.itemgenerator.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quantumrpg.Perms;
import su.nightexpress.quantumrpg.modules.api.QModuleDrop;
import su.nightexpress.quantumrpg.modules.command.MCmd;
import su.nightexpress.quantumrpg.modules.list.itemgenerator.ItemGeneratorManager;
import su.nightexpress.quantumrpg.modules.list.itemgenerator.editor.EditorGUI;

import java.util.List;

public class EditCommand extends MCmd<ItemGeneratorManager> {

    public EditCommand(@NotNull ItemGeneratorManager module) {
        super(module, new String[] {"edit"}, Perms.ADMIN);
    }

    @NotNull
    @Override
    public String usage() { return "<id>"; }

    @NotNull
    @Override
    public String description() { return plugin.lang().ItemGenerator_Cmd_Editor_Desc.getMsg(); }

    @Override
    public boolean playersOnly() { return true; }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            List<String> ids = this.module.getItemIds();
            ids.remove(QModuleDrop.RANDOM_ID);
            return ids;
        }
        return super.getTab(player, i, args);
    }

    @Override
    protected void perform(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if (strings.length != 2) {
            this.printUsage(commandSender);
            return;
        }
        ItemGeneratorManager.GeneratorItem itemGenerator = module.getItemById(strings[1]);
        if (itemGenerator == null) {
            plugin.lang().ItemGenerator_Cmd_Editor_Error_InvalidItem.send(commandSender);
            return;
        }
        try {
            new EditorGUI(module, itemGenerator).open((Player) commandSender, 1);
        } catch (IllegalStateException e) {
            plugin.lang().ItemGenerator_Cmd_Editor_Error_AlreadyOpen.replace("%player%", commandSender.getName()).send(commandSender);
        }
    }
}
