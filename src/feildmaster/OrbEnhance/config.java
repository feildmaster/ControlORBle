package feildmaster.OrbEnhance;

import org.bukkit.util.config.Configuration;

public class config {
    private static plugin Plugin;
    private static Configuration config;

    public config(plugin p) {
        Plugin = p;
        config = Plugin.getConfiguration();
        loadConfigVars();
        config.save();
    }

    public void reload() {
        config.load();
        loadConfigVars();
    }

    private void loadConfigVars() {
        Plugin.virtualExp = config.getBoolean("config.virtualEXP", Plugin.virtualExp);

        // Monsters
        Plugin.CaveSpider = config.getInt("monster.CaveSpider", Plugin.CaveSpider);
        Plugin.CaveSpider = config.getInt("monster.CaveSpider", Plugin.CaveSpider);
        Plugin.CaveSpider = config.getInt("monster.CaveSpider", Plugin.CaveSpider);
        Plugin.CaveSpider = config.getInt("monster.CaveSpider", Plugin.CaveSpider);
        Plugin.CaveSpider = config.getInt("monster.CaveSpider", Plugin.CaveSpider);
        Plugin.CaveSpider = config.getInt("monster.CaveSpider", Plugin.CaveSpider);
        Plugin.CaveSpider = config.getInt("monster.CaveSpider", Plugin.CaveSpider);
        Plugin.CaveSpider = config.getInt("monster.CaveSpider", Plugin.CaveSpider);
        Plugin.CaveSpider = config.getInt("monster.CaveSpider", Plugin.CaveSpider);
        Plugin.CaveSpider = config.getInt("monster.CaveSpider", Plugin.CaveSpider);
        Plugin.CaveSpider = config.getInt("monster.CaveSpider", Plugin.CaveSpider);
        Plugin.CaveSpider = config.getInt("monster.CaveSpider", Plugin.CaveSpider);

        // Animals
        Plugin.Chicken = config.getInt("animal.Chicken", Plugin.Chicken);
        Plugin.Chicken = config.getInt("animal.Chicken", Plugin.Chicken);
        Plugin.Chicken = config.getInt("animal.Chicken", Plugin.Chicken);
        Plugin.Chicken = config.getInt("animal.Chicken", Plugin.Chicken);
        Plugin.Chicken = config.getInt("animal.Chicken", Plugin.Chicken);
        //Plugin.TamedWolf = config.getInt("animal.TamedWolf", Plugin.TamedWolf);
    }
}
