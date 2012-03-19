package com.feildmaster.controlorble.commands;

import com.feildmaster.controlorble.JavaPlugin;
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

        for (String key : plugin.getConfig().getKeys(false)) {
            if (set) {
                break;
            }
            ConfigurationSection section = plugin.getConfig().getConfigurationSection(key);
            if (section.contains(type)) {
                String fullKey = key+"."+type;
                Object def = plugin.getConfig().getDefaults().get(fullKey);
                plugin.debug(fullKey+": "+def);
                if (def instanceof Integer) {
                    try {
                        plugin.getConfig().set(fullKey, Integer.parseInt(value));
                        sender.sendMessage(fullKey+" set to "+plugin.getConfig().get(fullKey));
                    } catch (Exception e) {
                        sender.sendMessage("Setting value \""+key+"."+type+": "+value+"\" failed. (Not a number)");
                    } finally {
                        set = true;
                    }
                } else if (def instanceof Boolean) {
                    try {
                        plugin.getConfig().set(fullKey, Boolean.parseBoolean(value));
                        sender.sendMessage(fullKey+" set to "+plugin.getConfig().get(fullKey));
                    } catch (Exception e) {
                        sender.sendMessage("Setting value \""+key+"."+type+": "+value+"\" failed. (Not true/false)");
                    } finally {
                        set = true;
                    }
                }
            }
        }

        if (!set) {
            sender.sendMessage("Parsing \""+type+": "+value+"\" failed.");
        }

        return true;
    }

}
