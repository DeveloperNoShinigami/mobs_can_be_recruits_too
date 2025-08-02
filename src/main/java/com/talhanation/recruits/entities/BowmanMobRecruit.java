package com.talhanation.recruits.entities;

import net.minecraft.world.entity.Mob;

/**
 * Simple delegate for vanilla bow using mobs.
 * Provides ranged recruit behaviour via {@link RangedMobRecruit}.
 */
public class BowmanMobRecruit extends RangedMobRecruit {

    public BowmanMobRecruit(Mob mob) {
        super(mob);
    }
}
