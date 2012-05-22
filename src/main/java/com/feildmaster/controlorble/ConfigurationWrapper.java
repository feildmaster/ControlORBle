package com.feildmaster.controlorble;

import com.feildmaster.lib.configuration.EnhancedConfiguration;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public class ConfigurationWrapper extends EnhancedConfiguration {
    public ConfigurationWrapper(Plugin plugin) {
        super(plugin);
        loadDefaults(); // Load the defaults. :D
        populateDefaultBlocks();
        // Update Config - Remove in 3.0
        updateBlockValues(); // This updates old configuration to the new configuration
        if(get("blockExp.Basic", "") != "") {
            unset("blockExp.Basic");
        }
        if(get("animal.Wolf", "") != "") {
            set("animal.tameWolf", get("animal.Wolf"));
            unset("animal.Wolf");
        }
    }

    public int getPercent(String node) {
        int def = getDefaults().getInt(node);
        int value = getInt(node, -1);
        if(value < 0 || value > 100) {
            set(node, def);
            value = def;
        }
        return value;
    }

    public int getMultiplier(String type) {
        int i = getInt("multipliers."+type);
        if (i < 0) i = 1;
        return i;
    }

    public int getExp(String node) {
        int def = getDefaults().getInt(node);
        int value = getInt(node, -2000);
        if(value < -1000) {
            set(node, def);
            value = def;
        }
        return value;
    }

    private void populateDefaultBlocks() {
        for(Material m : Material.values()) {
            if(m.isBlock() && m != Material.AIR) {
                String key = "blockExp." + m.toString();
                getDefaults().set(key + ".place", 0);
                getDefaults().set(key + ".break", 0);
            }
        }
    }

    // This function updates old values to new config
    private void updateBlockValues() {
        ConfigurationSection section = this.getConfigurationSection("blockExp");
        for (String key : section.getKeys(false)) {
            if (section.isConfigurationSection(key)) {
                continue;
            }
            this.set("blockExp." + key + ".break", section.get(key));
        }
    }
}
