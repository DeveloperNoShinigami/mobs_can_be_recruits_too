package com.talhanation.recruits.compat;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import com.talhanation.recruits.config.RecruitsServerConfig;

public interface IWeapon {
    Item getWeapon();
    double getMoveSpeedAmp();
    int getAttackCooldown();
    int getWeaponLoadTime();
    float getProjectileSpeed();
    AbstractHurtingProjectile getProjectile(LivingEntity shooter);
    AbstractArrow getProjectileArrow(LivingEntity shooter);
    AbstractHurtingProjectile shoot(LivingEntity shooter, AbstractHurtingProjectile projectile, double x, double y, double z);
    AbstractArrow shootArrow(LivingEntity shooter, AbstractArrow projectile, double x, double y, double z);

    SoundEvent getShootSound();
    SoundEvent getLoadSound();
    boolean isGun();
    boolean canMelee();
    boolean isBow();
    boolean isCrossBow();
    void performRangedAttackIWeapon(LivingEntity shooter, double x, double y, double z, float projectileSpeed);

    static boolean isMusketModWeapon(ItemStack stack){
        return stack.getDescriptionId().equals("item.musketmod.musket") ||
                stack.getDescriptionId().equals("item.musketmod.musket_with_bayonet") ||
                stack.getDescriptionId().equals("item.musketmod.musket_with_scope") ||
                stack.getDescriptionId().equals("item.musketmod.blunderbuss") ||
                stack.getDescriptionId().equals("item.musketmod.cartridge") ||
                stack.getDescriptionId().equals("item.musketmod.pistol");
    }

    static boolean isCGMWeapon(ItemStack stack){
        String mod = stack.getItem().getCreatorModId(stack);
        if(mod != null && mod.equals("cgm")) return true;
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key != null && RecruitsServerConfig.AdditionalGunItems.get().contains(key.toString());
    }

    boolean isLoaded(ItemStack stack);

    void setLoaded(ItemStack stack, boolean loaded);
}
