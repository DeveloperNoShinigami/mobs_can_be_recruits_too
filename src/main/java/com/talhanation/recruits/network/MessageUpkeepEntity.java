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

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MessageUpkeepEntity implements Message<MessageUpkeepEntity> {

    private UUID player_uuid;
    private UUID target;
    private int group;

    public MessageUpkeepEntity() {
    }

    public MessageUpkeepEntity(UUID player_uuid, UUID target, int group) {
        this.player_uuid = player_uuid;
        this.target = target;
        this.group = group;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer player = Objects.requireNonNull(context.getSender());
        List<Mob> mobs = player.getCommandSenderWorld().getEntitiesOfClass(Mob.class,
                context.getSender().getBoundingBox().inflate(100),
                m -> {
                    if (m instanceof AbstractRecruitEntity recruit) {
                        return recruit.isEffectedByCommand(player_uuid, group);
                    }
                    IRecruitEntity recruit = IRecruitEntity.of(m);
                    return m.getPersistentData().getBoolean("RecruitControlled") &&
                            recruit.isOwned() &&
                            recruit.isOwnedBy(player_uuid) &&
                            (recruit.getGroup() == this.group || this.group == 0);
                });
        for (Mob mob : mobs) {
            if (mob instanceof AbstractRecruitEntity recruit) {
                CommandEvents.onUpkeepCommand(player_uuid, (IRecruitEntity) recruit, group, true, target, null);
            } else {
                CommandEvents.onUpkeepCommand(player_uuid, mob, group, true, target, null);
            }
        }
    }

    public MessageUpkeepEntity fromBytes(FriendlyByteBuf buf) {
        this.player_uuid = buf.readUUID();
        this.target = buf.readUUID();
        this.group = buf.readInt();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(player_uuid);
        buf.writeUUID(target);
        buf.writeInt(group);
    }
}