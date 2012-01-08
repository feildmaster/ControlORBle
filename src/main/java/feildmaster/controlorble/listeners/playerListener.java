package feildmaster.controlorble.listeners;

import feildmaster.controlorble.ExpEditor;
import feildmaster.controlorble.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class playerListener extends PlayerListener {
    private JavaPlugin Plugin;

    public playerListener(JavaPlugin plugin) {
        Plugin = plugin;
    }

    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(Plugin.expBuffer.containsKey(event.getPlayer().getName())) {
            Plugin.getServer().getScheduler().scheduleSyncDelayedTask(Plugin, new playerContainer(event.getPlayer(), Plugin.expBuffer.get(event.getPlayer().getName())), 5);
            Plugin.expBuffer.remove(event.getPlayer().getName());
        }
    }

    public class playerContainer implements Runnable {
        private Player player;
        private int exp;

        public playerContainer(Player player, int exp) {
            this.player = player;
            this.exp = exp;
        }

        public void run() {
            new ExpEditor(player).giveExp(exp);
        }
    }
}