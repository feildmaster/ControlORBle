package com.feildmaster.controlorble;

import com.feildmaster.lib.configuration.EnhancedConfiguration;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

public class ConfigurationWrapper extends EnhancedConfiguration {
    public ConfigurationWrapper(Plugin plugin) {
        super(plugin);
        loadDefaults(); // Load the defaults. :D
        populateBlocks();

        if(get("blockExp.Basic", "") != "") { // Blah fix for now.
            set("blockExp.Basic", null);
        }

        if(get("animal.Wolf", "") != "") { // Blah fix for now.
            set("animal.tameWolf", get("animal.Wolf"));
            set("animal.Wolf", null);
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

    private void populateBlocks() {
        for(Material m : Material.values()) {
            if(m.isBlock() && m != Material.AIR) {
                getDefaults().set("blockExp."+m.toString(), 0);
            }
        }
    }
}
