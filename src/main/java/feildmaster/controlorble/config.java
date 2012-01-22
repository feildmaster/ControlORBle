package feildmaster.controlorble;

import org.bukkit.util.config.Configuration;

public class config {
    private static JavaPlugin Plugin;
    private static Configuration config;

    public config(JavaPlugin p) {
        Plugin = p;
        config = Plugin.getConfiguration();
        loadConfigVars();
        config.save();
    }

    public void reload() {
        config.load();
        loadConfigVars();
        config.save(); // For the... just in case moments.
    }

    private void loadConfigVars() {
        config.setHeader("# Read about the configuration here:",
                "# http://dev.bukkit.org/server-mods/controlorble/pages/configuration/");

        // Config Variables
        Plugin.virtualExp = config.getBoolean("config.virtualEXP", false);
        Plugin.virtualPlayerExp = config.getBoolean("config.virtualPlayerEXP", false);
        Plugin.showTotal = config.getBoolean("config.showTotal", false);
        Plugin.lossByTotal = config.getBoolean("config.expLossByTotal", false);
        Plugin.playerDelevel = config.getBoolean("config.playerDelevel", true);
        Plugin.expBurn = getPercent("config.expBurn", 0);
        Plugin.multiLoss = config.getBoolean("config.customExpLoss", false);

        // Player experience variables
        Plugin.expLoss = getPercent("expLoss.Basic", 0);
        Plugin.expLossContact = getPercent("expLoss.Contact", 0);
        Plugin.expLossDrown = getPercent("expLoss.Drown", 0);
        Plugin.expLossFire = getPercent("expLoss.Fire", 0);
        Plugin.expLossLava = getPercent("expLoss.Lava", 0);
        Plugin.expLossLightning = getPercent("expLoss.Lightning", 0);
        Plugin.expLossStarve = getPercent("expLoss.Starve", 0);
        Plugin.expLossSuicide = getPercent("expLoss.Suicide", 0);
        Plugin.expLossTnT = getPercent("expLoss.TnT", 0);
        Plugin.expLossVoid = getPercent("expLoss.Void", 0);

        // Monsters
        Plugin.CaveSpider = getExp("monster.CaveSpider", 10);
        Plugin.Creeper = getExp("monster.Creeper", 10);
        Plugin.Enderman = getExp("monster.Enderman", 10);
        Plugin.Ghast = getExp("monster.Ghast", 10);
        Plugin.Giant = getExp("monster.Giant", 10);
        Plugin.PigZombie = getExp("monster.PigZombie", 10);
        Plugin.Silverfish = getExp("monster.Silverfish", 10);
        Plugin.Skeleton = getExp("monster.Skeleton", 10);
        Plugin.Slime = getExp("monster.Slime", 10);
        Plugin.Spider = getExp("monster.Spider", 10);
        Plugin.Wolf = getExp("monster.Wolf", 10);
        Plugin.Zombie = getExp("monster.Zombie", 10);
        
        // 1.0.0 Patch
        Plugin.EnderDragon = getExp("monster.EnderDragon", 20000);
        Plugin.Blaze = getExp("monster.Blaze", 10);
        Plugin.MagmaCube = getExp("monster.MagmaCube", 10);

        // Animals
        Plugin.Chicken = getExp("animal.Chicken", 1);
        Plugin.Cow = getExp("animal.Cow", 1);
        Plugin.Pig = getExp("animal.Pig", 1);
        Plugin.Sheep = getExp("animal.Sheep", 1);
        Plugin.Squid = getExp("animal.Squid", 1);
        Plugin.TamedWolf = getExp("animal.Wolf", 0);
    }

    private int getPercent(String node, int def) {
        int value = config.getInt(node, def);
        if(value < 0 || value > 100) value = setProperty(node, def);
        return value;
    }

    private int getExp(String node, int def) {
        int value = config.getInt(node, def);
        if(value < -1000) value = setProperty(node, def);
        return value;
    }

    private int setProperty(String node, int value) {
        config.setProperty(node, value);
        return value;
    }
}
