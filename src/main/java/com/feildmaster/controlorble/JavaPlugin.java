package com.feildmaster.controlorble;

import com.feildmaster.controlorble.commands.*;
import com.feildmaster.controlorble.listeners.*;
import com.feildmaster.lib.configuration.PluginWrapper;
import com.feildmaster.lib.debug.Debugger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class JavaPlugin extends PluginWrapper {
    private boolean debugerEnabled;
    private ConfigurationWrapper config;

    @Override
    public void onEnable() {
        debugerEnabled = getServer().getPluginManager().getPlugin("debuger") != null;

        // Register plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "SimpleNotice");

        // Register events
        getServer().getPluginManager().registerEvents(new OrbListener(this), this);

        checkConfig();

        // Register commands
        ExpCommand exp = new ExpCommand(this);
        getServer().getPluginCommand("exp").setExecutor(exp);
        getServer().getPluginCommand("exp-reload").setExecutor(exp);
        getServer().getPluginCommand("xp").setExecutor(exp);
        getServer().getPluginCommand("exp-set").setExecutor(new ExpSet(this));

        debug("Enable Complete");
    }

    @Override
    public void onDisable() {
        if (getConfig().isModified()) {
            debug("Saving Configuration");
            if (!getConfig().save()) {
                debug(getConfig().getLastException().getCause().getMessage());
            }
        }
    }

    @Override
    public ConfigurationWrapper getConfig() {
        if (config == null) {
            config = new ConfigurationWrapper(this);
        }
        return config;
    }

    public void checkConfig() {
        if (getConfig().needsUpdate()) {
            debug("Saving Default Config");
            saveDefaultConfig();
        }
    }

    public void debug(String message) {
        debug(null, message);
    }

    public void debug(Player player, String message) {
        if (debugerEnabled) {
            Debugger.debug(player, message, this);
        } else if (getConfig().getBoolean("config.debug")) {
            getLogger().info(message);
        }
    }

    public String format(String string) {
        return format(ChatColor.DARK_AQUA, string);
    }

    public String format(ChatColor color, String string) {
        return String.format(color + "[ControlORBle] %s", string);
    }
}
