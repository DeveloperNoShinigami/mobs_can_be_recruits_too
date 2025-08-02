package com.talhanation.recruits.network;

import com.talhanation.recruits.CommandEvents;
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

public class MessageRangedFire implements Message<MessageRangedFire> {

    private UUID player;
    private int group;
    private boolean should;

    public MessageRangedFire(){
    }

    public MessageRangedFire(UUID player, int group, boolean shields) {
        this.player = player;
        this.group = group;
        this.should = shields;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer serverPlayer = context.getSender();
        List<Mob> mobs = Objects.requireNonNull(context.getSender()).getCommandSenderWorld().getEntitiesOfClass(Mob.class,
                context.getSender().getBoundingBox().inflate(100),
                m -> {
                    if (m instanceof AbstractRecruitEntity recruit) {
                        return recruit.isEffectedByCommand(this.player, group);
                    }
                    IRecruitEntity recruit = IRecruitEntity.of(m);
                    return m.getPersistentData().getBoolean("RecruitControlled") &&
                            recruit.isOwned() &&
                            recruit.isOwnedBy(this.player) &&
                            (recruit.getGroup() == this.group || this.group == 0);
                });
        for (Mob m : mobs) {
            if (m instanceof AbstractRecruitEntity recruit) {
                CommandEvents.onRangedFireCommand(serverPlayer, this.player, recruit, group, should);
            } else {
                CommandEvents.onRangedFireCommand(serverPlayer, this.player, m, group, should);
            }
        }
    }
    public MessageRangedFire fromBytes(FriendlyByteBuf buf) {
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