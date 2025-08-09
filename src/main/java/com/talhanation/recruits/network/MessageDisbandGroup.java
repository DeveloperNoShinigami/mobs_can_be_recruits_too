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

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MessageDisbandGroup implements Message<MessageDisbandGroup> {

    private UUID owner;
    private UUID recruit;
    private boolean keepTeam;

    public MessageDisbandGroup() {
    }

    public MessageDisbandGroup(UUID owner, UUID recruit, boolean keepTeam) {
        this.owner = owner;
        this.recruit = recruit;
        this.keepTeam = keepTeam;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer player = Objects.requireNonNull(context.getSender());
        List<Mob> list = player.getCommandSenderWorld().getEntitiesOfClass(
                Mob.class,
                player.getBoundingBox().inflate(100D),
                mob -> mob instanceof AbstractRecruitEntity || mob.getPersistentData().getBoolean("RecruitControlled")
        );
        int group = -1;

        for (Mob recruit1 : list) {
            if (recruit1.getUUID().equals(recruit)) {
                if (recruit1 instanceof AbstractRecruitEntity r) {
                    group = r.getGroup();
                } else {
                    group = MobRecruit.get(recruit1).getGroup();
                }
                break;
            }
        }

        if (group == -1) {
            return;
        }

        for (Mob mob : list) {
            if (mob instanceof AbstractRecruitEntity recruit) {
                if (owner.equals(recruit.getOwnerUUID()) && recruit.getGroup() == group) {
                    recruit.disband(context.getSender(), keepTeam, true);
                }
            } else {
                MobRecruit recruit = MobRecruit.get(mob);
                UUID mobOwner = recruit.getOwnerUUID();
                if (mobOwner != null && mobOwner.equals(owner) && recruit.getGroup() == group) {
                    disband(mob, player, keepTeam);
                }
            }
        }
    }

    private static void disband(Mob mob, ServerPlayer player, boolean keepTeam) {
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

    public MessageDisbandGroup fromBytes(FriendlyByteBuf buf) {
        this.owner = buf.readUUID();
        this.recruit = buf.readUUID();
        this.keepTeam = buf.readBoolean();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(owner);
        buf.writeUUID(recruit);
        buf.writeBoolean(keepTeam);
    }
}