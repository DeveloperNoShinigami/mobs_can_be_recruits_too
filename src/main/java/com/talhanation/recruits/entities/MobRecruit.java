package com.talhanation.recruits.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.InventoryCarrier;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Wrapper that exposes recruit-style accessors for vanilla mobs using
 * their persistent NBT data. This allows command and GUI logic to treat
 * regular mobs the same as {@link AbstractRecruitEntity} instances.
 */
public class MobRecruit implements IRecruitEntity {

    private static final String KEY_OWNED = "Owned";
    private static final String KEY_OWNER = "Owner";
    private static final String KEY_GROUP = "Group";
    private static final String KEY_FOLLOW_STATE = "FollowState";
    private static final String KEY_SHOULD_FOLLOW = "ShouldFollow";
    private static final String KEY_PAYMENT_TIMER = "paymentTimer";
    private static final String KEY_UPKEEP_TIMER = "upkeepTimer";
    private static final String KEY_MOUNT_TIMER = "mountTimer";

    private final Mob mob;

    public MobRecruit(Mob mob) {
        this.mob = mob;
    }

    protected CompoundTag data() {
        return mob.getPersistentData();
    }

    protected boolean getBoolean(String key) {
        return data().getBoolean(key);
    }

    protected void setBoolean(String key, boolean value) {
        data().putBoolean(key, value);
    }

    protected int getInt(String key) {
        return data().getInt(key);
    }

    protected void setInt(String key, int value) {
        data().putInt(key, value);
    }

    protected long getLong(String key) {
        return data().getLong(key);
    }

    protected void setLong(String key, long value) {
        data().putLong(key, value);
    }

    protected String getString(String key) {
        return data().getString(key);
    }

    protected void setString(String key, String value) {
        data().putString(key, value);
    }

    @Nullable
    protected BlockPos getBlockPos(String key) {
        return data().contains(key) ? BlockPos.of(data().getLong(key)) : null;
    }

    protected void setBlockPos(String key, @Nullable BlockPos pos) {
        if (pos == null) {
            data().remove(key);
        } else {
            setLong(key, pos.asLong());
        }
    }

    @Override
    public boolean isOwned() {
        return getBoolean(KEY_OWNED);
    }

    @Override
    public void setIsOwned(boolean owned) {
        setBoolean(KEY_OWNED, owned);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return data().hasUUID(KEY_OWNER) ? data().getUUID(KEY_OWNER) : null;
    }

    @Override
    public void setOwnerUUID(@Nullable UUID uuid) {
        if (uuid == null) {
            data().remove(KEY_OWNER);
        } else {
            data().putUUID(KEY_OWNER, uuid);
        }
    }

    @Override
    public int getGroup() {
        return getInt(KEY_GROUP);
    }

    @Override
    public void setGroup(int group) {
        setInt(KEY_GROUP, group);
    }

    public int getFollowState() {
        return getInt(KEY_FOLLOW_STATE);
    }

    public void setFollowState(int state) {
        setInt(KEY_FOLLOW_STATE, state);
    }

    public boolean getShouldFollow() {
        return getBoolean(KEY_SHOULD_FOLLOW);
    }

    public void setShouldFollow(boolean shouldFollow) {
        setBoolean(KEY_SHOULD_FOLLOW, shouldFollow);
    }

    public int getPaymentTimer() {
        return getInt(KEY_PAYMENT_TIMER);
    }

    public void setPaymentTimer(int timer) {
        setInt(KEY_PAYMENT_TIMER, timer);
    }

    public int getUpkeepTimer() {
        return getInt(KEY_UPKEEP_TIMER);
    }

    public void setUpkeepTimer(int timer) {
        setInt(KEY_UPKEEP_TIMER, timer);
    }

    public int getMountTimer() {
        return getInt(KEY_MOUNT_TIMER);
    }

    public void setMountTimer(int timer) {
        setInt(KEY_MOUNT_TIMER, timer);
    }

    @Nullable
    public Container getInventory() {
        return mob instanceof InventoryCarrier carrier ? carrier.getInventory() : null;
    }

    public Mob getMob() {
        return mob;
    }
}

