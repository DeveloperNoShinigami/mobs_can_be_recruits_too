package com.talhanation.recruits.network;

import com.talhanation.recruits.RecruitEvents;
import com.talhanation.recruits.TeamEvents;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import com.talhanation.recruits.entities.MobRecruit;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;

public class MessageDisband implements Message<MessageDisband> {

    private UUID recruit;
    private boolean keepTeam;

    public MessageDisband() {
    }

    public MessageDisband(UUID recruit, boolean keepTeam) {
        this.recruit = recruit;
        this.keepTeam = keepTeam;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer player = Objects.requireNonNull(context.getSender());
        player.getCommandSenderWorld().getEntitiesOfClass(
                Mob.class,
                player.getBoundingBox().inflate(16D),
                (mob) -> mob.getUUID().equals(this.recruit) &&
                        (mob instanceof AbstractRecruitEntity || mob.getPersistentData().getBoolean("RecruitControlled"))
        ).forEach(mob -> disband(mob, player, keepTeam));
    }

    private static void disband(Mob mob, ServerPlayer player, boolean keepTeam) {
        if (mob instanceof AbstractRecruitEntity recruit) {
            recruit.disband(player, keepTeam, true);
        } else {
            MobRecruit recruit = MobRecruit.get(mob);
            UUID owner = recruit.getOwnerUUID();
            if (owner != null) {
                RecruitEvents.recruitsPlayerUnitManager.removeRecruits(owner, 1);
            }
            mob.setTarget(null);
            recruit.setIsOwned(false);
            recruit.setOwnerUUID(null);
            if (!keepTeam && mob.getTeam() != null) {
                TeamEvents.removeRecruitFromTeam(mob, mob.getTeam(), (ServerLevel) mob.level());
            }
        }
    }

    public MessageDisband fromBytes(FriendlyByteBuf buf) {
        this.recruit = buf.readUUID();
        this.keepTeam = buf.readBoolean();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(recruit);
        buf.writeBoolean(keepTeam);
    }
}