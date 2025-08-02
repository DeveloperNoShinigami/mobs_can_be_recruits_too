package com.talhanation.recruits.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;

import javax.annotation.Nullable;

/**
 * Delegate for ranged vanilla mobs. Implements recruit ranged
 * interfaces so command logic can interact with skeletons or other
 * shooters using the same code paths as real recruits.
 */
public class RangedMobRecruit extends MobRecruit implements IRangedRecruit, IStrategicFire {

    private static final String KEY_STRATEGIC_FIRE = "ShouldStrategicFire";
    private static final String KEY_STRATEGIC_FIRE_POS = "StrategicFirePos";

    public RangedMobRecruit(Mob mob) {
        super(mob);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (getMob() instanceof RangedAttackMob ranged) {
            ranged.performRangedAttack(target, distanceFactor);
        }
    }

    @Override
    public void setShouldStrategicFire(boolean should) {
        setBoolean(KEY_STRATEGIC_FIRE, should);
    }

    @Override
    public void setStrategicFirePos(BlockPos blockpos) {
        setBlockPos(KEY_STRATEGIC_FIRE_POS, blockpos);
    }

    public boolean shouldStrategicFire() {
        return getBoolean(KEY_STRATEGIC_FIRE);
    }

    @Nullable
    public BlockPos getStrategicFirePos() {
        return getBlockPos(KEY_STRATEGIC_FIRE_POS);
    }
}

