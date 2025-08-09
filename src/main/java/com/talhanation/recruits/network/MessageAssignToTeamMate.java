package com.talhanation.recruits.network;

import com.talhanation.recruits.TeamEvents;
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

public class MessageAssignToTeamMate implements Message<MessageAssignToTeamMate> {

    private UUID recruit;
    private UUID newOwner;

    public MessageAssignToTeamMate() {
    }

    public MessageAssignToTeamMate(UUID recruit, UUID newOwner) {
        this.recruit = recruit;
        this.newOwner = newOwner;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer serverPlayer = context.getSender();
        List<Mob> list = Objects.requireNonNull(context.getSender()).getCommandSenderWorld().getEntitiesOfClass(
                Mob.class,
                context.getSender().getBoundingBox().inflate(64.0D),
                mob -> mob instanceof AbstractRecruitEntity || mob.getPersistentData().getBoolean("RecruitControlled")
        );

        for (Mob mob : list) {
            if (mob.getUUID().equals(this.recruit)) {
                TeamEvents.assignToTeamMate(serverPlayer, newOwner, mob);
                break;
            }
        }
    }

    public MessageAssignToTeamMate fromBytes(FriendlyByteBuf buf) {
        this.recruit = buf.readUUID();
        this.newOwner = buf.readUUID();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.recruit);
        buf.writeUUID(this.newOwner);
    }
}