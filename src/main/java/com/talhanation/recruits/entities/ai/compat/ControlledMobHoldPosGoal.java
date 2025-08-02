package com.talhanation.recruits.entities.ai.compat;

import com.talhanation.recruits.entities.IRecruitEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Makes controlled mobs move to and stay at their assigned hold position.
 */
public class ControlledMobHoldPosGoal extends Goal {
    private final PathfinderMob mob;
    private final double speed;
    private BlockPos targetPos;

    public ControlledMobHoldPosGoal(PathfinderMob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        CompoundTag nbt = mob.getPersistentData();
        IRecruitEntity recruit = IRecruitEntity.of(mob);
        if (!recruit.isOwned()) return false;
        int state = nbt.getInt("FollowState");
        if (state != 2 && state != 3) return false;
        if (!nbt.contains("HoldX")) {
            nbt.putDouble("HoldX", mob.getX());
            nbt.putDouble("HoldY", mob.getY());
            nbt.putDouble("HoldZ", mob.getZ());
        }
      
        targetPos = new BlockPos((int) nbt.getDouble("HoldX"), (int) nbt.getDouble("HoldY"), (int) nbt.getDouble("HoldZ"));

        return mob.blockPosition().distSqr(targetPos) > 2.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.blockPosition().distSqr(targetPos) > 2.0D && !mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        moveToPos();
    }

    @Override
    public void tick() {
        moveToPos();
    }

    private void moveToPos() {
        if (targetPos != null) {
            mob.getNavigation().moveTo(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D, speed);
        }
    }
}
