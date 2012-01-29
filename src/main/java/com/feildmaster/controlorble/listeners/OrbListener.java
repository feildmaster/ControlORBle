package com.feildmaster.controlorble.listeners;

import com.feildmaster.lib.expeditor.Editor;
import com.feildmaster.controlorble.*;
import com.feildmaster.controlorble.event.PlayerBreakBlockDropOrbEvent;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

public class OrbListener implements Listener {
    private final JavaPlugin plugin;

    public OrbListener(JavaPlugin p) {
        plugin = p;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(plugin.expBuffer.containsKey(event.getPlayer().getName())) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new playerContainer(event.getPlayer(), plugin.expBuffer.get(event.getPlayer().getName())), 5);
            plugin.expBuffer.remove(event.getPlayer().getName());
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
        if (entity instanceof EnderDragon) return plugin.getConfig().getExp("monster.EnderDragon");
        if (entity instanceof Blaze) return plugin.getConfig().getExp("monster.Blaze");
        if (entity instanceof MagmaCube) return plugin.getConfig().getExp("monster.MagmaCube"); // Before Slime
        // Monsters
        if (entity instanceof CaveSpider) return plugin.getConfig().getExp("monster.CaveSpider"); // Before Spider
        if (entity instanceof Creeper) return plugin.getConfig().getExp("monster.Creeper");
        if (entity instanceof Enderman) return plugin.getConfig().getExp("monster.Enderman");
        if (entity instanceof Ghast) return plugin.getConfig().getExp("monster.Ghast");
        if (entity instanceof Giant) return plugin.getConfig().getExp("monster.Giant");
        if (entity instanceof PigZombie) return plugin.getConfig().getExp("monster.PigZombie");
        if (entity instanceof Silverfish) return plugin.getConfig().getExp("monster.Silverfish");
        if (entity instanceof Skeleton) return plugin.getConfig().getExp("monster.Skeleton");
        if (entity instanceof Slime) return plugin.getConfig().getExp("monster.Slime");
        if (entity instanceof Spider) return plugin.getConfig().getExp("monster.Spider");
        if (entity instanceof Wolf) return plugin.getConfig().getExp(((Wolf)entity).isTamed()?"animal.Wolf":"monster.Wolf");
        if (entity instanceof Zombie) return plugin.getConfig().getExp("monster.Zombie");
        // Animals
        if (entity instanceof Chicken) return plugin.getConfig().getExp("animal.Chicken");
        if (entity instanceof Cow) return plugin.getConfig().getExp("animal.Cow");
        if (entity instanceof Pig) return plugin.getConfig().getExp("animal.Pig");
        if (entity instanceof Sheep) return plugin.getConfig().getExp("animal.Sheep");
        if (entity instanceof Squid) return plugin.getConfig().getExp("animal.Squid");

        return 0;
    }

    private void playerDeathHandler(PlayerDeathEvent event) {
        Player p = (Player)event.getEntity();
        Editor e = new Editor(p);
        e.recalcTotalExp();
        Double loss = (plugin.getConfig().getBoolean("config.expLossByTotal")?e.getTotalExp():e.getExpToLevel()) *
                (calculatePercent(p.getLastDamageCause()==null?EntityDamageEvent.DamageCause.CUSTOM:p.getLastDamageCause().getCause())/100D);

        if(p.hasPermission("orbEnhance.KeepExp")) { // Keep all experience
            loss = 0D;
        }

        if(!plugin.getConfig().getBoolean("config.playerDelevel") && loss > e.getExp()) {
            loss = (double) e.getExp();
        }

        if(e.getTotalExp() > loss.intValue()) {
            //event.setNewExp(e.getTotalExp()-loss.intValue());
            plugin.expBuffer.put(p.getName(), e.getTotalExp()-loss.intValue());
        } else {
            if (event.getNewExp() != 0) event.setNewExp(0);
            loss = (double) e.getTotalExp();
        }

        if(loss.intValue() > 0 && plugin.getConfig().getPercent("expBurn") > 0)
            loss -= loss * (plugin.getConfig().getPercent("expBurn")/100D) ;

        if(loss.intValue() > 0) {
            if(plugin.getConfig().getBoolean("config.virtualPlayerEXP")) {
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
        if (plugin.getConfig().getBoolean("config.virtualEXP")) {
            p.giveExp(exp);
            p.sendMessage(gainMessage(exp));
        } else {
            event.setDroppedExp(exp);
        }
    }

    private String gainMessage(int exp) {
        return "You have gained "+exp+" experience";
    }

    private int calculatePercent(EntityDamageEvent.DamageCause dc) {
        if(plugin.getConfig().getBoolean("config.customExpLoss")) {
            switch(dc) {
                // TODO: Suffocation and Fall percent
                //case SUFFOCATION: return Plugin.expLossSuffocate;
                //case FALL: return Plugin.expLossFall;

                case CONTACT: return plugin.getConfig().getPercent("expLoss.Contact");

                case FIRE:
                case FIRE_TICK: return plugin.getConfig().getPercent("expLoss.Fire");

                case LAVA: return plugin.getConfig().getPercent("expLoss.Lava");

                case DROWNING: return plugin.getConfig().getPercent("expLoss.Drown");

                case BLOCK_EXPLOSION: return plugin.getConfig().getPercent("expLoss.TnT");

                case VOID: return plugin.getConfig().getPercent("expLoss.Void");

                case LIGHTNING: return plugin.getConfig().getPercent("expLoss.Lightning");

                case SUICIDE: return plugin.getConfig().getPercent("expLoss.Suicide");

                default: return plugin.getConfig().getPercent("expLoss.Basic");
            }
        } else {
            return plugin.getConfig().getPercent("expLoss.Basic");
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

    @EventHandler(priority=EventPriority.MONITOR)
    public void blockBreak(BlockBreakEvent event) {
        String key;
        if(plugin.getConfig().getBoolean("customBlockExp")) {
            key = "blockExp."+event.getBlock().getType().toString();
        } else {
            key = "blockExp.Basic";
        }
        Object o = plugin.getConfig().get(key);
        if (o instanceof Integer) {
            int exp = plugin.getConfig().getExp(key);
            PlayerBreakBlockDropOrbEvent e = new PlayerBreakBlockDropOrbEvent(event.getPlayer(), event.getBlock(), exp);
            plugin.getServer().getPluginManager().callEvent(e);
            ExperienceOrb orb = event.getBlock().getWorld().spawn(event.getBlock().getLocation(), ExperienceOrb.class);
            orb.setExperience(e.getExp());
        }
    }
}
