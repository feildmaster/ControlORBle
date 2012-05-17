package com.feildmaster.controlorble.commands;

import com.feildmaster.controlorble.JavaPlugin;
import com.feildmaster.lib.configuration.EnhancedConfiguration;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;

public class ExpSet implements CommandExecutor {
    private final JavaPlugin plugin;

    public ExpSet(JavaPlugin instance) {
        plugin = instance;
        plugin.debug("Registering /exp-set commands");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("orbEnhance.modifyExp")) {
            sender.sendMessage("Missing Permissions");
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage("Usage: /exp-set {type} {value}");
            return true;
        }

        String type = args[0];
        String value = args[1];
        boolean set = false;
        EnhancedConfiguration config = plugin.getConfig();

        for (String key : config.getKeys(false)) {
            Object o = config.get(key);

            if (!(o instanceof ConfigurationSection)) {
                continue;
            }

            ConfigurationSection section = (ConfigurationSection) o;

            if (section.contains(type)) {
                String fullKey = key+"."+type;
                Object def = config.getDefaults().get(fullKey);
                boolean Int = def instanceof Integer;
                boolean Bool = def instanceof Boolean;
                if (!Int && !Bool) {
                    continue;
                }
                try {
                    config.set(type, (Int ? Integer.parseInt(value) : Boolean.parseBoolean(value)));
                    config.save();
                    sender.sendMessage(fullKey+" set to "+section.get(type));
                } catch (Exception e) {
                    sender.sendMessage("Setting value \""+fullKey+": "+value+"\" failed. (Not " + (Int ? "a number)" : "true/false)"));
                } finally {
                    set = true;
                    break;
                }
            }
        }

        if (!set) {
            sender.sendMessage("Parsing \""+type+": "+value+"\" failed.");
        }

        return true;
    }

}
