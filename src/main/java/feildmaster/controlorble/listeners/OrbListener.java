package feildmaster.controlorble.listeners;

import lib.feildmaster.ExpEditor.Editor;
import feildmaster.controlorble.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

public class OrbListener implements Listener {
    private final JavaPlugin Plugin;

    public OrbListener(JavaPlugin p) {
        Plugin = p;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(Plugin.expBuffer.containsKey(event.getPlayer().getName())) {
            Plugin.getServer().getScheduler().scheduleSyncDelayedTask(Plugin, new playerContainer(event.getPlayer(), Plugin.expBuffer.get(event.getPlayer().getName())), 5);
            Plugin.expBuffer.remove(event.getPlayer().getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
        if(!(cause instanceof EntityDamageByEntityEvent)) return null;

        Player p = null;
        Entity damager = ((EntityDamageByEntityEvent)cause).getDamager();

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
        // 1.0.0 Monsters
        if (entity instanceof EnderDragon) return Plugin.EnderDragon;
        if (entity instanceof Blaze) return Plugin.Blaze;
        if (entity instanceof MagmaCube) return Plugin.MagmaCube; // Before Slime
        // Monsters
        if (entity instanceof CaveSpider) return Plugin.CaveSpider; // Before Spider
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
        Editor e = new Editor(p);
        e.recalcTotalExp();
        Double loss = (Plugin.lossByTotal?e.getTotalExp():e.getExpToLevel()) *
                (calculatePercent(p.getLastDamageCause()==null?EntityDamageEvent.DamageCause.CUSTOM:p.getLastDamageCause().getCause())/100D);

        if(p.hasPermission("orbEnhance.KeepExp")) { // Keep all experience
            loss = 0D;
        }

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

        if(loss.intValue() > 0 && Plugin.expBurn > 0)
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

    private int calculatePercent(EntityDamageEvent.DamageCause dc) {
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

    public class playerContainer implements Runnable {
        private Player player;
        private int exp;

        public playerContainer(Player player, int exp) {
            this.player = player;
            this.exp = exp;
        }

        public void run() {
            new Editor(player).giveExp(exp);
        }
    }
}
