package com.talhanation.recruits.network;

import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;

public class MessageUpkeepPos implements Message<MessageUpkeepPos> {

    private UUID player;
    private int group;
    private BlockPos pos;

    public MessageUpkeepPos() {
    }

    public MessageUpkeepPos(UUID player, int group, BlockPos pos) {
        this.player = player;
        this.group = group;
        this.pos = pos;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer player = Objects.requireNonNull(context.getSender());
        player.getCommandSenderWorld().getEntitiesOfClass(Mob.class,
                player.getBoundingBox().inflate(100),
                m -> {
                    if (m instanceof AbstractRecruitEntity recruit) {
                        return recruit.isEffectedByCommand(this.player, group);
                    }
                    return m.getPersistentData().getBoolean("RecruitControlled") &&
                            m.getPersistentData().getBoolean("Owned") &&
                            m.getPersistentData().getUUID("Owner").equals(this.player) &&
                            (m.getPersistentData().getInt("Group") == this.group || this.group == 0);
                }
        ).forEach(m -> {
            if (m instanceof AbstractRecruitEntity recruit) {
                CommandEvents.onUpkeepCommand(this.player, recruit, group, false, null, pos);
            } else {
                CommandEvents.onUpkeepCommand(this.player, m, group, false, null, pos);
            }
        });
    }

    public MessageUpkeepPos fromBytes(FriendlyByteBuf buf) {
        this.player = buf.readUUID();
        this.group = buf.readInt();
        this.pos = buf.readBlockPos();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.player);
        buf.writeInt(this.group);
        buf.writeBlockPos(this.pos);
    }
}