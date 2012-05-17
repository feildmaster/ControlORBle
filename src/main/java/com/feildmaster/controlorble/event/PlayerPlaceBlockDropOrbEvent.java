package com.feildmaster.controlorble.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerPlaceBlockDropOrbEvent extends DropOrbEvent implements Cancellable {
    private static HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player player;
    private Block block;

    public PlayerPlaceBlockDropOrbEvent(Player player, Block block) {
        this(player, block, 0);
    }

    public PlayerPlaceBlockDropOrbEvent(Player player, Block block, int exp) {
        super(exp);
        this.player = player;
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public Player getPlayer() {
        return player;
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
