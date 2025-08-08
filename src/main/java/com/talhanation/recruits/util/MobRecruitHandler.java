package com.talhanation.recruits.util;

import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.TeamEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Default implementation of {@link RecruitHandler} that reuses the
 * existing recruit GUI and behaviour for arbitrary mobs. It also mirrors
 * recruit right-click state cycling so converted mobs can follow, hold, and
 * wander just like standard recruits.
 */
public class MobRecruitHandler implements RecruitHandler {
    @Override
    public void handle(PlayerInteractEvent event, Mob mob) {
        CompoundTag nbt = mob.getPersistentData();
        Player player = event.getEntity();
        if (nbt.getBoolean("RecruitControlled")) {
            RecruitEventsAccessor.restoreControlledMobInventory(mob);
        } else if (TeamEvents.isControlledMob(mob.getType())) {
            RecruitEventsAccessor.initializeControlledMob(mob);
        } else {
            player.sendSystemMessage(Component.literal(
                    "This mob cannot be recruited. Add its id to ControlledMobIds or enable UniversalMobControl."));
        }
        if (!nbt.getBoolean("RecruitControlled")) return;

        ItemStack currency = TeamEvents.getCurrencyForMob(mob.getType());

        if (!nbt.getBoolean("Owned")) {
            int cost = nbt.getInt("HireCost");
            if (event.getItemStack().is(currency.getItem()) && event.getItemStack().getCount() >= cost) {
                event.getItemStack().shrink(cost);
                nbt.putBoolean("Owned", true);
                nbt.putUUID("Owner", player.getUUID());
                nbt.putInt("FollowState", 1);
                nbt.putInt("AggroState", 3); // start passive after recruitment
                nbt.putBoolean("Listen", true);
                RecruitEventsAccessor.resetControlledMobPaymentTimer(mob);
                if (mob instanceof PathfinderMob pathfinderMob) {
                    RecruitEventsAccessor.applyControlledMobGoals(pathfinderMob);
                }
                player.sendSystemMessage(Component.literal("Mob recruited"));
                CommandEvents.openMobInventoryScreen(player, mob);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        } else if (nbt.getBoolean("Owned") && nbt.contains("Owner") && nbt.getUUID("Owner").equals(player.getUUID())) {
            if (player.isCrouching()) {
                CommandEvents.openMobInventoryScreen(player, mob);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            String name = mob.getName().getString();
            int state = nbt.getInt("FollowState");
            switch (state) {
                // default includes 0,2,3,5,... -> switch to follow
                default -> {
                    nbt.putInt("FollowState", 1);
                    clearHoldPos(nbt);
                    player.sendSystemMessage(Component.translatable("chat.recruits.text.follow", name));
                }
                // follow -> hold at player position
                case 1 -> {
                    nbt.putInt("FollowState", 4);
                    nbt.putDouble("HoldX", player.getX());
                    nbt.putDouble("HoldY", player.getY());
                    nbt.putDouble("HoldZ", player.getZ());
                    player.sendSystemMessage(Component.translatable("chat.recruits.text.holdPos", name));
                }
                // hold -> wander
                case 4 -> {
                    nbt.putInt("FollowState", 0);
                    clearHoldPos(nbt);
                    player.sendSystemMessage(Component.translatable("chat.recruits.text.wander", name));
                }
            }

            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    private static void clearHoldPos(CompoundTag nbt) {
        nbt.remove("HoldX");
        nbt.remove("HoldY");
        nbt.remove("HoldZ");
    }

    /**
     * Helper accessors exposing selected RecruitEvents methods without making
     * them part of the public API directly. This keeps the extraction local
     * while allowing reuse.
     */
    public static final class RecruitEventsAccessor {
        private RecruitEventsAccessor() {}

        public static void restoreControlledMobInventory(Mob mob) {
            com.talhanation.recruits.RecruitEvents.restoreControlledMobInventory(mob);
        }

        public static void initializeControlledMob(Mob mob) {
            com.talhanation.recruits.RecruitEvents.initializeControlledMob(mob);
        }

        public static void resetControlledMobPaymentTimer(Mob mob) {
            com.talhanation.recruits.RecruitEvents.resetControlledMobPaymentTimer(mob);
        }

        public static void applyControlledMobGoals(PathfinderMob pathfinderMob) {
            com.talhanation.recruits.RecruitEvents.applyControlledMobGoals(pathfinderMob);
        }

        public static void doControlledNoPaymentAction(Mob mob) {
            com.talhanation.recruits.RecruitEvents.doControlledNoPaymentAction(mob);
        }

        public static void applyCompanionProfession(Mob mob) {
            com.talhanation.recruits.RecruitEvents.applyCompanionProfession(mob);
        }
    }
}
