package com.talhanation.recruits.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;

/**
 * Delegate for vanilla mobs taking the role of a captain. Captains can
 * issue strategic fire commands and optionally track a sail position.
 */
public class CaptainMobRecruit extends MobRecruit implements IStrategicFire {

    private static final String KEY_SAIL_POS = "SailPos";
    private static final String KEY_STRATEGIC_FIRE = "ShouldStrategicFire";
    private static final String KEY_STRATEGIC_FIRE_POS = "StrategicFirePos";

    public CaptainMobRecruit(Mob mob) {
        super(mob);
    }

    public void setSailPos(@Nullable BlockPos pos) {
        setBlockPos(KEY_SAIL_POS, pos);
    }

    @Nullable
    public BlockPos getSailPos() {
        return getBlockPos(KEY_SAIL_POS);
    }

    @Override
    public void setShouldStrategicFire(boolean should) {
        setBoolean(KEY_STRATEGIC_FIRE, should);
    }

    @Override
    public void setStrategicFirePos(BlockPos blockPos) {
        setBlockPos(KEY_STRATEGIC_FIRE_POS, blockPos);
    }

    public boolean getShouldStrategicFire() {
        return getBoolean(KEY_STRATEGIC_FIRE);
    }

    @Nullable
    public BlockPos getStrategicFirePos() {
        return getBlockPos(KEY_STRATEGIC_FIRE_POS);
    }
}
