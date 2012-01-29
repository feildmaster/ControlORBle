package com.feildmaster.controlorble.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DropOrbEvent extends Event {
    private int exp;

    public DropOrbEvent(int value) {
        exp = value;
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

    private static HandlerList handlers = new HandlerList();
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
