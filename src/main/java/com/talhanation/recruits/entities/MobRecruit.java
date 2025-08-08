package com.talhanation.recruits.entities;

import com.talhanation.recruits.config.RecruitsServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.InteractionHand;

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

    private static final String KEY_OWNED = "Owned";
    private static final String KEY_OWNER = "Owner";
    private static final String KEY_GROUP = "Group";
    private static final String KEY_FOLLOW_STATE = "FollowState";
    private static final String KEY_AGGRO_STATE = "AggroState";
    private static final String KEY_SHOULD_FOLLOW = "ShouldFollow";
    private static final String KEY_PAYMENT_TIMER = "paymentTimer";
    private static final String KEY_UPKEEP_TIMER = "upkeepTimer";
    private static final String KEY_MOUNT_TIMER = "mountTimer";

    private static final String KEY_HOLD_X = "HoldX";
    private static final String KEY_HOLD_Y = "HoldY";
    private static final String KEY_HOLD_Z = "HoldZ";
    private static final String KEY_SHOULD_HOLD_POS = "ShouldHoldPos";
    private static final String KEY_FLEEING = "Fleeing";
    private static final String KEY_SHOULD_MOUNT = "ShouldMount";
    private static final String KEY_ROTATE = "Rotate";
    private static final String KEY_OWNER_ROT = "OwnerRot";
    private static final String KEY_HUNGER = "Hunger";
    private static final String KEY_MORAL = "Moral";
    private static final String KEY_MOVE_SPEED = "MoveSpeed";
    private static final String KEY_UPKEEP_UUID = "UpkeepUUID";
    private static final String KEY_UPKEEP_POS_X = "UpkeepPosX";
    private static final String KEY_UPKEEP_POS_Y = "UpkeepPosY";
    private static final String KEY_UPKEEP_POS_Z = "UpkeepPosZ";
    private static final String KEY_XP = "Xp";
    private static final String KEY_LEVEL = "Level";
    private static final String KEY_KILLS = "Kills";

    private final Mob mob;
    private final SimpleContainer inventory = new SimpleContainer(15);
    private int beforeItemSlot = -1;

    public MobRecruit(Mob mob) {
        this.mob = mob;
    }

    private CompoundTag data() {
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

    protected float getFloat(String key) {
        return data().getFloat(key);
    }

    protected void setFloat(String key, float value) {
        data().putFloat(key, value);
    }

    protected double getDouble(String key) {
        return data().getDouble(key);
    }

    protected void setDouble(String key, double value) {
        data().putDouble(key, value);
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
            data().putLong(key, pos.asLong());
        }
    }

    @Override
    public Mob getMob() {
        return mob;
    }

    // ---------------------------------------------------------------------
    // Basic recruit data
    // ---------------------------------------------------------------------

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

    public int getAggroState() {
        return data().contains(KEY_AGGRO_STATE) ? getInt(KEY_AGGRO_STATE) : 0;
    }

    public void setAggroState(int state) {
        setInt(KEY_AGGRO_STATE, state);
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

    public int getXp() {
        return getInt(KEY_XP);
    }

    public void setXp(int xp) {
        setInt(KEY_XP, xp);
    }

    public int getXpLevel() {
        return getInt(KEY_LEVEL);
    }

    public void setXpLevel(int level) {
        setInt(KEY_LEVEL, level);
    }

    public void addXp(int xp) {
        setXp(getXp() + xp);
    }

    public void addXpLevel(int level) {
        setXpLevel(getXpLevel() + level);
    }

    public int getKills() {
        return getInt(KEY_KILLS);
    }

    public void setKills(int kills) {
        setInt(KEY_KILLS, kills);
    }

    public void checkLevel() {
        if (getXp() >= RecruitsServerConfig.RecruitsMaxXpForLevelUp.get()) {
            addXpLevel(1);
            setXp(0);
        }
    }

    @Nullable
    public Container getCarrierInventory() {
        return mob instanceof InventoryCarrier carrier ? carrier.getInventory() : null;
    }

    // ---------------------------------------------------------------------
    // Inventory
    // ---------------------------------------------------------------------

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

    private boolean hasFoodInInv() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (canEatItemStack(inventory.getItem(i))) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------------
    // Hunger & morale
    // ---------------------------------------------------------------------

    @Override
    public void updateMorale() {
        float current = getMorale();
        float morale = current;

        if (isStarving() && isOwned()) {
            if (current > 0) morale -= 2F;
        }

        if (isOwned() && !isSaturated()) {
            if (current > 35) morale -= 1F;
        }

        if (isSaturated() || mob.getHealth() >= mob.getMaxHealth() * 0.85F) {
            if (current < 65) morale += 2F;
        }

        if (morale < 0) morale = 0;
        setMoral(morale);
    }

    @Override
    public void updateHunger() {
        if (!RecruitsServerConfig.RecruitHunger.get())
            return;
        float hunger = getHunger();
        if (getFollowState() == 2) {
            hunger -= 2F / 60F;
        } else {
            hunger -= 3F / 60F;
        }
        if (hunger < 0) hunger = 0;
        setHunger(hunger);
    }

    @Override
    public boolean needsToEat() {
        if (!RecruitsServerConfig.RecruitHunger.get())
            return false;
        if (getHunger() <= 50F) {
            return true;
        }
        if (getHunger() <= 70F && mob.getHealth() != mob.getMaxHealth() && mob.getTarget() == null && isOwned()) {
            return true;
        }
        return mob.getHealth() <= mob.getMaxHealth() * 0.30F && mob.getTarget() == null;
    }

    @Override
    public boolean isSaturated() {
        if (!RecruitsServerConfig.RecruitHunger.get())
            return true;
        return getHunger() >= 90F;
    }

    private boolean isStarving() {
        if (!RecruitsServerConfig.RecruitHunger.get())
            return false;
        return getHunger() <= 1F;
    }

    @Override
    public float getHunger() {
        return data().contains(KEY_HUNGER) ? getFloat(KEY_HUNGER) : 100F;
    }

    @Override
    public void setHunger(float value) {
        setFloat(KEY_HUNGER, value);
    }

    @Override
    public float getMorale() {
        return data().contains(KEY_MORAL) ? getFloat(KEY_MORAL) : 100F;
    }

    @Override
    public void setMoral(float value) {
        setFloat(KEY_MORAL, value);
    }

    // ---------------------------------------------------------------------
    // State flags
    // ---------------------------------------------------------------------

    @Override
    public Vec3 getHoldPos() {
        if (data().contains(KEY_HOLD_X) && data().contains(KEY_HOLD_Y) && data().contains(KEY_HOLD_Z)) {
            return new Vec3(
                    getDouble(KEY_HOLD_X),
                    getDouble(KEY_HOLD_Y),
                    getDouble(KEY_HOLD_Z)
            );
        }
        return null;
    }

    @Override
    public boolean getShouldHoldPos() {
        return getBoolean(KEY_SHOULD_HOLD_POS);
    }

    @Override
    public boolean getFleeing() {
        return getBoolean(KEY_FLEEING);
    }

    @Override
    public boolean needsToGetFood() {
        int timer = getUpkeepTimer();
        boolean needsToEat = needsToEat();
        boolean hasFood = hasFoodInInv();
        boolean isChest = data().contains(KEY_UPKEEP_POS_X) && data().contains(KEY_UPKEEP_POS_Y) && data().contains(KEY_UPKEEP_POS_Z);
        boolean isEntity = data().hasUUID(KEY_UPKEEP_UUID);
        return (!hasFood && timer == 0 && needsToEat) && (isChest || isEntity);
    }

    @Override
    public boolean getShouldMount() {
        return getBoolean(KEY_SHOULD_MOUNT);
    }

    @Override
    public double getMoveSpeed() {
        return data().contains(KEY_MOVE_SPEED) ? getDouble(KEY_MOVE_SPEED) : 1.0D;
    }

    @Override
    public boolean getRotate() {
        return getBoolean(KEY_ROTATE);
    }

    @Override
    public void setRotate(boolean rotate) {
        setBoolean(KEY_ROTATE, rotate);
    }

    @Override
    public float getOwnerRot() {
        return data().contains(KEY_OWNER_ROT) ? getFloat(KEY_OWNER_ROT) : 0F;
    }

    @Override
    public void setOwnerRot(float rot) {
        setFloat(KEY_OWNER_ROT, rot);
    }
}

