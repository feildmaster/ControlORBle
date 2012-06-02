package com.feildmaster.controlorble.listeners;

import com.feildmaster.lib.expeditor.Editor;
import com.feildmaster.controlorble.*;
import com.feildmaster.controlorble.event.*;
import java.util.List;
import java.util.Random;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.metadata.*;

// TODO: Clean "plugin.getConfig().getBoolean("config.hideVirtualEXPMessage")" into a common function
// experienceMessage(player, amount)
public class OrbListener implements Listener {
    private final JavaPlugin plugin;
    private final LazyMetadataValue noExp;
    private final Random random = new Random();

    public OrbListener(JavaPlugin p) {
        plugin = p;
        plugin.debug("Registering Events");
        noExp = new FixedMetadataValue(plugin, true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (plugin.getConfig().getBoolean("stopMonsterEXP.monsterSpawner") && event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            plugin.debug("Enitity "+event.getEntityType()+"["+event.getEntity().getUniqueId()+"], will not drop experience when killed.");
            event.getEntity().setMetadata("noExp", noExp);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0); // Set to 0, let plugin override later.

        // Player gets special case
        if(event instanceof PlayerDeathEvent) {
            playerDeathHandler((PlayerDeathEvent) event);
            return;
        }

        Entity entity = event.getEntity();
        Player p = getPlayer(entity.getLastDamageCause());

        if(p == null) {
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

        if(damager instanceof Projectile) {
            Projectile projectile = (Projectile) damager;
            if(projectile.getShooter() instanceof Player) {
                p = (Player) projectile.getShooter();
                plugin.debug(projectile.getType() + " killed " + cause.getEntityType() + ". Shooter: " + p.getName());
            } else {
                plugin.debug(projectile.getType() + " killed " + cause.getEntityType());
            }
        } else if(damager instanceof Player) {
            p = (Player)damager;
            plugin.debug(cause.getEntityType() + " killed by player: " + p.getName());
        }

        return p;
    }

    private int getExp(Entity entity) {
        plugin.debug("Getting Experience from " + entity.getType() + " death");
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

        plugin.debug("\"" + entity.getType() + "\" not found. 0 Experience.");
        return 0;
    }

    private void playerDeathHandler(PlayerDeathEvent event) {
        Player p = (Player) event.getEntity();

        plugin.debug("Player Death: " + p.getName());

        Editor e = new Editor(p);
        // Recalculate total experience, because minecraft doesn't do this...!
        e.recalcTotalExp();

        EntityDamageEvent.DamageCause cause = p.getLastDamageCause() == null ? EntityDamageEvent.DamageCause.CUSTOM : p.getLastDamageCause().getCause();

        int expBase = plugin.getConfig().getBoolean("config.expLossByTotal") ? e.getTotalExp() : e.getExpToLevel();
        double percentage = calculatePercent(cause) / 100D;

        Double loss = expBase * percentage;

        if(p.hasPermission("orbEnhance.KeepExp")) { // Keep all experience
            plugin.debug("\"orbEnhance.KeepExp\" present, keeping all experience.");
            event.setKeepLevel(true);
            return;
        }

        if(!plugin.getConfig().getBoolean("config.playerDelevel") && loss > e.getExp()) {
            loss = (double) e.getExp();
        }

        if(e.getTotalExp() > loss.intValue()) {
            final int exp = e.getTotalExp() - loss.intValue();
            event.setNewExp(exp);
        } else {
            if (event.getNewExp() != 0) {
                event.setNewExp(0);
            }
            loss = (double) e.getTotalExp();
        }

        plugin.debug(p.getName() + " will respawn with " + event.getNewExp() + " exp");

        if(loss.intValue() > 0 && plugin.getConfig().getPercent("config.expBurn") > 0) {
            loss -= loss * (plugin.getConfig().getPercent("config.expBurn")/100D) ;
        }

        if(loss.intValue() > 0) {
            if(plugin.getConfig().getBoolean("config.virtualPlayerEXP")) {
                if(!plugin.getConfig().getBoolean("config.hideVirtualEXPMessage")) {
                    // You lose more than this... it gets displayed after expBurn
                    p.sendMessage("You have lost "+loss.intValue()+" experience");
                }

                Player killer = getPlayer(p.getLastDamageCause());
                if(killer != null) {
                    killer.giveExp(loss.intValue());
                    if (!plugin.getConfig().getBoolean("config.hideVirtualEXPMessage")) {
                        sendMessage(killer, gainMessage(loss.intValue()));
                    }
                }
            } else {
                event.setDroppedExp(loss.intValue());
            }
        }
    }

    private void monsterDeathHandler(EntityDeathEvent event, Player p, int exp) {
        if(exp != 0) {
            exp *= plugin.getConfig().getMultiplier("monsterEXP");
            if (plugin.getConfig().getBoolean("config.virtualEXP")) {
                giveExperience(p, exp);
            } else {
                event.setDroppedExp(exp);
            }
        }

        plugin.debug(event.getEntity().getType() + " dropped " + exp + " exp");
    }

    private String gainMessage(int exp) {
        //return "You have " + (exp >= 0 ? "gained" : "lost") + " " + (exp >= 0 ? exp : -exp) + " experience";
        return "You have gained " + exp + " experience";
    }

    private void sendMessage(Player player, String message) {
        if (player == null || message == null) {
            return;
        }

        if (!plugin.sendPluginMessage(player, message)) {
            player.sendMessage(message);
        }
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent event) {
        String key = "blockExp."+event.getBlock().getType().toString() + ".break";
        int exp = plugin.getConfig().getExp(key);

        if (PlayerBreakBlockDropOrbEvent.getHandlerList().getRegisteredListeners().length > 0) {
            PlayerBreakBlockDropOrbEvent e = new PlayerBreakBlockDropOrbEvent(event.getPlayer(), event.getBlock(), exp);

            int chance = plugin.getConfig().getPercent("chance.blockBreak");
            e.setCancelled(chance == 0 || (chance < 100 && chance > random.nextInt(100)));

            plugin.getServer().getPluginManager().callEvent(e);

            exp = e.getExp();

            if (e.isCancelled()) {
                return;
            }
        } else {
            int chance = plugin.getConfig().getPercent("chance.blockBreak");
            if (chance == 0 || (chance < 100 && chance > random.nextInt(100))) {
                return;
            }
        }

        if (plugin.getConfig().getBoolean("config.virtualBlockEXP")) {
            giveExperience(event.getPlayer(), exp);
        } else {
            spawnExperience(event, exp);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void blockPlace(BlockPlaceEvent event) {
        String key = "blockExp." + event.getBlock().getType().toString() + ".place";
        int exp = plugin.getConfig().getExp(key);

        if (PlayerPlaceBlockDropOrbEvent.getHandlerList().getRegisteredListeners().length > 0) {
            PlayerPlaceBlockDropOrbEvent e = new PlayerPlaceBlockDropOrbEvent(event.getPlayer(), event.getBlock(), exp);

            int chance = plugin.getConfig().getPercent("chance.blockPlace");
            e.setCancelled(chance == 0 && (chance < 100 && chance > random.nextInt(100)));

            plugin.getServer().getPluginManager().callEvent(e);

            exp = e.getExp();

            if (e.isCancelled()) {
                return;
            }
        } else {
            int chance = plugin.getConfig().getPercent("chance.blockPlace");
            if (chance == 0 || (chance < 100 && chance > random.nextInt(100))) {
                return;
            }
        }

        if (plugin.getConfig().getBoolean("config.virtualBlockEXP")) {
            giveExperience(event.getPlayer(), exp);
        } else {
            spawnExperience(event, exp);
        }
    }

    private void giveExperience(Player player, int exp) {
        Editor editor = new Editor(player);
        if (exp < 0) {
            boolean flag = plugin.getConfig().getBoolean("config.expLossByTotal");
            if (flag) {
                editor.recalcTotalExp();
            }

            editor.takeExp(exp, flag);
        } else if (exp > 0) {
            editor.giveExp(exp);
        }

        if (!plugin.getConfig().getBoolean("config.hideVirtualEXPMessage")) {
            sendMessage(player, gainMessage(exp));
        }
    }

    private void spawnExperience(BlockEvent event, int exp) {
        // This needs to be fixed in Minecraft/CraftBukkit!!
        if (exp < 1) {
            return;
        }

        ExperienceOrb orb = event.getBlock().getWorld().spawn(event.getBlock().getLocation(), ExperienceOrb.class);
        orb.setExperience(exp);
    }
}
