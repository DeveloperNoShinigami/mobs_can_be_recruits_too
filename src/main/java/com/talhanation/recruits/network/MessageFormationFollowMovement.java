package com.talhanation.recruits.network;

import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import com.talhanation.recruits.entities.IRecruitEntity;
import com.talhanation.recruits.util.FormationMember;
import com.talhanation.recruits.util.MobFormationAdapter;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.world.entity.Mob;

public class MessageFormationFollowMovement implements Message<MessageFormationFollowMovement> {

    private UUID player_uuid;

    private int group;
    private int formation;

    public MessageFormationFollowMovement(){
    }

    public MessageFormationFollowMovement(UUID player_uuid, int group, int formation) {
        this.player_uuid = player_uuid;
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
                    IRecruitEntity recruit = IRecruitEntity.of(m);
                    return m.getPersistentData().getBoolean("RecruitControlled") &&
                            recruit.isOwned() &&
                            recruit.isOwnedBy(this.player_uuid) &&
                            (recruit.getGroup() == this.group || this.group == 0);
                });

        List<FormationMember> members = new ArrayList<>();
        for (Mob mob : mobs) {
            if (mob instanceof FormationMember fm) members.add(fm); else members.add(new MobFormationAdapter(mob));
        }

        CommandEvents.applyFormation(formation, members, context.getSender(), context.getSender().position());
    }

    public MessageFormationFollowMovement fromBytes(FriendlyByteBuf buf) {
        this.player_uuid = buf.readUUID();
        this.group = buf.readInt();
        this.formation = buf.readInt();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.player_uuid);
        buf.writeInt(this.group);
        buf.writeInt(this.formation);
    }

}