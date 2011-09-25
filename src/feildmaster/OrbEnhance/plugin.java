package feildmaster.OrbEnhance;

import feildmaster.OrbEnhance.listeners.entityListener;
import feildmaster.OrbEnhance.commands.ExpCommand;
import feildmaster.OrbEnhance.commands.ExpReloadCommand;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class plugin extends JavaPlugin {
    public config Config;
//    public Map<String, Integer> expBuffer = new HashMap<String, Integer>();

    // Other variables
    public boolean virtualExp = true;
    public int expLoss = 0; // % of experience lost on death

    // Monster variables
    public int CaveSpider = 10;
    public int Creeper = 10;
    public int Enderman = 10;
    public int Ghast = 10;
    public int Giant = 10;
    public int PigZombie = 10;
    public int Silverfish = 10;
    public int Skeleton = 10;
    public int Slime = 10;
    public int Spider = 10;
    public int Wolf = 10;
    public int Zombie = 10;

    // Animal variables
    public int Chicken = 1;
    public int Cow = 1;
    public int Pig = 1;
    public int Sheep = 1;
    public int Squid = 1;
    public int TamedWolf = 0;

    public void onDisable() {
        getServer().getLogger().info(String.format("[%1$s] v%2$s Disabled!",getDescription().getName(), getDescription().getVersion()));
    }

    public void onEnable() {
        // Register events
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, new entityListener(this), Event.Priority.Low, this);

        // Register commands
        getServer().getPluginCommand("exp").setExecutor(new ExpCommand(this));
        getServer().getPluginCommand("exp-reload").setExecutor(new ExpReloadCommand(this));

        // Load Config
        Config = new config(this);

        // All done
        getServer().getLogger().info(String.format("[%1$s] v%2$s Enabled!",getDescription().getName(), getDescription().getVersion()));
    }


    public String format(String string) {
        return format(ChatColor.DARK_AQUA, string);
    }
    public String format(ChatColor color, String string) {
        return String.format(color+"[ControlORBle] %1$s", string);
    }

//    public class playerListener extends PlayerListener implements Runnable {
//        private plugin Plugin;
//        private Player player;
//
//        public playerListener(plugin p) {
//            Plugin = p;
//        }
//
//        public void onPlayerRespawn(PlayerRespawnEvent event) {
//            player = event.getPlayer();
//
//            getServer().broadcastMessage("Experience: "+event.getPlayer().getExperience());
//
//            if(expBuffer.containsKey(player.getName())) {
//                getServer().getScheduler().scheduleSyncDelayedTask(Plugin, this, 20);
//            } else
//                player = null;
//        }
//
//        public void run() {
//            player.setExperience(expBuffer.get(player.getName()));
//            expBuffer.remove(player.getName());
//
//            getServer().broadcastMessage("New Experience: "+player.getExperience());
//        }
//    }
}
