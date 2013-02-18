package com.feildmaster.controlorble.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class BlockPlaceExpEvent extends PlayerEvent implements Cancellable {
    private static HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Block block;
    private int exp;

    public BlockPlaceExpEvent(Player player, Block block) {
        this(player, block, 0);
    }

    public BlockPlaceExpEvent(Player player, Block block, int exp) {
        super(player);
        this.exp = exp;
        this.block = block;
    }

    /**
     * Get the block placed
     *
     * @return new instance of the block being placed
     */
    public Block getBlock() {
        return getPlayer().getWorld().getBlockAt(block.getX(), block.getY(), block.getZ());
    }

    /**
     * Gets the amount of experience the orb will drop
     *
     * @return exp the orb will drop
     */
    public int getExp() {
        return exp;
    }

    /**
     * Set the amount of experience for orb drop. Can not be less than 0.
     *
     * @param value Amount to set the orb to drop
     */
    public void setExp(int value) {
        if(value < 0) return;
        exp = value;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean value) {
        cancelled = value;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
