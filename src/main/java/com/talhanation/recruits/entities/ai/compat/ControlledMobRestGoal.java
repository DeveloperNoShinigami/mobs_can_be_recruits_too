package com.talhanation.recruits.entities.ai.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Simple rest goal for controlled mobs.
 */
public class ControlledMobRestGoal extends Goal {
    private final PathfinderMob mob;

    public ControlledMobRestGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        CompoundTag nbt = mob.getPersistentData();
        if (!nbt.getBoolean("RecruitControlled") || !nbt.getBoolean("Owned"))
            return false;
        if (!nbt.getBoolean("ShouldRest"))
            return false;
        if (nbt.getInt("FollowState") != 0)
            return false;
        return mob.getTarget() == null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (mob.getHealth() < mob.getMaxHealth()) {
            mob.heal(0.05F);
        }
    }

    @Override
    public void stop() {
        mob.getPersistentData().putBoolean("ShouldRest", false);
    }
}
