package feildmaster.OrbEnhance;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class plugin extends JavaPlugin {
    Map<String, Integer> expBuffer = new HashMap<String, Integer>();

    public void onDisable() {
    }

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.ENTITY_DEATH, new entityListener(), Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, new playerListener(), Event.Priority.Lowest, this);
        getServer().getLogger().info(String.format("[%1$s] v%2$s Enabled!",getDescription().getName(), getDescription().getVersion()));
    }

    public class entityListener extends EntityListener {
        public void onEntityDeath(EntityDeathEvent event) {
            if(event.getEntity() instanceof Player) {
                Player p = (Player)event.getEntity();
                int exp = p.getExperience();
                if(exp > 0) {
                    expBuffer.put(p.getName(), p.getExperience());
                    p.setExperience(0);
                }
            }
        }
    }

    public class playerListener extends PlayerListener {
        public void onPlayerRespawn(PlayerRespawnEvent event) {
            Player p = event.getPlayer();
            if(expBuffer.containsKey(p.getName())) {
                p.setExperience(expBuffer.get(p.getName()));
                expBuffer.remove(p);
            }
        }
    }
}
