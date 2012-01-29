package com.feildmaster.controlorble.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerBreakBlockDropOrbEvent extends DropOrbEvent {
    private Player player;
    private Block block;

    public PlayerBreakBlockDropOrbEvent(Player player, Block block) {
        this(player, block, 0);
    }

    public PlayerBreakBlockDropOrbEvent(Player player, Block block, int amount) {
        super(amount);
        this.player = player;
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public Player getPlayer() {
        return player;
    }
}
