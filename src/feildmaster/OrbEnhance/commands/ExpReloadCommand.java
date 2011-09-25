package feildmaster.OrbEnhance.commands;

import feildmaster.OrbEnhance.plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ExpReloadCommand implements CommandExecutor {
    private plugin Plugin;

    public ExpReloadCommand(plugin p) {
        Plugin = p;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Plugin.Config.reload();
        sender.sendMessage(Plugin.format("Reload Complete"));
        return true;
    }

}
