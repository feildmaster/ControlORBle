package feildmaster.controlorble.listeners;

import feildmaster.controlorble.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class entityListener extends EntityListener {
    private final JavaPlugin Plugin;

    public entityListener(JavaPlugin p) {
        Plugin = p;
    }

    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0); // Set to 0, let JavaPlugin override later.

        // Player gets special case
        if(event instanceof PlayerDeathEvent) {
            playerDeathHandler((PlayerDeathEvent)event);
            return;
        }

        Entity entity = event.getEntity();
        Player p = getPlayer(entity.getLastDamageCause());

        if(p == null) return; // Not a player kill

        monsterDeathHandler(event, p, getExp(entity));
    }

    private Player getPlayer(EntityDamageEvent cause) {
        Player p = null;
        Entity damager = null;

        if(cause instanceof EntityDamageByEntityEvent) {
            damager = ((EntityDamageByEntityEvent)cause).getDamager();
        } else {
            return null;
        }

        if(damager instanceof Arrow) {
            Arrow damage_arrow = (Arrow)damager;
            if(damage_arrow.getShooter() instanceof Player) {
                p = (Player)damage_arrow.getShooter();
            }
        } else if(damager instanceof Player) {
            p = (Player)damager;
        }

        return p;
    }

    private int getExp(Entity entity) {
        // Monsters
        if (entity instanceof CaveSpider) return Plugin.CaveSpider;
        if (entity instanceof Creeper) return Plugin.Creeper;
        if (entity instanceof Enderman) return Plugin.Enderman;
        if (entity instanceof Ghast) return Plugin.Ghast;
        if (entity instanceof Giant) return Plugin.Giant;
        if (entity instanceof PigZombie) return Plugin.PigZombie;
        if (entity instanceof Silverfish) return Plugin.Silverfish;
        if (entity instanceof Skeleton) return Plugin.Skeleton;
        if (entity instanceof Slime) return Plugin.Slime;
        if (entity instanceof Spider) return Plugin.Spider;
        if (entity instanceof Wolf) return ((Wolf)entity).isTamed()?Plugin.TamedWolf:Plugin.Wolf;
        if (entity instanceof Zombie) return Plugin.Zombie;
        // 1.0.0 Monsters
        if (entity instanceof EnderDragon) return Plugin.EnderDragon;
        if (entity instanceof Blaze) return Plugin.Blaze;
        if (entity instanceof MagmaCube) return Plugin.MagmaCube;
        // Animals
        if (entity instanceof Chicken) return Plugin.Chicken;
        if (entity instanceof Cow) return Plugin.Cow;
        if (entity instanceof Pig) return Plugin.Pig;
        if (entity instanceof Sheep) return Plugin.Sheep;
        if (entity instanceof Squid) return Plugin.Squid;

        return 0;
    }

    private void playerDeathHandler(PlayerDeathEvent event) {
        Player p = (Player)event.getEntity();
        ExpEditor e = new ExpEditor(p);
        Double loss = (Plugin.lossByTotal?e.getTotalExp():(e.getLevel()*10+10))*(calculatePercent(p.getLastDamageCause()==null?DamageCause.CUSTOM:p.getLastDamageCause().getCause())/100D);

        if(!Plugin.playerDelevel && loss > e.getExp()) {
            loss = (double) e.getExp();
        }

        if(e.getTotalExp() > loss.intValue()) {
            //event.setNewExp(e.getTotalExp()-loss.intValue());
            Plugin.expBuffer.put(p.getName(), e.getTotalExp()-loss.intValue());
        } else {
            if (event.getNewExp() != 0) event.setNewExp(0);
            loss = (double) e.getTotalExp();
        }

        if(Plugin.expBurn > 0)
            loss -= loss * (Plugin.expBurn/100D) ;

        if(loss.intValue() > 0) {
            if(Plugin.virtualPlayerExp) {
                p.sendMessage("You have lost "+loss.intValue()+" experience");

                Player killer = getPlayer(p.getLastDamageCause());
                if(killer != null) {
                    killer.sendMessage(gainMessage(loss.intValue()));
                }
            } else {
                event.setDroppedExp(loss.intValue());
            }
        }
    }

    private void monsterDeathHandler(EntityDeathEvent event, Player p, int exp) {
        if(Plugin.virtualExp) {
            p.giveExp(exp);
            p.sendMessage(gainMessage(exp));
        } else
            event.setDroppedExp(exp);
    }

    private String gainMessage(int exp) {
        return "You have gained "+exp+" experience";
    }

    private int calculatePercent(DamageCause dc) {
        if(Plugin.multiLoss) {
            switch(dc) {
                // TODO: Suffocation and Fall percent
                //case SUFFOCATION: return Plugin.expLossSuffocate;
                //case FALL: return Plugin.expLossFall;

                case CONTACT: return Plugin.expLossContact;

                case FIRE:
                case FIRE_TICK: return Plugin.expLossFire;

                case LAVA: return Plugin.expLossLava;

                case DROWNING: return Plugin.expLossDrown;

                case BLOCK_EXPLOSION: return Plugin.expLossTnT;

                case VOID: return Plugin.expLossVoid;

                case LIGHTNING: return Plugin.expLossLightning;

                case SUICIDE: return Plugin.expLossSuicide;

                default: return Plugin.expLoss;
            }
        } else {
            return Plugin.expLoss;
        }
    }
}
