package com.talhanation.recruits.hooks;

import com.talhanation.recruits.entities.AbstractRecruitEntity;
import com.talhanation.recruits.config.RecruitsServerConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Tick handler updating basic stats for mobs converted through UniversalMobControl.
 */
public class ControlledMobHooks {

    @SubscribeEvent
    public void onMobTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (!(entity instanceof Mob mob) || mob instanceof AbstractRecruitEntity) return;

        CompoundTag tag = mob.getPersistentData();
        if (!tag.getBoolean("RecruitControlled")) return;

        updateTimers(mob, tag);
        updateHunger(mob, tag);
        updateMorale(mob, tag);
        updateExperience(mob, tag);
    }

    private void updateTimers(Mob mob, CompoundTag tag) {
        if (!RecruitsServerConfig.RecruitsPayment.get()) return;
        int timer = tag.contains("paymentTimer") ? tag.getInt("paymentTimer") : 0;
        if (timer > 0) {
            timer--;
        } else {
            timer = 20 * 60 * RecruitsServerConfig.RecruitsPaymentInterval.get();
        }
        tag.putInt("paymentTimer", timer);
    }

    private void updateHunger(Mob mob, CompoundTag tag) {
        float hunger = tag.contains("Hunger") ? tag.getFloat("Hunger") : 50F;
        int followState = tag.contains("FollowState") ? tag.getInt("FollowState") : 0;
        hunger -= followState == 2 ? 2F / 60F : 3F / 60F;
        if (hunger < 0F) hunger = 0F;
        tag.putFloat("Hunger", hunger);

        if (hunger >= 70F && mob.getHealth() < mob.getMaxHealth()) {
            mob.heal(1F / 50F);
        }
    }

    private void updateMorale(Mob mob, CompoundTag tag) {
        float morale = tag.contains("Moral") ? tag.getFloat("Moral") : 50F;
        float hunger = tag.getFloat("Hunger");
        boolean owned = tag.getBoolean("Owned");

        if (hunger <= 1F && owned && morale > 0F) {
            morale -= 2F;
        }
        if (owned && hunger < 90F && morale > 35F) {
            morale -= 1F;
        }
        if ((hunger >= 90F || mob.getHealth() >= mob.getMaxHealth() * 0.85F) && morale < 65F) {
            morale += 2F;
        }
        if (morale < 0F) morale = 0F;
        if (morale > 100F) morale = 100F;
        tag.putFloat("Moral", morale);
    }

    private void updateExperience(Mob mob, CompoundTag tag) {
        int xp = tag.contains("Xp") ? tag.getInt("Xp") : 0;
        int level = tag.contains("Level") ? tag.getInt("Level") : 1;
        int maxLevel = RecruitsServerConfig.RecruitsMaxXpLevel.get();
        if (xp >= RecruitsServerConfig.RecruitsMaxXpForLevelUp.get()) {
            xp = 0;
            if (level < maxLevel) {
                level++;
                mob.playSound(SoundEvents.PLAYER_LEVELUP, 1F, 0.8F + mob.getRandom().nextFloat() * 0.4F);
            }
        }
        tag.putInt("Xp", xp);
        tag.putInt("Level", level);
    }
}
