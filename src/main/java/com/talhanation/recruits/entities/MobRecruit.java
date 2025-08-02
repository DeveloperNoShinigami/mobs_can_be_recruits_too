package com.talhanation.recruits.entities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Wrapper that exposes recruit-style accessors for vanilla mobs using
 * their persistent NBT data. This allows command and GUI logic to treat
 * regular mobs the same as {@link AbstractRecruitEntity} instances.
 */
public class MobRecruit implements IRecruitMob {

    private final Mob mob;

    private final SimpleContainer inventory = new SimpleContainer(15);
    private int beforeItemSlot = -1;
    private float hunger = 100F;
    private float morale = 100F;

    public MobRecruit(Mob mob) {
        this.mob = mob;
    }

    private CompoundTag data() {
        return mob.getPersistentData();
    }

    @Override
    public Mob getMob() {
        return mob;
    }

    // basic recruit data ----------------------------------------------------

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

    // inventory -------------------------------------------------------------

    @Override
    public SimpleContainer getInventory() {
        return inventory;
    }

    @Override
    public int getInventorySize() {
        return inventory.getContainerSize();
    }

    @Override
    public void setBeforeItemSlot(int slot) {
        beforeItemSlot = slot;
    }

    @Override
    public int getBeforeItemSlot() {
        return beforeItemSlot;
    }

    @Override
    public void resetItemInHand() {
        ItemStack foodStack = mob.getOffhandItem().copy();
        ItemStack before = ItemStack.EMPTY;
        if (beforeItemSlot >= 0 && beforeItemSlot < inventory.getContainerSize()) {
            before = inventory.getItem(beforeItemSlot).copy();
            inventory.removeItemNoUpdate(beforeItemSlot);
        }
        mob.setItemInHand(InteractionHand.OFF_HAND, before);
        if (beforeItemSlot >= 0 && beforeItemSlot < inventory.getContainerSize()) {
            inventory.setItem(beforeItemSlot, foodStack);
        }
        beforeItemSlot = -1;
    }

    @Override
    public boolean canEatItemStack(ItemStack stack) {
        return stack.isEdible();
    }

    // hunger & morale -------------------------------------------------------

    @Override
    public void updateMorale() {
    }

    @Override
    public void updateHunger() {
    }

    @Override
    public boolean needsToEat() {
        return hunger <= 70F;
    }

    @Override
    public boolean isSaturated() {
        return hunger >= 90F;
    }

    @Override
    public float getHunger() {
        return hunger;
    }

    @Override
    public void setHunger(float value) {
        hunger = value;
    }

    @Override
    public float getMorale() {
        return morale;
    }

    @Override
    public void setMoral(float value) {
        morale = value;
    }

    // state flags -----------------------------------------------------------

    @Override
    public Vec3 getHoldPos() {
        return null;
    }

    @Override
    public boolean getShouldHoldPos() {
        return false;
    }

    @Override
    public boolean getFleeing() {
        return false;
    }

    @Override
    public boolean needsToGetFood() {
        return false;
    }

    @Override
    public boolean getShouldMount() {
        return false;
    }

    @Override
    public double getMoveSpeed() {
        return 1.0D;
    }

    @Override
    public boolean getRotate() {
        return false;
    }

    @Override
    public void setRotate(boolean rotate) {
    }

    @Override
    public float getOwnerRot() {
        return 0;
    }

    @Override
    public void setOwnerRot(float rot) {
    }
}

