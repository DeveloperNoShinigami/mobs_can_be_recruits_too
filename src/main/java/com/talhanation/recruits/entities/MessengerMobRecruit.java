package com.talhanation.recruits.entities;

import net.minecraft.world.entity.Mob;

/**
 * Delegate for messenger style companions. Stores message and state
 * information so vanilla mobs can act as messengers.
 */
public class MessengerMobRecruit extends CompanionMobRecruit {

    private static final String KEY_MESSAGE = "MessengerMessage";
    private static final String KEY_STATE = "MessengerState";
    private static final String KEY_WAITING = "MessengerWaiting";

    public MessengerMobRecruit(Mob mob) {
        super(mob);
    }

    public String getMessage() {
        return getString(KEY_MESSAGE);
    }

    public void setMessage(String message) {
        setString(KEY_MESSAGE, message);
    }

    public int getMessengerState() {
        return getInt(KEY_STATE);
    }

    public void setMessengerState(int state) {
        setInt(KEY_STATE, state);
    }

    public int getWaitingTime() {
        return getInt(KEY_WAITING);
    }

    public void setWaitingTime(int time) {
        setInt(KEY_WAITING, time);
    }
}
