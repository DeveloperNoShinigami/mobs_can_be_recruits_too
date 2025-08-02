package com.talhanation.recruits.network;

import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import com.talhanation.recruits.entities.IRecruitEntity;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MessageClearUpkeep implements Message<MessageClearUpkeep> {
    private UUID uuid;
    private int group;

    public MessageClearUpkeep() {
    }

    public MessageClearUpkeep(UUID uuid, int group) {
        this.uuid = uuid;
        this.group = group;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        List<Mob> mobs = Objects.requireNonNull(context.getSender()).getCommandSenderWorld().getEntitiesOfClass(Mob.class,
                context.getSender().getBoundingBox().inflate(100),
                m -> {
                    if (m instanceof AbstractRecruitEntity recruit) {
                        return recruit.isEffectedByCommand(uuid, group);
                    }
                    IRecruitEntity recruit = IRecruitEntity.of(m);
                    return m.getPersistentData().getBoolean("RecruitControlled") &&
                            recruit.isOwned() &&
                            recruit.isOwnedBy(uuid) &&
                            (recruit.getGroup() == this.group || this.group == 0);
                });
        for (Mob mob : mobs) {
            if (mob instanceof AbstractRecruitEntity recruit) {
                CommandEvents.onClearUpkeepButton(uuid, (IRecruitEntity) recruit, group);
            } else {
                CommandEvents.onClearUpkeepButton(uuid, mob, group);
            }
        }
    }

    public MessageClearUpkeep fromBytes(FriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
        this.group = buf.readInt();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeInt(group);
    }
}

