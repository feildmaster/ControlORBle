package com.feildmaster.controlorble.listeners;

import com.feildmaster.lib.expeditor.Editor;
import com.feildmaster.controlorble.*;
import com.feildmaster.controlorble.event.PlayerBreakBlockDropOrbEvent;
import java.util.List;
import java.util.concurrent.Callable;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.metadata.*;

// TODO: Clean "plugin.getConfig().getBoolean("config.hideVirtualEXPMessage")" into a common function
// experienceMessage(player, amount)
public class OrbListener implements Listener {
    private final JavaPlugin plugin;
    private final LazyMetadataValue noExp;

    public OrbListener(JavaPlugin p) {
        plugin = p;
        plugin.debug("Registering Events");
        noExp = new FixedMetadataValue(plugin, true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (!plugin.getConfig().getBoolean("stopMonsterEXP.monsterSpawner")) {
            return;
        }
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            plugin.debug("Enitity "+event.getEntityType()+"["+event.getEntity().getUniqueId()+"], will not drop experience when killed.");
            event.getEntity().setMetadata("noExp", noExp);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        int i = getPlayerExp(event.getPlayer().getMetadata("expBuffer"));
        if (i != -10) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new playerContainer(event.getPlayer(), i), 5);
        }
    }

    private int getPlayerExp(List<MetadataValue> list) {
        for (MetadataValue meta : list) {
            if (!meta.getOwningPlugin().equals(plugin)) {
                continue;
            } else if (!(meta.value() instanceof Integer)) {
                continue;
            }
            int i = meta.asInt();
            meta.invalidate();
            return i;
        }

        return -10;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0); // Set to 0, let plugin override later.

        // Player gets special case
        if(event instanceof PlayerDeathEvent) {
            plugin.debug("Player Death");
            playerDeathHandler((PlayerDeathEvent) event);
            return;
        }

        Entity entity = event.getEntity();
        Player p = getPlayer(entity.getLastDamageCause());

        if(p == null) { // Not a player kill
            plugin.debug("Not a player kill");
            return;
        }

        if (entity.hasMetadata("noExp") && containsMeta(entity.getMetadata("noExp"), noExp)) {
            plugin.debug("Monster has noExp flag!");
            return;
        }

        monsterDeathHandler(event, p, getExp(entity));
    }

    private boolean containsMeta(List<MetadataValue> list, MetadataValue def) {
        if (list.contains(def)) {
            return true;
        }
        for (MetadataValue meta : list) {
            if (!(meta instanceof FixedMetadataValue)) {
                continue;
            } else if (!meta.getOwningPlugin().equals(plugin)) {
                continue;
            } else if (!meta.value().equals(def.value())) {
                continue;
            }
            return true;
        }

        return false;
    }

    private Player getPlayer(EntityDamageEvent cause) {
        if(cause == null) {
            plugin.debug("EntityDamageEvent is null!");
            return null;
        }
        if(!(cause instanceof EntityDamageByEntityEvent)) {
            plugin.debug("EntityDamageEvent isn't by Entity.");
            return null;
        }

        Player p = null;
        Entity damager = ((EntityDamageByEntityEvent) cause).getDamager();

        if(damager instanceof Arrow) {
            Arrow damage_arrow = (Arrow) damager;
            if(damage_arrow.getShooter() instanceof Player) {
                p = (Player) damage_arrow.getShooter();
                plugin.debug("Arrow Killed Entity (by "+p+")");
            } else {
                plugin.debug("Arrow Killed Entity");
            }
        } else if(damager instanceof Player) {
            plugin.debug("Entity Killed By Player");
            p = (Player)damager;
        }

        return p;
    }

    private int getExp(Entity entity) {
        plugin.debug("Getting Experience from "+entity+" death");
        // 1.2 Creatures
        if (entity instanceof IronGolem) {
            return plugin.getConfig().getExp("animal.IronGolem");
        }
        if (entity instanceof Ocelot) {
            Ocelot o = (Ocelot) entity;
            boolean baby = o.isAdult();
            if (o.isTamed()) {
                if (baby) {
                    return plugin.getConfig().getExp("animal.OcelotTameBaby");
                } else {
                    return plugin.getConfig().getExp("animal.OcelotTame");
                }
            } else {
                if (baby) {
                    return plugin.getConfig().getExp("animal.OcelotBaby");
                } else {
                    return plugin.getConfig().getExp("animal.Ocelot");
                }
            }
        }
        // I forgot snowman!
        if (entity instanceof Snowman) return plugin.getConfig().getExp("animal.Snowman");
        // 1.0.0 Monsters
        if (entity instanceof EnderDragon || entity instanceof ComplexEntityPart) return plugin.getConfig().getExp("monster.EnderDragon");
        if (entity instanceof Blaze) return plugin.getConfig().getExp("monster.Blaze");
        // Monsters
        if (entity instanceof Creeper) return plugin.getConfig().getExp("monster.Creeper");
        if (entity instanceof Enderman) return plugin.getConfig().getExp("monster.Enderman");
        if (entity instanceof Ghast) return plugin.getConfig().getExp("monster.Ghast");
        if (entity instanceof Giant) return plugin.getConfig().getExp("monster.Giant");
        if (entity instanceof PigZombie) return plugin.getConfig().getExp("monster.PigZombie");
        if (entity instanceof Silverfish) return plugin.getConfig().getExp("monster.Silverfish");
        if (entity instanceof Skeleton) return plugin.getConfig().getExp("monster.Skeleton");
        if (entity instanceof Slime) {
            if (entity instanceof MagmaCube) { // Before Slime
                return plugin.getConfig().getExp("monster.MagmaCube");
            } else {
                return plugin.getConfig().getExp("monster.Slime");
            }
        }
        if (entity instanceof Spider) {
            if (entity instanceof CaveSpider) { // Before Spider
                return plugin.getConfig().getExp("monster.CaveSpider");
            } else {
                return plugin.getConfig().getExp("monster.Spider");
            }
        }
        if (entity instanceof Zombie) return plugin.getConfig().getExp("monster.Zombie");
        // Animals
        if (entity instanceof Wolf) return plugin.getConfig().getExp(((Wolf) entity).isTamed()?"animal.tameWolf":"monster.Wolf");
        if (entity instanceof Chicken) return plugin.getConfig().getExp("animal.Chicken");
        if (entity instanceof Cow) return plugin.getConfig().getExp("animal.Cow");
        if (entity instanceof Pig) return plugin.getConfig().getExp("animal.Pig");
        if (entity instanceof Sheep) return plugin.getConfig().getExp("animal.Sheep");
        if (entity instanceof Squid) return plugin.getConfig().getExp("animal.Squid");

        plugin.debug("\""+entity+"\" not found. 0 Experience.");
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
            final int exp = e.getTotalExp()-loss.intValue();
            //event.setNewExp(e.getTotalExp()-loss.intValue());
            p.setMetadata("expBuffer", new LazyMetadataValue(plugin, new Callable<Object>() {
                public Object call() throws Exception {
                    return exp;
                }
            }));
        } else {
            if (event.getNewExp() != 0) event.setNewExp(0);
            loss = (double) e.getTotalExp();
        }

        if(loss.intValue() > 0 && plugin.getConfig().getPercent("expBurn") > 0)
            loss -= loss * (plugin.getConfig().getPercent("expBurn")/100D) ;

        if(loss.intValue() > 0) {
            if(plugin.getConfig().getBoolean("config.virtualPlayerEXP")) {
                if(!plugin.getConfig().getBoolean("config.hideVirtualEXPMessage")) {
                    p.sendMessage("You have lost "+loss.intValue()+" experience");
                }

                Player killer = getPlayer(p.getLastDamageCause());
                if(killer != null && !plugin.getConfig().getBoolean("config.hideVirtualEXPMessage")) {
                    killer.sendMessage(gainMessage(loss.intValue()));
                }
            } else {
                event.setDroppedExp(loss.intValue());
            }
        }
    }

    private void monsterDeathHandler(EntityDeathEvent event, Player p, int exp) {
        plugin.debug(event.getEntity() + " ["+ event.getEntity() + "]" + " dropped "+exp);

        if(exp == 0) return;
        exp *= plugin.getConfig().getMultiplier("monsterEXP");
        if (plugin.getConfig().getBoolean("config.virtualEXP")) {
            p.giveExp(exp);
            if(!plugin.getConfig().getBoolean("config.hideVirtualEXPMessage")) {
                p.sendMessage(gainMessage(exp));
            }
        } else {
            event.setDroppedExp(exp);
        }
    }

    private String gainMessage(Number exp) {
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

            plugin.debug(player+" will respawn with "+exp+" exp");
        }

        public void run() {
            plugin.debug("Giving "+player+" "+exp+" exp");
            new Editor(player).giveExp(exp);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void blockBreak(BlockBreakEvent event) {
        String key = "blockExp."+event.getBlock().getType().toString();
        Object o = plugin.getConfig().get(key);
        if (o instanceof Integer) {
            int exp = plugin.getConfig().getExp(key);
            if(exp < 0) exp = 0;
            PlayerBreakBlockDropOrbEvent e = new PlayerBreakBlockDropOrbEvent(event.getPlayer(), event.getBlock(), exp);
            plugin.getServer().getPluginManager().callEvent(e);
            if(e.getExp() < 1) return;
            if (plugin.getConfig().getBoolean("config.virtualBlockEXP")) {
                event.getPlayer().giveExp(e.getExp());
                if(!plugin.getConfig().getBoolean("config.hideVirtualExpMessage")) {
                    event.getPlayer().sendMessage(gainMessage(e.getExp()));
                }
            } else {
                ExperienceOrb orb = event.getBlock().getWorld().spawn(event.getBlock().getLocation(), ExperienceOrb.class);
                orb.setExperience(e.getExp());
            }
        }
    }
}
