package com.talhanation.recruits.entities;

import net.minecraft.world.entity.Mob;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Wrapper that exposes recruit-style accessors for vanilla mobs using
 * their persistent NBT data. This allows command and GUI logic to treat
 * regular mobs the same as {@link AbstractRecruitEntity} instances.
 */
public class MobRecruit implements IRecruitEntity {

    private final Mob mob;

    public MobRecruit(Mob mob) {
        this.mob = mob;
    }

    private CompoundTag data() {
        return mob.getPersistentData();
    }

    @Override
    public boolean isOwned() {
        return data().getBoolean("Owned");
    }

    @Override
    public void setIsOwned(boolean owned) {
        data().putBoolean("Owned", owned);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return data().hasUUID("Owner") ? data().getUUID("Owner") : null;
    }

    @Override
    public void setOwnerUUID(@Nullable UUID uuid) {
        if (uuid == null) {
            data().remove("Owner");
        } else {
            data().putUUID("Owner", uuid);
        }
    }

    @Override
    public int getGroup() {
        return data().getInt("Group");
    }

    @Override
    public void setGroup(int group) {
        data().putInt("Group", group);
    }

    public Mob getMob() {
        return mob;
    }
}

