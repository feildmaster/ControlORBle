package com.feildmaster.controlorble.listeners;

import com.feildmaster.controlorble.*;
import com.feildmaster.controlorble.event.BlockPlaceExpEvent;
import com.feildmaster.lib.expeditor.Editor;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.metadata.*;

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
            plugin.debug("Enitity " + event.getEntityType() + "[" + event.getEntity().getUniqueId() + "], will not drop experience when killed.");
            event.getEntity().setMetadata("noExp", noExp);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0); // Set to 0, let plugin override later.

        // Player gets special case
        if (event instanceof PlayerDeathEvent) {
            playerDeathHandler((PlayerDeathEvent) event);
            return;
        }

        Entity entity = event.getEntity();
        Player p = getPlayer(entity.getLastDamageCause());

        if (p == null) {
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
        if (cause == null) {
            plugin.debug("EntityDamageEvent is null!");
            return null;
        }
        if (!(cause instanceof EntityDamageByEntityEvent)) {
            plugin.debug("EntityDamageEvent isn't by Entity.");
            return null;
        }

        Player p = null;
        Entity damager = ((EntityDamageByEntityEvent) cause).getDamager();

        if (damager instanceof Projectile) {
            Projectile projectile = (Projectile) damager;
            if (projectile.getShooter() instanceof Player) {
                p = (Player) projectile.getShooter();
                plugin.debug(projectile.getType() + " killed " + cause.getEntityType() + ". Shooter: " + p.getName());
            } else {
                plugin.debug(projectile.getType() + " killed " + cause.getEntityType());
            }
        } else if (damager instanceof Player) {
            p = (Player) damager;
            plugin.debug(cause.getEntityType() + " killed by player: " + p.getName());
        } else if (damager instanceof Tameable && plugin.getConfig().getBoolean("config.enablePets")) {
            Tameable animal = (Tameable) damager;
            if (animal.isTamed()) {
                AnimalTamer tamer = animal.getOwner();
                if (tamer instanceof Player) {
                    p = (Player) tamer;
                }
            }
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
        if (entity instanceof Snowman) {
            return plugin.getConfig().getExp("animal.Snowman");
        }
        // 1.0.0 Monsters
        if (entity instanceof EnderDragon || entity instanceof ComplexEntityPart) {
            return plugin.getConfig().getExp("monster.EnderDragon");
        }
        if (entity instanceof Blaze) {
            return plugin.getConfig().getExp("monster.Blaze");
        }
        // Monsters
        if (entity instanceof Creeper) {
            return plugin.getConfig().getExp("monster.Creeper");
        }
        if (entity instanceof Enderman) {
            return plugin.getConfig().getExp("monster.Enderman");
        }
        if (entity instanceof Ghast) {
            return plugin.getConfig().getExp("monster.Ghast");
        }
        if (entity instanceof Giant) {
            return plugin.getConfig().getExp("monster.Giant");
        }
        if (entity instanceof PigZombie) {
            return plugin.getConfig().getExp("monster.PigZombie");
        }
        if (entity instanceof Silverfish) {
            return plugin.getConfig().getExp("monster.Silverfish");
        }
        if (entity instanceof Skeleton) {
            return plugin.getConfig().getExp("monster.Skeleton");
        }
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
        if (entity instanceof Zombie) {
            return plugin.getConfig().getExp("monster.Zombie");
        }
        // Animals
        if (entity instanceof Wolf) {
            return plugin.getConfig().getExp(((Wolf) entity).isTamed() ? "animal.tameWolf" : "monster.Wolf");
        }
        if (entity instanceof Chicken) {
            return plugin.getConfig().getExp("animal.Chicken");
        }
        if (entity instanceof Cow) {
            return plugin.getConfig().getExp("animal.Cow");
        }
        if (entity instanceof Pig) {
            return plugin.getConfig().getExp("animal.Pig");
        }
        if (entity instanceof Sheep) {
            return plugin.getConfig().getExp("animal.Sheep");
        }
        if (entity instanceof Squid) {
            return plugin.getConfig().getExp("animal.Squid");
        }

        plugin.debug("\"" + entity.getType() + "\" not found. 0 Experience.");
        return 0;
    }

    private void playerDeathHandler(PlayerDeathEvent event) {
        Player p = (Player) event.getEntity();
        plugin.debug("Player Death: " + p.getName());

        if (p.hasPermission("orbEnhance.KeepExp")) { // Keep all experience
            plugin.debug("\"orbEnhance.KeepExp\" present, keeping all experience.");
            event.setKeepLevel(true);
            return;
        }

        Editor e = new Editor(p);
        e.recalcTotalExp(); // Recalculate total experience, because minecraft doesn't do this...!

        EntityDamageEvent.DamageCause cause = p.getLastDamageCause() == null ? EntityDamageEvent.DamageCause.CUSTOM : p.getLastDamageCause().getCause();

        int expBase = expLossByTotal() ? e.getTotalExp() : e.getExpToLevel();
        double percentage = calculatePercent(cause) / 100D;

        Double loss = expBase * percentage;

        if (!plugin.getConfig().getBoolean("config.playerDelevel") && loss > e.getExp()) {
            loss = (double) e.getExp();
        }

        if (e.getTotalExp() > loss.intValue()) {
            final int exp = e.getTotalExp() - loss.intValue();
            event.setNewExp(exp + 1);
        } else {
            if (event.getNewExp() != 0) {
                event.setNewExp(0);
            }
            loss = (double) e.getTotalExp();
        }

        plugin.debug(p.getName() + " will respawn with " + (event.getNewExp() - 1) + " exp");

        if (loss.intValue() <= 0) {
            return;
        }

        if (plugin.getConfig().getBoolean("config.virtualPlayerEXP")) {
            sendExpMessage(p, gainMessage(-loss.intValue()));

            Player killer = getPlayer(p.getLastDamageCause());
            if (killer != null) {
                // Burn the experience
                burn(loss);

                if (loss.intValue() <= 0) {
                    return;
                }

                // Give exp and send message
                killer.giveExp(loss.intValue());
                sendExpMessage(killer, gainMessage(loss.intValue()));
            }
        } else {
            burn(loss);
            if (loss.intValue() <= 0) {
                return;
            }
            event.setDroppedExp(loss.intValue());
        }
    }

    private void burn(Double value) {
        if (value <= 0) {
            return;
        }

        int percent = plugin.getConfig().getPercent("config.expBurn");
        if (percent > 0) {
            value -= value * (percent / 100D);
        }
    }

    private void monsterDeathHandler(EntityDeathEvent event, Player p, int exp) {
        if (exp != 0) {
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
        return "You " + (exp >= 0 ? ("gained " + exp) : ("lost " + -exp)) + " exp";
    }

    private void sendExpMessage(Player player, String message) {
        if (player == null || message == null) {
            return;
        }

        if (player.getListeningPluginChannels().contains("SimpleNotice")) {
            player.sendPluginMessage(plugin, "SimpleNotice", message.getBytes(java.nio.charset.Charset.forName("UTF-8")));
        } else if (!plugin.getConfig().getBoolean("config.hideVirtualEXPMessage")) {
            player.sendMessage(message);
        }
    }

    private int calculatePercent(EntityDamageEvent.DamageCause dc) {
        if (plugin.getConfig().getBoolean("config.customExpLoss")) {
            switch (dc) {
                // TODO: Suffocation and Fall percent
                //case SUFFOCATION: return Plugin.expLossSuffocate;
                //case FALL: return Plugin.expLossFall;

                case CONTACT:
                    return plugin.getConfig().getPercent("expLoss.Contact");

                case FIRE:
                case FIRE_TICK:
                    return plugin.getConfig().getPercent("expLoss.Fire");

                case LAVA:
                    return plugin.getConfig().getPercent("expLoss.Lava");

                case DROWNING:
                    return plugin.getConfig().getPercent("expLoss.Drown");

                case BLOCK_EXPLOSION:
                    return plugin.getConfig().getPercent("expLoss.TnT");

                case VOID:
                    return plugin.getConfig().getPercent("expLoss.Void");

                case LIGHTNING:
                    return plugin.getConfig().getPercent("expLoss.Lightning");

                case SUICIDE:
                    return plugin.getConfig().getPercent("expLoss.Suicide");

                default:
                    return plugin.getConfig().getPercent("expLoss.Basic");
            }
        } else {
            return plugin.getConfig().getPercent("expLoss.Basic");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent event) {
        int exp = plugin.getConfig().getExp(getBlockExpKey(event.getBlock(), "break"));

        if (event.getExpToDrop() != 0 && exp == 0) {
            return; // Use the defaults
        }

        int chance = plugin.getConfig().getPercent("chance.blockBreak");
        if (chance == 0 || (chance < 100 && chance > random.nextInt(100))) {
            return;
        }

        event.setExpToDrop(exp);
    }

    // We should use MONITOR here (Even though we're "changing" the event)
    @EventHandler(priority = EventPriority.HIGHEST)
    public void giveVirtualExtractExp(FurnaceExtractEvent event) {
        if (!plugin.getConfig().getBoolean("config.virtualFurnaceEXP")) {
            return;
        }

        giveExperience(event.getPlayer(), event.getExpToDrop());
        event.setExpToDrop(0); // Drop nothing.
    }

    // We should use MONITOR here (Even though we're "changing" the event)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void giveVirtualExp(BlockBreakEvent event) {
        if (!plugin.getConfig().getBoolean("config.virtualBlockEXP")) {
            return;
        }

        giveExperience(event.getPlayer(), event.getExpToDrop());
        event.setExpToDrop(0); // Drop nothing.
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void expChange(PlayerExpChangeEvent event) {
        if (event.getAmount() >= 0) {
            return;
        }

        new Editor(event.getPlayer()).takeExp(event.getAmount(), expLossByTotal());
        event.setAmount(0); // After taking experience, set to 0. Don't want minecraft fucking up. :D
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void blockPlace(BlockPlaceEvent event) {
        int exp = plugin.getConfig().getExp(getBlockExpKey(event.getBlock(), "place"));

        if (BlockPlaceExpEvent.getHandlerList().getRegisteredListeners().length > 0) {
            BlockPlaceExpEvent e = new BlockPlaceExpEvent(event.getPlayer(), event.getBlock(), exp);

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

    private String getBlockExpKey(Block block, String type) {
        String string = String.format("blockExp.%s-%d.%s", block.getType(), block.getData(), type);
        if (!plugin.getConfig().isSet(string)) {
            string = String.format("blockExp.%s.%s", block.getType(), type);
        }
        return string;
    }

    private void giveExperience(Player player, int exp) {
        // No experience is bad experience
        if (exp == 0) {
            return;
        }

        if (exp < 0) {
            Editor editor = new Editor(player);
            boolean flag = expLossByTotal();
            if (flag) {
                editor.recalcTotalExp();
            }

            editor.takeExp(exp, flag);
        } else if (exp > 0) {
            new Editor(player).giveExp(exp);
        }

        sendExpMessage(player, gainMessage(exp));
    }

    private void spawnExperience(BlockEvent event, int exp) {
        // This needs to be fixed in Minecraft/CraftBukkit!!
        if (exp == 0) {
            return;
        }

        ExperienceOrb orb = event.getBlock().getWorld().spawn(event.getBlock().getLocation(), ExperienceOrb.class);
        orb.setExperience(exp);
    }

    private boolean expLossByTotal() {
        return plugin.getConfig().getBoolean("config.expLossByTotal") && plugin.getConfig().getBoolean("config.playerDelevel");
    }
}
