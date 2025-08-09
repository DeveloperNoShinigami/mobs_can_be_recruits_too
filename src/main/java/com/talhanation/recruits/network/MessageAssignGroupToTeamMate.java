package com.talhanation.recruits.network;

import com.talhanation.recruits.TeamEvents;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import com.talhanation.recruits.entities.MobRecruit;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MessageAssignGroupToTeamMate implements Message<MessageAssignGroupToTeamMate> {

    private UUID owner;
    private UUID newOwner;
    private UUID recruit;

    public MessageAssignGroupToTeamMate(){
    }

    public MessageAssignGroupToTeamMate(UUID owner, UUID newOwner, UUID recruit) {
        this.owner = owner;
        this.newOwner = newOwner;
        this.recruit = recruit;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer serverPlayer = Objects.requireNonNull(context.getSender());
        List<Mob> list = serverPlayer.getCommandSenderWorld().getEntitiesOfClass(
                Mob.class,
                context.getSender().getBoundingBox().inflate(100D),
                mob -> {
                    if (mob instanceof AbstractRecruitEntity r) {
                        UUID u = r.getOwnerUUID();
                        return u != null && u.equals(owner);
                    }
                    if (mob.getPersistentData().getBoolean("RecruitControlled")) {
                        UUID u = MobRecruit.get(mob).getOwnerUUID();
                        return u != null && u.equals(owner);
                    }
                    return false;
                }
        );
        int group = -1;

        for (Mob mob : list){
            if(mob.getUUID().equals(recruit)){
                if (mob instanceof AbstractRecruitEntity r) {
                    group = r.getGroup();
                } else {
                    group = MobRecruit.get(mob).getGroup();
                }
                break;
            }
        }

        for (Mob mob : list) {
            if (mob instanceof AbstractRecruitEntity recruitEntity) {
                UUID recruitOwner = recruitEntity.getOwnerUUID();
                if (recruitOwner != null && recruitOwner.equals(owner) && recruitEntity.getGroup() == group)
                    TeamEvents.assignToTeamMate(serverPlayer, newOwner, recruitEntity);
            } else {
                MobRecruit recruit = MobRecruit.get(mob);
                UUID recruitOwner = recruit.getOwnerUUID();
                if (recruitOwner != null && recruitOwner.equals(owner) && recruit.getGroup() == group)
                    TeamEvents.assignToTeamMate(serverPlayer, newOwner, mob);
            }
        }
    }
    public MessageAssignGroupToTeamMate fromBytes(FriendlyByteBuf buf) {
        this.owner = buf.readUUID();
        this.newOwner = buf.readUUID();
        this.recruit = buf.readUUID();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.owner);
        buf.writeUUID(this.newOwner);
        buf.writeUUID(this.recruit);
    }

}