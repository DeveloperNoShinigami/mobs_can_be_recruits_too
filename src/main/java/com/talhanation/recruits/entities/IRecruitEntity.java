package com.talhanation.recruits.entities;

import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Common interface for recruit data shared between {@link AbstractRecruitEntity}
 * and generic controlled mobs. Provides owner, group and ownership accessors
 * so logic can operate on either type transparently.
 */
public interface IRecruitEntity {

    boolean isOwned();

    void setIsOwned(boolean owned);

    @Nullable
    UUID getOwnerUUID();

    void setOwnerUUID(@Nullable UUID uuid);

    int getGroup();

    void setGroup(int group);

    /**
     * Convenience helper to query ownership by UUID.
     */
    default boolean isOwnedBy(UUID uuid) {
        UUID owner = getOwnerUUID();
        return owner != null && owner.equals(uuid);
    }

    /**
     * Obtain a recruit abstraction for the given mob. Recruits implement this
     * interface directly while other mobs are wrapped in a {@link MobRecruit}.
     */
    static IRecruitEntity of(Mob mob) {
        return mob instanceof IRecruitEntity r ? r : MobRecruit.get(mob);
    }
}

