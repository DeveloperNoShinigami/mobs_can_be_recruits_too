package com.talhanation.recruits.network;

import com.talhanation.recruits.RecruitEvents;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;

public class MessageOpenControlledMobPromoteScreen implements Message<MessageOpenControlledMobPromoteScreen> {
    private UUID player;
    private UUID mobId;

    public MessageOpenControlledMobPromoteScreen() {
        this.player = new UUID(0, 0);
    }

    public MessageOpenControlledMobPromoteScreen(Player player, UUID mobId) {
        this.player = player.getUUID();
        this.mobId = mobId;
    }

    @Override
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer serverPlayer = Objects.requireNonNull(context.getSender());
        if (!serverPlayer.getUUID().equals(this.player)) {
            return;
        }
        serverPlayer.getCommandSenderWorld().getEntitiesOfClass(
                Mob.class,
                serverPlayer.getBoundingBox().inflate(16.0D),
                m -> !(m instanceof AbstractRecruitEntity) &&
                        m.getPersistentData().getBoolean("RecruitControlled") &&
                        m.getUUID().equals(this.mobId)
        ).forEach(m -> RecruitEvents.openControlledMobPromoteScreen(serverPlayer, m));
    }

    @Override
    public MessageOpenControlledMobPromoteScreen fromBytes(FriendlyByteBuf buf) {
        this.player = buf.readUUID();
        this.mobId = buf.readUUID();
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(player);
        buf.writeUUID(mobId);
    }
}
