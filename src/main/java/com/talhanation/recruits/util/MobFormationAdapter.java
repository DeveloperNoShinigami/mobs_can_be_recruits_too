package com.talhanation.recruits.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.phys.Vec3;

/**
 * Adapter for generic mobs to participate in formations using persistent NBT fields.
 */
public class MobFormationAdapter implements FormationMember {
    private static final String KEY_HOLD_X = "HoldX";
    private static final String KEY_HOLD_Y = "HoldY";
    private static final String KEY_HOLD_Z = "HoldZ";
    private static final String KEY_SHOULD_HOLD_POS = "ShouldHoldPos";
    private static final String KEY_SHOULD_FOLLOW = "ShouldFollow";
    private static final String KEY_SHOULD_PROTECT = "ShouldProtect";
    private static final String KEY_SHOULD_MOVE_POS = "ShouldMovePos";
    private static final String KEY_FOLLOW_STATE = "FollowState";

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
        nbt.putDouble(KEY_HOLD_X, pos.x);
        nbt.putDouble(KEY_HOLD_Y, pos.y);
        nbt.putDouble(KEY_HOLD_Z, pos.z);
    }

    @Override
    public void setFollowState(int state) {
        CompoundTag nbt = mob.getPersistentData();
        switch (state) {
            case 0, 6 -> {
                nbt.putBoolean(KEY_SHOULD_FOLLOW, false);
                nbt.putBoolean(KEY_SHOULD_HOLD_POS, false);
                nbt.putBoolean(KEY_SHOULD_PROTECT, false);
                nbt.putBoolean(KEY_SHOULD_MOVE_POS, false);
            }
            case 1 -> {
                nbt.putBoolean(KEY_SHOULD_FOLLOW, true);
                nbt.putBoolean(KEY_SHOULD_HOLD_POS, false);
                nbt.putBoolean(KEY_SHOULD_PROTECT, false);
                nbt.putBoolean(KEY_SHOULD_MOVE_POS, false);
            }
            case 2 -> {
                nbt.putBoolean(KEY_SHOULD_FOLLOW, false);
                nbt.putBoolean(KEY_SHOULD_HOLD_POS, true);
                nbt.putBoolean(KEY_SHOULD_PROTECT, false);
                nbt.putBoolean(KEY_SHOULD_MOVE_POS, false);
                Vec3 pos = mob.position();
                nbt.remove(KEY_HOLD_X);
                nbt.remove(KEY_HOLD_Y);
                nbt.remove(KEY_HOLD_Z);
                setHoldPos(pos);
            }
            case 3 -> {
                nbt.putBoolean(KEY_SHOULD_FOLLOW, false);
                nbt.putBoolean(KEY_SHOULD_HOLD_POS, true);
                nbt.putBoolean(KEY_SHOULD_PROTECT, false);
                nbt.putBoolean(KEY_SHOULD_MOVE_POS, false);
            }
            case 4 -> {
                nbt.putBoolean(KEY_SHOULD_FOLLOW, false);
                nbt.putBoolean(KEY_SHOULD_HOLD_POS, true);
                nbt.putBoolean(KEY_SHOULD_PROTECT, false);
                nbt.putBoolean(KEY_SHOULD_MOVE_POS, false);
                Vec3 pos = mob.position();
                if (mob instanceof TamableAnimal tamable) {
                    LivingEntity owner = tamable.getOwner();
                    if (owner != null) {
                        pos = owner.position();
                    }
                }
                nbt.remove(KEY_HOLD_X);
                nbt.remove(KEY_HOLD_Y);
                nbt.remove(KEY_HOLD_Z);
                setHoldPos(pos);
                state = 3;
            }
            case 5 -> {
                nbt.putBoolean(KEY_SHOULD_FOLLOW, false);
                nbt.putBoolean(KEY_SHOULD_HOLD_POS, false);
                nbt.putBoolean(KEY_SHOULD_PROTECT, true);
                nbt.putBoolean(KEY_SHOULD_MOVE_POS, false);
            }
        }
        nbt.putInt(KEY_FOLLOW_STATE, state);
    }
}
