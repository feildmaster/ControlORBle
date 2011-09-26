package feildmaster.OrbEnhance.listeners;

import feildmaster.OrbEnhance.plugin;
import org.bukkit.entity.Arrow;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class entityListener extends EntityListener {
    private final plugin Plugin;

    public entityListener(plugin p) {
        Plugin = p;
    }


    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0); // Set to 0, let plugin override later.

        Entity entity = event.getEntity();

        // Player gets special case
        if(event instanceof PlayerDeathEvent) {
            playerDeathHandler((PlayerDeathEvent)event);
            return;
        }

        EntityDamageEvent cause = entity.getLastDamageCause();

        if(!(cause instanceof EntityDamageByEntityEvent)) return; // Not an entity kill

        Entity damager = ((EntityDamageByEntityEvent)cause).getDamager();

        if(!(damager instanceof Arrow) && !(damager instanceof Player)) return; // Not a player kill

        Player p = null;

        // Check if arrow fired from player!
        if(damager instanceof Arrow) {
            Arrow damage_arrow = (Arrow)damager;
            if(!(damage_arrow.getShooter() instanceof Player)) return; // Not a players arrow.
            p = (Player)damage_arrow.getShooter();
        } else
            p = (Player)damager;

        // Monsters
        if (entity instanceof CaveSpider)
            monsterDeathHandler(event, p, Plugin.CaveSpider);
        else if (entity instanceof Creeper)
            monsterDeathHandler(event, p, Plugin.Creeper);
        else if (entity instanceof Enderman)
            monsterDeathHandler(event, p, Plugin.Enderman);
        else if (entity instanceof Ghast)
            monsterDeathHandler(event, p, Plugin.Ghast);
        else if (entity instanceof Giant)
            monsterDeathHandler(event, p, Plugin.Giant);
        else if (entity instanceof PigZombie)
            monsterDeathHandler(event, p, Plugin.PigZombie);
        else if (entity instanceof Silverfish)
            monsterDeathHandler(event, p, Plugin.Silverfish);
        else if (entity instanceof Skeleton)
            monsterDeathHandler(event, p, Plugin.Skeleton);
        else if (entity instanceof Slime)
            monsterDeathHandler(event, p, Plugin.Slime);
        else if (entity instanceof Spider)
            monsterDeathHandler(event, p, Plugin.Spider);
        else if (entity instanceof Wolf)
            monsterDeathHandler(event, p, ((Wolf)entity).isTamed()?Plugin.TamedWolf:Plugin.Wolf);
        else if (entity instanceof Zombie)
            monsterDeathHandler(event, p, Plugin.Zombie);
        // Animals
        else if (entity instanceof Chicken)
            monsterDeathHandler(event, p, Plugin.Chicken);
        else if (entity instanceof Cow)
            monsterDeathHandler(event, p, Plugin.Cow);
        else if (entity instanceof Pig)
            monsterDeathHandler(event, p, Plugin.Pig);
        else if (entity instanceof Sheep)
            monsterDeathHandler(event, p, Plugin.Sheep);
        else if (entity instanceof Squid)
            monsterDeathHandler(event, p, Plugin.Squid);
    }

    private void playerDeathHandler(PlayerDeathEvent event) {
        Player p = (Player)event.getEntity();
        int loss = (Plugin.playerDelevel?p.getTotalExperience():p.getExperience())*(calculatePercent(event.getEntity().getLastDamageCause().getCause())/100);
        event.setDroppedExp(loss);
        event.setNewExp((Plugin.playerDelevel?p.getTotalExperience():p.getTotalExperience()-p.getExperience())-loss);
    }

    private void monsterDeathHandler(EntityDeathEvent event, Player p, int exp) {
        if(Plugin.virtualExp) {
            p.setExperience(p.getExperience()+exp);
            p.sendMessage(String.format("You have gained %1$d experience", exp));
        } else
            event.setDroppedExp(exp);
    }
    private int calculatePercent(DamageCause dc) {
        // TODO: MultiLoss switch
        if(Plugin.multiLoss)
            switch(dc) {
                case CONTACT:

                case SUFFOCATION:

                case FIRE:
                case FIRE_TICK:

                case LAVA:

                case DROWNING:

                case BLOCK_EXPLOSION:

                case VOID:

                case LIGHTNING:

                case SUICIDE:

                case FALL:

                default:
                    return Plugin.expLoss;
            }
        else
            return Plugin.expLoss;
    }
}
