package com.talhanation.recruits.entities;

import net.minecraft.world.entity.Mob;

/**
 * Wrapper for vanilla mobs that mirror nomad recruit behaviour.
 * Nomads are mobile bowmen and therefore reuse {@link BowmanMobRecruit} logic.
 */
public class NomadMobRecruit extends BowmanMobRecruit {

    public NomadMobRecruit(Mob mob) {
        super(mob);
    }
}
