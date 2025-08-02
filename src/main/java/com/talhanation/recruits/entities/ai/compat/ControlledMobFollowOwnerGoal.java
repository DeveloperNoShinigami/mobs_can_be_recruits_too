package com.talhanation.recruits.entities.ai.compat;

import com.talhanation.recruits.entities.IRecruitEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.server.level.ServerLevel;

import java.util.EnumSet;
import java.util.UUID;

/**
 * Simple follow owner goal for non-recruit mobs controlled via UniversalMobControl.
 */
public class ControlledMobFollowOwnerGoal extends Goal {
    private final PathfinderMob mob;
    private final double speed;
    private final float startDistance;
    private final float stopDistance;
    private Player owner;

    public ControlledMobFollowOwnerGoal(PathfinderMob mob, double speed, float startDistance, float stopDistance) {
        this.mob = mob;
        this.speed = speed;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    private Player getOwner() {
        IRecruitEntity recruit = IRecruitEntity.of(mob);
        UUID id = recruit.getOwnerUUID();
        return id == null ? null : ((ServerLevel) mob.level()).getPlayerByUUID(id);
    }

    @Override
    public boolean canUse() {
        CompoundTag nbt = mob.getPersistentData();
        IRecruitEntity recruit = IRecruitEntity.of(mob);
        if (!recruit.isOwned()) return false;
        if (nbt.getInt("FollowState") != 1) return false;
        owner = getOwner();
        return owner != null && mob.distanceTo(owner) > startDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return owner != null && !mob.getNavigation().isDone() && mob.distanceTo(owner) > stopDistance;
    }

    @Override
    public void start() {
        mob.getNavigation().moveTo(owner, speed);
    }

    @Override
    public void tick() {
        if (owner != null) {
            mob.getNavigation().moveTo(owner, speed);
        }
    }
}
