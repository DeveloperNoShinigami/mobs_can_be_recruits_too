package com.talhanation.recruits.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * Adapter for generic mobs to participate in formations using persistent NBT fields.
 */
public class MobFormationAdapter implements FormationMember {
    private final Mob mob;

    public MobFormationAdapter(Mob mob) {
        this.mob = mob;
    }

    @Override
    public Mob getMob() {
        return mob;
    }

    @Override
    public void setHoldPos(Vec3 pos) {
        CompoundTag nbt = mob.getPersistentData();
        nbt.putDouble("HoldX", pos.x);
        nbt.putDouble("HoldY", pos.y);
        nbt.putDouble("HoldZ", pos.z);
    }

    @Override
    public void setFollowState(int state) {
        mob.getPersistentData().putInt("FollowState", state);
    }
}
