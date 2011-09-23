package feildmaster.OrbEnhance;

import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
//import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class entityListener extends EntityListener {
    private final plugin Plugin;

    public entityListener(plugin p) {
        Plugin = p;
    }


    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        // Player gets special case
        if(event instanceof PlayerDeathEvent) {
            System.out.println("PlayerDeathEvent Detected");
            playerDeathHandler((PlayerDeathEvent)event);
        }
        // Monsters
        else if (entity instanceof CaveSpider) {
        } else if (entity instanceof Creeper) {
        } else if (entity instanceof Enderman) {
        } else if (entity instanceof Ghast) {
        } else if (entity instanceof Giant) {
        } else if (entity instanceof PigZombie) {
        } else if (entity instanceof Silverfish) {
        } else if (entity instanceof Skeleton) {
        } else if (entity instanceof Slime) {
        } else if (entity instanceof Spider) {
        } else if (entity instanceof Wolf) {
        } else if (entity instanceof Zombie) {
        }
        // Animals
        else if (entity instanceof Chicken) {
        } else if (entity instanceof Cow) {
        } else if (entity instanceof Pig) {
        } else if (entity instanceof Sheep) {
        } else if (entity instanceof Squid) {
        }
    }

    private void playerDeathHandler(PlayerDeathEvent event) {
        Player p = ((Player)event.getEntity());
        //DamageCause cause = event.getEntity().getLastDamageCause().getCause();
        Plugin.expBuffer.put(p.getName(), p.getExperience());
        // Damage cause in next version!
        //if(cause == DamageCause.BLOCK_EXPLOSION)

        event.setDroppedExp(0);
    }
}
