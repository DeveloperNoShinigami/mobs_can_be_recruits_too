package com.talhanation.recruits.network;

import com.talhanation.recruits.entities.AbstractRecruitEntity;
import com.talhanation.recruits.entities.MobRecruit;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;

public class MessageDismountGui implements Message<MessageDismountGui> {

    private UUID uuid;
    private UUID player;

    public MessageDismountGui() {
    }

    public MessageDismountGui(UUID player, UUID uuid) {
        this.player = player;
        this.uuid = uuid;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer serverPlayer = Objects.requireNonNull(context.getSender());
        serverPlayer.getCommandSenderWorld().getEntitiesOfClass(
                Mob.class,
                serverPlayer.getBoundingBox().inflate(16.0D),
                (mob) -> mob.getUUID().equals(this.uuid) &&
                        (mob instanceof AbstractRecruitEntity || mob.getPersistentData().getBoolean("RecruitControlled"))
        ).forEach(mob -> {
            MobRecruit recruit = MobRecruit.get(mob);
            recruit.setShouldMount(false);
            if (mob.isPassenger()) {
                mob.stopRiding();
            }
        });
    }

    public MessageDismountGui fromBytes(FriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
        this.player = buf.readUUID();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeUUID(player);
    }
}