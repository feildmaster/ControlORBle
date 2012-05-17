package com.feildmaster.controlorble.event;

import org.bukkit.event.Event;

public abstract class DropOrbEvent extends Event {
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
}
