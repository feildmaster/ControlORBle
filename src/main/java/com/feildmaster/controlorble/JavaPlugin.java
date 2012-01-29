package com.feildmaster.controlorble;

import com.feildmaster.controlorble.listeners.*;
import com.feildmaster.controlorble.commands.ExpCommand;
import com.feildmaster.lib.configuration.PluginWrapper;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;

public class JavaPlugin extends PluginWrapper {
    public final Map<String, Integer> expBuffer = new HashMap<String, Integer>();

    public void onEnable() {
        // Register events
        getServer().getPluginManager().registerEvents(new OrbListener(this), this);

        checkConfig();

        // Register commands
        ExpCommand exp = new ExpCommand(this);
        getServer().getPluginCommand("exp").setExecutor(exp);
        getServer().getPluginCommand("exp-reload").setExecutor(exp);
        getServer().getPluginCommand("xp").setExecutor(exp);
    }
    public void onDisable() {}

    private ConfigurationWrapper config;
    public ConfigurationWrapper getConfig() {
        if(config == null) {
            config = new ConfigurationWrapper(this);
        }
        return config;
    }

    public void checkConfig() {
        if(!getConfig().checkDefaults() || !getConfig().fileExists()) {
            saveDefaultConfig();
        }
    }

    public String format(String string) {
        return format(ChatColor.DARK_AQUA, string);
    }
    public String format(ChatColor color, String string) {
        return String.format(color+"[ControlORBle] %1$s", string);
    }
}
