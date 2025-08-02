package com.talhanation.recruits.entities;

import net.minecraft.world.entity.Mob;

/**
 * Wrapper for vanilla shield users. Provides the same block cooldown
 * as {@link RecruitShieldmanEntity} so shield behaviour matches recruits.
 */
public class ShieldmanMobRecruit extends MobRecruit {

    public ShieldmanMobRecruit(Mob mob) {
        super(mob);
    }

    /**
     * Cooldown in ticks before the mob can raise its shield again.
     */
    public int getBlockCoolDown() {
        return 100;
    }
}
