package com.talhanation.recruits.entities.ai.compat;

import com.talhanation.recruits.entities.IRecruitEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.PathfinderMob;

/**
 * Melee attack goal respecting Recruit control flags.
 */
public class ControlledMobMeleeAttackGoal extends MeleeAttackGoal {
    private final PathfinderMob mob;

    public ControlledMobMeleeAttackGoal(PathfinderMob mob, double speed, boolean longMemory) {
        super(mob, speed, longMemory);
        this.mob = mob;
    }

    private boolean isActive() {
        CompoundTag nbt = mob.getPersistentData();
        IRecruitEntity recruit = IRecruitEntity.of(mob);
        if (!nbt.getBoolean("RecruitControlled") || !recruit.isOwned())
            return false;
        if (nbt.getBoolean("ShouldRest"))
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
