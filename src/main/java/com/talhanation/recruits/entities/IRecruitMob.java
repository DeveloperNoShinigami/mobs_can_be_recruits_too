package com.talhanation.recruits.entities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Extension of {@link IRecruitEntity} that exposes gameplay critical state
 * used by recruit AI goals. It allows vanilla {@link Mob} instances to be
 * wrapped via {@link MobRecruit} so the same goals can operate on them.
 */
public interface IRecruitMob extends IRecruitEntity {

    /**
     * @return underlying mob instance for this recruit abstraction.
     */
    Mob getMob();

    /**
     * Convenience method mirroring {@link Mob#getCommandSenderWorld()}.
     */
    default Level getCommandSenderWorld() {
        return getMob().level();
    }

    // --- Inventory helpers ---
    SimpleContainer getInventory();
    int getInventorySize();
    void setBeforeItemSlot(int slot);
    int getBeforeItemSlot();
    void resetItemInHand();
    boolean canEatItemStack(ItemStack stack);

    // --- Hunger and morale ---
    void updateMorale();
    void updateHunger();
    boolean needsToEat();
    boolean isSaturated();
    float getHunger();
    void setHunger(float value);
    float getMorale();
    void setMoral(float value);

    // --- State flags ---
    Vec3 getHoldPos();
    boolean getShouldHoldPos();
    boolean getFleeing();
    boolean needsToGetFood();
    boolean getShouldMount();
    double getMoveSpeed();
    boolean getRotate();
    void setRotate(boolean rotate);
    float getOwnerRot();
    void setOwnerRot(float rot);

    // --- Delegates to the underlying mob ---
    default ItemStack getOffhandItem() {
        return getMob().getOffhandItem();
    }

    default void setItemInHand(InteractionHand hand, ItemStack stack) {
        getMob().setItemInHand(hand, stack);
    }

    default void startUsingItem(InteractionHand hand) {
        getMob().startUsingItem(hand);
    }

    default void stopUsingItem() {
        getMob().stopUsingItem();
    }

    default boolean isUsingItem() {
        return getMob().isUsingItem();
    }

    default void heal(float amount) {
        getMob().heal(amount);
    }

    default PathNavigation getNavigation() {
        return getMob().getNavigation();
    }

    default JumpControl getJumpControl() {
        return getMob().getJumpControl();
    }

    /**
     * Obtain a recruit abstraction for the given mob, wrapping vanilla mobs
     * in a {@link MobRecruit} adapter.
     */
    static IRecruitMob of(Mob mob) {
        return mob instanceof IRecruitMob r ? r : new MobRecruit(mob);
    }
}

