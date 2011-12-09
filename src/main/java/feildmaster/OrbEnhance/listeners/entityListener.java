package feildmaster.OrbEnhance.listeners;

import feildmaster.OrbEnhance.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

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

        Player p;

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
        // 1.0.0 patch
        else if (entity instanceof EnderDragon)
            monsterDeathHandler(event, p, Plugin.EnderDragon);
        else if (entity instanceof Blaze)
            monsterDeathHandler(event, p, Plugin.Blaze);
        else if (entity instanceof MagmaCube)
            monsterDeathHandler(event, p, Plugin.MagmaCube);
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
        ExpEditor e = new ExpEditor(p);
        Double loss = (Plugin.lossByTotal?e.getTotalExp():(e.getLevel()*10+10))*(calculatePercent(p.getLastDamageCause()==null?DamageCause.CUSTOM:p.getLastDamageCause().getCause())/100D);

        if(!Plugin.playerDelevel && loss > e.getExp())
            loss = (double) e.getExp();

        if(e.getTotalExp() > loss.intValue())
            event.setNewExp(e.getTotalExp()-loss.intValue());
        else {
            if (event.getNewExp() != 0) event.setNewExp(0);
            loss = (double) e.getTotalExp();
        }

        if(Plugin.expBurn > 0)
            loss -= loss * (Plugin.expBurn/100D) ;

        if(loss.intValue() > 0)
            event.setDroppedExp(loss.intValue());
    }

    private void monsterDeathHandler(EntityDeathEvent event, Player p, int exp) {
        if(Plugin.virtualExp) {
            p.setExperience(p.getExperience()+exp);
            p.sendMessage(String.format("You have gained %1$d experience", exp));
        } else
            event.setDroppedExp(exp);
    }
    private int calculatePercent(DamageCause dc) {
        if(Plugin.multiLoss)
            switch(dc) {
                //case SUFFOCATION: // TODO: Suffocation percent
                //case FALL: // TODO: Fall percent

                case CONTACT:
                    return Plugin.expLossContact;

                case FIRE:
                case FIRE_TICK:
                    return Plugin.expLossFire;

                case LAVA:
                    return Plugin.expLossLava;

                case DROWNING:
                    return Plugin.expLossDrown;

                case BLOCK_EXPLOSION:
                    return Plugin.expLossTnT;

                case VOID:
                    return Plugin.expLossVoid;

                case LIGHTNING:
                    return Plugin.expLossLightning;

                case SUICIDE:
                    return Plugin.expLossSuicide;

                default:
                    return Plugin.expLoss;
            }
        else
            return Plugin.expLoss;
    }
}
