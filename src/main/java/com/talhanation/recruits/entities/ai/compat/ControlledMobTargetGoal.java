package com.talhanation.recruits.entities.ai.compat;

import com.talhanation.recruits.RecruitEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

/**
 * Target goal for controlled mobs that respects Recruit diplomacy rules.
 */
public class ControlledMobTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
    public ControlledMobTargetGoal(PathfinderMob mob) {
        super(mob, LivingEntity.class, 10, true, false, target -> RecruitEvents.canAttack(mob, target));
    }

    @Override
    public boolean canUse() {
        int state = this.mob.getPersistentData().getInt("AggroState");
        if (state != 1 && state != 2) return false; // only free target when aggressive or raid
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        int state = this.mob.getPersistentData().getInt("AggroState");
        if (state != 1 && state != 2) return false;
        return super.canContinueToUse();
    }
}
