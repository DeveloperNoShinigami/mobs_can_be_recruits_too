package com.talhanation.recruits.entities.ai.compat;

import com.talhanation.recruits.compat.BlunderbussWeapon;
import com.talhanation.recruits.compat.CGMWeapon;
import com.talhanation.recruits.compat.IWeapon;
import com.talhanation.recruits.compat.MusketBayonetWeapon;
import com.talhanation.recruits.compat.MusketScopeWeapon;
import com.talhanation.recruits.compat.MusketWeapon;
import com.talhanation.recruits.compat.PistolWeapon;
import com.talhanation.recruits.entities.IRecruitEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Musket attack goal for controlled mobs, reusing recruit musket logic.
 */
public class ControlledMobRangedMusketAttackGoal extends Goal {
    private final PathfinderMob mob;
    private LivingEntity target;
    private IWeapon weapon;
    private int weaponLoadTime;
    private int seeTime;
    private State state;
    private final double stopRange;

    public ControlledMobRangedMusketAttackGoal(PathfinderMob mob, double stopRange) {
        this.mob = mob;
        this.weapon = new MusketWeapon();
        this.stopRange = stopRange;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private boolean isActive() {
        CompoundTag nbt = mob.getPersistentData();
        IRecruitEntity recruit = IRecruitEntity.of(mob);
        if (!nbt.getBoolean("RecruitControlled") || !recruit.isOwned())
            return false;
        if (nbt.getBoolean("ShouldRest"))
            return false;
        if (!nbt.getBoolean("ShouldRanged"))
            return false;
        return nbt.getInt("AggroState") != 3;
    }

    @Override
    public boolean canUse() {
        if (!isActive()) return false;
        LivingEntity t = mob.getTarget();
        if (t != null && t.isAlive() && isWeaponInHand()) {
            return t.distanceTo(mob) >= stopRange;
        }
        CompoundTag nbt = mob.getPersistentData();
        return nbt.getBoolean("ShouldStrategicFire") || (isWeaponInHand() && weapon != null && !weapon.isLoaded(mob.getMainHandItem()));
    }

    @Override
    public boolean canContinueToUse() {
        if (!isActive()) return false;
        return canUse();
    }

    @Override
    public void start() {
        mob.setAggressive(true);
        this.state = State.IDLE;
        this.weaponLoadTime = weapon.getWeaponLoadTime();
    }

    @Override
    public void stop() {
        this.seeTime = 0;
        this.weaponLoadTime = 0;
        mob.stopUsingItem();
        mob.setAggressive(false);
    }

    protected boolean isWeaponInHand() {
        ItemStack itemStack = mob.getItemBySlot(EquipmentSlot.MAINHAND);
        String id = itemStack.getDescriptionId();
        if (id.equals("item.musketmod.musket")) {
            this.weapon = new MusketWeapon();
            return true;
        } else if (id.equals("item.musketmod.musket_with_bayonet")) {
            this.weapon = new MusketBayonetWeapon();
            return true;
        } else if (id.equals("item.musketmod.musket_with_scope")) {
            this.weapon = new MusketScopeWeapon();
            return true;
        } else if (id.equals("item.musketmod.blunderbuss")) {
            this.weapon = new BlunderbussWeapon();
            return true;
        } else if (id.equals("item.musketmod.pistol")) {
            this.weapon = new PistolWeapon();
            return true;
        } else if (IWeapon.isCGMWeapon(itemStack)) {
            this.weapon = new CGMWeapon();
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        target = mob.getTarget();
        CompoundTag nbt = mob.getPersistentData();
        BlockPos strategicPos = null;
        if (nbt.getBoolean("ShouldStrategicFire")) {
            strategicPos = new BlockPos(nbt.getInt("StrategicFireX"), nbt.getInt("StrategicFireY"), nbt.getInt("StrategicFireZ"));
        }

        if (!isWeaponInHand()) return;

        if (target != null && target.isAlive()) {
            boolean canSee = mob.getSensing().hasLineOfSight(target);
            if (canSee) {
                ++this.seeTime;
            } else {
                this.seeTime = 0;
            }

            switch (state) {
                case IDLE -> {
                    mob.setAggressive(false);
                    State newState;
                    if (!weapon.isLoaded(mob.getMainHandItem())) {
                        newState = canLoad() ? State.RELOAD : State.IDLE;
                    } else if (seeTime > 0) {
                        newState = State.AIMING;
                    } else {
                        newState = State.IDLE;
                    }
                    this.state = newState;
                }
                case RELOAD -> {
                    mob.startUsingItem(InteractionHand.MAIN_HAND);
                    int i = mob.getTicksUsingItem();
                    if (i >= this.weaponLoadTime) {
                        mob.releaseUsingItem();
                        mob.playSound(this.weapon.getLoadSound(), 1.0F, 1.0F / (mob.getRandom().nextFloat() * 0.4F + 0.8F));
                        this.weapon.setLoaded(mob.getMainHandItem(), true);
                        this.consumeAmmo();
                        state = State.AIMING;
                    }
                }
                case AIMING -> {
                    mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                    mob.setAggressive(true);
                    this.seeTime++;
                    if (this.seeTime >= 15 + mob.getRandom().nextInt(15)) {
                        this.seeTime = 0;
                        this.state = State.SHOOT;
                    }
                }
                case SHOOT -> {
                    mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                    this.weapon.performRangedAttackIWeapon(mob, target.getX(), target.getY(), target.getZ(), weapon.getProjectileSpeed());
                    this.weapon.setLoaded(mob.getMainHandItem(), false);
                    this.state = canLoad() ? State.RELOAD : State.IDLE;
                }
            }
        } else if (strategicPos != null) {
            switch (state) {
                case IDLE -> {
                    mob.setAggressive(false);
                    State newState;
                    if (!weapon.isLoaded(mob.getMainHandItem())) {
                        newState = canLoad() ? State.RELOAD : State.IDLE;
                    } else {
                        newState = State.AIMING;
                    }
                    this.state = newState;
                }
                case RELOAD -> {
                    mob.startUsingItem(InteractionHand.MAIN_HAND);
                    int i = mob.getTicksUsingItem();
                    if (i >= this.weaponLoadTime) {
                        mob.releaseUsingItem();
                        mob.playSound(this.weapon.getLoadSound(), 1.0F, 1.0F / (mob.getRandom().nextFloat() * 0.4F + 0.8F));
                        this.weapon.setLoaded(mob.getMainHandItem(), true);
                        this.consumeAmmo();
                        state = State.AIMING;
                    }
                }
                case AIMING -> {
                    mob.getLookControl().setLookAt(Vec3.atCenterOf(strategicPos));
                    mob.setAggressive(true);
                    this.seeTime++;
                    if (this.seeTime >= 15 + mob.getRandom().nextInt(15)) {
                        this.seeTime = 0;
                        this.state = State.SHOOT;
                    }
                }
                case SHOOT -> {
                    mob.getLookControl().setLookAt(Vec3.atCenterOf(strategicPos));
                    this.weapon.performRangedAttackIWeapon(mob, strategicPos.getX(), strategicPos.getY(), strategicPos.getZ(), weapon.getProjectileSpeed());
                    this.weapon.setLoaded(mob.getMainHandItem(), false);
                    this.state = canLoad() ? State.RELOAD : State.IDLE;
                }
            }
        }
    }

    private void consumeAmmo() {
        CompoundTag data = mob.getPersistentData();
        if (!data.contains("MobInventory")) return;
        ListTag list = data.getList("MobInventory", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag ct = list.getCompound(i);
            ItemStack stack = ItemStack.of(ct);
            if (stack.getDescriptionId().equals("item.musketmod.cartridge")) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    list.remove(i);
                } else {
                    stack.save(ct);
                }
                data.put("MobInventory", list);
                break;
            }
        }
    }

    private boolean canLoad() {
        CompoundTag data = mob.getPersistentData();
        if (!data.contains("MobInventory")) return false;
        ListTag list = data.getList("MobInventory", 10);
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = ItemStack.of(list.getCompound(i));
            if (stack.getDescriptionId().equals("item.musketmod.cartridge")) return true;
        }
        return false;
    }

    private enum State {
        IDLE,
        RELOAD,
        AIMING,
        SHOOT
    }
}
