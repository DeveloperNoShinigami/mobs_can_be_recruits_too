package com.talhanation.recruits.network;

import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MessageStrategicFire implements Message<MessageStrategicFire> {

    private UUID player;
    private int group;
    private boolean should;

    public MessageStrategicFire() {
    }

    public MessageStrategicFire(UUID player, int group, boolean should) {
        this.player = player;
        this.group = group;
        this.should = should;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer serverPlayer = Objects.requireNonNull(context.getSender());
        List<Mob> mobs = serverPlayer.getCommandSenderWorld().getEntitiesOfClass(Mob.class,
                serverPlayer.getBoundingBox().inflate(100),
                m -> {
                    if (m instanceof AbstractRecruitEntity recruit) {
                        return recruit.isEffectedByCommand(this.player, group);
                    }
                    return m.getPersistentData().getBoolean("RecruitControlled") &&
                            m.getPersistentData().getBoolean("Owned") &&
                            m.getPersistentData().getUUID("Owner").equals(this.player) &&
                            (m.getPersistentData().getInt("Group") == this.group || this.group == 0);
                });
        for (Mob mob : mobs) {
            if (mob instanceof AbstractRecruitEntity recruit) {
                CommandEvents.onStrategicFireCommand(serverPlayer, this.player, recruit, group, should);
            } else {
                CommandEvents.onStrategicFireCommand(serverPlayer, this.player, mob, group, should);
            }
        }
    }

    public MessageStrategicFire fromBytes(FriendlyByteBuf buf) {
        this.player = buf.readUUID();
        this.group = buf.readInt();
        this.should = buf.readBoolean();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.player);
        buf.writeInt(this.group);
        buf.writeBoolean(this.should);
    }
}