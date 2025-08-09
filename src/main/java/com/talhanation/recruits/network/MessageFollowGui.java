package com.talhanation.recruits.network;

import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.Main;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import com.talhanation.recruits.entities.IRecruitEntity;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Objects;
import java.util.UUID;

public class MessageFollowGui implements Message<MessageFollowGui> {

    private int state;
    private UUID uuid;

    public MessageFollowGui() {
    }

    public MessageFollowGui(int state, UUID uuid) {
        this.state = state;
        this.uuid = uuid;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer player = Objects.requireNonNull(context.getSender());
        player.getCommandSenderWorld().getEntitiesOfClass(
                Mob.class,
                player.getBoundingBox().inflate(16.0D),
                m -> m.getUUID().equals(this.uuid) && (
                        m instanceof AbstractRecruitEntity recruit
                                ? recruit.isEffectedByCommand(player.getUUID(), 0)
                                : m.getPersistentData().getBoolean("RecruitControlled"))
        ).forEach(m -> {
            CommandEvents.onMovementCommandGUI(IRecruitEntity.of(m), this.state);

            int aggroState;
            int followState;
            boolean listen;

            if (m instanceof AbstractRecruitEntity recruit) {
                aggroState = recruit.getState();
                followState = recruit.getFollowState();
                listen = recruit.getListen();
            } else {
                aggroState = m.getPersistentData().getInt("AggroState");
                followState = m.getPersistentData().getInt("FollowState");
                listen = m.getPersistentData().getBoolean("Listen");
            }

            Main.SIMPLE_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new MessageSyncMobFlags(m.getUUID(), aggroState, followState, listen, m.getClass().getName()));
        });
    }

    public MessageFollowGui fromBytes(FriendlyByteBuf buf) {
        this.state = buf.readInt();
        this.uuid = buf.readUUID();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(state);
        buf.writeUUID(uuid);
    }
}
