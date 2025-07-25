package com.talhanation.recruits.compat;

import com.talhanation.recruits.entities.AbstractRecruitEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Generic compatibility for MrCrayfish's Gun Mod (cgm).
 * This implementation uses basic arrow projectiles and does not require the mod at compile time.
 */
public class CGMWeapon implements IWeapon {
    @Override
    @Nullable
    public Item getWeapon() {
        return null; // handled via item instance
    }

    @Override
    public double getMoveSpeedAmp() { return 0.4D; }

    @Override
    public int getAttackCooldown() { return 20; }

    @Override
    public int getWeaponLoadTime() { return 20; }

    @Override
    public float getProjectileSpeed() { return 2.0F; }

    @Override
    public AbstractHurtingProjectile getProjectile(LivingEntity shooter) { return null; }

    @Override
    public AbstractArrow getProjectileArrow(LivingEntity shooter) { return new Arrow(shooter.level(), shooter); }

    @Override
    public AbstractHurtingProjectile shoot(LivingEntity shooter, AbstractHurtingProjectile projectile, double x, double y, double z) { return null; }

    @Override
    public AbstractArrow shootArrow(LivingEntity shooter, AbstractArrow projectile, double x, double y, double z) {
        double d0 = x - shooter.getX();
        double d1 = y - projectile.getY();
        double d2 = z - shooter.getZ();
        double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
        projectile.shoot(d0, d1 + d3 * 0.1D, d2, 2.5F, 0.1F);
        return projectile;
    }

    @Override
    public SoundEvent getShootSound() { return SoundEvents.GENERIC_EXPLODE; }

    @Override
    public SoundEvent getLoadSound() { return SoundEvents.CROSSBOW_LOADING_END; }

    @Override
    public boolean isGun() { return true; }

    @Override
    public boolean canMelee() { return false; }

    @Override
    public boolean isBow() { return false; }

    @Override
    public boolean isCrossBow() { return false; }

    @Override
    public void performRangedAttackIWeapon(AbstractRecruitEntity shooter, double x, double y, double z, float projectileSpeed) {
        AbstractArrow projectileEntity = this.getProjectileArrow(shooter);
        this.shootArrow(shooter, projectileEntity, x, y, z);
        shooter.playSound(this.getShootSound(), 1.0F, 1.0F);
        shooter.level().addFreshEntity(projectileEntity);
        shooter.damageMainHandItem();
    }

    @Override
    public boolean isLoaded(ItemStack stack) { return true; }

    @Override
    public void setLoaded(ItemStack stack, boolean loaded) {}
}
