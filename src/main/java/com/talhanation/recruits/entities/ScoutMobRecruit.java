package com.talhanation.recruits.entities;

import net.minecraft.world.entity.Mob;

/**
 * Delegate for vanilla scout companions. Stores additional state used by
 * {@link ScoutEntity} such as task state and scouting timers.
 */
public class ScoutMobRecruit extends CompanionMobRecruit {

    private static final String KEY_TASK_STATE = "ScoutTaskState";
    private static final String KEY_SCOUT_TIMER = "ScoutTimer";

    public ScoutMobRecruit(Mob mob) {
        super(mob);
    }

    public int getTaskState() {
        return getInt(KEY_TASK_STATE);
    }

    public void setTaskState(int state) {
        setInt(KEY_TASK_STATE, state);
    }

    public int getScoutingTimer() {
        return getInt(KEY_SCOUT_TIMER);
    }

    public void setScoutingTimer(int timer) {
        setInt(KEY_SCOUT_TIMER, timer);
    }
}
