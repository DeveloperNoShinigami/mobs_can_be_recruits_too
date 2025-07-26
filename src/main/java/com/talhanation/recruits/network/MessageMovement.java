package com.talhanation.recruits.network;

import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import net.minecraft.world.entity.Mob;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MessageMovement implements Message<MessageMovement> {

    private UUID player_uuid;
    private int state;
    private int group;
    private int formation;

    public MessageMovement(){
    }

    public MessageMovement(UUID player_uuid, int state, int group, int formation) {
        this.player_uuid = player_uuid;
        this.state  = state;
        this.group  = group;
        this.formation = formation;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context){
        List<Mob> mobs = Objects.requireNonNull(context.getSender()).getCommandSenderWorld().getEntitiesOfClass(Mob.class,
                context.getSender().getBoundingBox().inflate(100),
                m -> {
                    if (m instanceof AbstractRecruitEntity recruit) {
                        return recruit.isEffectedByCommand(this.player_uuid, this.group);
                    }
                    return m.getPersistentData().getBoolean("RecruitControlled") &&
                            m.getPersistentData().getBoolean("Owned") &&
                            m.getPersistentData().getUUID("Owner").equals(this.player_uuid) &&
                            (m.getPersistentData().getInt("Group") == this.group || this.group == 0);
                });

        CommandEvents.onMovementCommand(context.getSender(), mobs, this.state, this.formation);
    }

    public MessageMovement fromBytes(FriendlyByteBuf buf) {
        this.player_uuid = buf.readUUID();
        this.state = buf.readInt();
        this.group = buf.readInt();
        this.formation = buf.readInt();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.player_uuid);
        buf.writeInt(this.state);
        buf.writeInt(this.group);
        buf.writeInt(this.formation);
    }

}