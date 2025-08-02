package com.talhanation.recruits.entities.ai.compat;

import com.talhanation.recruits.entities.IRecruitEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;

/**
 * Ranged bow attack goal respecting Recruit control flags.
 */
public class ControlledMobRangedBowAttackGoal<T extends PathfinderMob & RangedAttackMob> extends RangedBowAttackGoal<T> {
    private final T mob;

    public ControlledMobRangedBowAttackGoal(T mob, double speed, int delay, float radius) {
        super(mob, speed, delay, radius);
        this.mob = mob;
    }

    private boolean isActive() {
        CompoundTag nbt = mob.getPersistentData();
        IRecruitEntity recruit = IRecruitEntity.of(mob);
        if (!nbt.getBoolean("RecruitControlled") || !recruit.isOwned())
            return false;
        if (nbt.getBoolean("ShouldRest"))
            return false;
        if (!nbt.getBoolean("ShouldRanged"))
            return false;
        return nbt.getInt("AggroState") != 3;
    }

    @Override
    public boolean canUse() {
        if (!isActive()) return false;
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (!isActive()) return false;
        return super.canContinueToUse();
    }
}
