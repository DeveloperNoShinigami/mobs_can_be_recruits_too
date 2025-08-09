package com.talhanation.recruits.network;

import com.talhanation.recruits.config.RecruitsServerConfig;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import com.talhanation.recruits.entities.MobRecruit;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;

public class MessageMountEntityGui implements Message<MessageMountEntityGui> {
    private UUID recruit;
    private boolean back;

    public MessageMountEntityGui() {
    }

    public MessageMountEntityGui(UUID recruit, boolean back) {
        this.recruit = recruit;
        this.back = back;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @SuppressWarnings({"all"})
    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer player = Objects.requireNonNull(context.getSender());

        player.getCommandSenderWorld().getEntitiesOfClass(
                Mob.class,
                player.getBoundingBox().inflate(32.0D),
                v -> v.getUUID().equals(this.recruit) && v.isAlive() &&
                        (v instanceof AbstractRecruitEntity || v.getPersistentData().getBoolean("RecruitControlled"))
        ).forEach(m -> mount(player, m));
    }

    @SuppressWarnings({"all"})
    private void mount(ServerPlayer player, Mob mob) {
        MobRecruit recruit = MobRecruit.get(mob);
        if (this.back && mob.getPersistentData().hasUUID("MountUUID")) {
            recruit.setShouldMount(true);
        } else if (mob.getVehicle() == null) {
            List<Entity> list = mob.getCommandSenderWorld().getEntitiesOfClass(
                    Entity.class,
                    mob.getBoundingBox().inflate(8),
                    (mount) -> !(mount instanceof AbstractHorse horse &&
                            horse.hasControllingPassenger()) &&
                            RecruitsServerConfig.MountWhiteList.get().contains(mount.getEncodeId())
            );

            double d0 = -1.0D;
            Entity horse = null;

            for (Entity entity : list) {
                double d1 = entity.distanceToSqr(mob);
                if (d0 == -1.0D || d1 < d0) {
                    horse = entity;
                    d0 = d1;
                }
            }

            if (horse == null) {
                player.sendSystemMessage(TEXT_NO_MOUNT(mob.getName().getString()));
                return;
            }

            recruit.setShouldMount(true);
            mob.getPersistentData().putUUID("MountUUID", horse.getUUID());
        }
    }

    public MessageMountEntityGui fromBytes(FriendlyByteBuf buf) {
        this.recruit = buf.readUUID();
        this.back = buf.readBoolean();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.recruit);
        buf.writeBoolean(this.back);
    }

    private static MutableComponent TEXT_NO_MOUNT(String name) {
        return Component.translatable("chat.recruits.text.noMount", name);
    }
}
