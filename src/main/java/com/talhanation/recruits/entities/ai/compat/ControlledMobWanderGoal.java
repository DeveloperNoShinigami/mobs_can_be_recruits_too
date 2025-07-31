package com.talhanation.recruits.entities.ai.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

/**
 * Random wandering goal that respects FollowState for controlled mobs.
 */
public class ControlledMobWanderGoal extends RandomStrollGoal {
    private final PathfinderMob mob;

    public ControlledMobWanderGoal(PathfinderMob mob, double speed) {
        super(mob, speed);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        CompoundTag nbt = mob.getPersistentData();
        if (nbt.getBoolean("RecruitControlled") && nbt.getInt("FollowState") != 0) {
            return false;
        }
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        CompoundTag nbt = mob.getPersistentData();
        if (nbt.getBoolean("RecruitControlled") && nbt.getInt("FollowState") != 0) {
            return false;
        }
        return super.canContinueToUse();
    }
}
