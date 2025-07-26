package com.talhanation.recruits.util;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * Simple interface for mobs that can participate in formations.
 */
public interface FormationMember {
    /**
     * @return underlying mob entity
     */
    Mob getMob();

    /**
     * Assign the mob's hold position.
     */
    void setHoldPos(Vec3 pos);

    /**
     * Assign the mob's follow state.
     */
    void setFollowState(int state);
}
