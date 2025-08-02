package com.talhanation.recruits.network;

import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import com.talhanation.recruits.entities.IRecruitEntity;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class MessageAggro implements Message<MessageAggro> {

    private UUID player;
    private UUID recruit;
    private int state;
    private int group;
    private boolean fromGui;


    public MessageAggro() {
    }

    public MessageAggro(UUID player, int state, int group) {
        this.player = player;
        this.state = state;
        this.group = group;
        this.fromGui = false;
        this.recruit = null;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer player = Objects.requireNonNull(context.getSender());

        double boundBoxInflateModifier = 16.0D;
        if(!fromGui) {
            boundBoxInflateModifier = 100.0D;
        }


        player.getCommandSenderWorld().getEntitiesOfClass(Mob.class,
                player.getBoundingBox().inflate(boundBoxInflateModifier),
                m -> {
                    if (m instanceof AbstractRecruitEntity recruit) {
                        if (fromGui && !recruit.getUUID().equals(this.recruit)) return false;
                        return recruit.isEffectedByCommand(this.player, group);
                    }
                    if (fromGui) return false;
                    IRecruitEntity recruit = IRecruitEntity.of(m);
                    return m.getPersistentData().getBoolean("RecruitControlled") &&
                            recruit.isOwned() &&
                            recruit.isOwnedBy(this.player) &&
                            (recruit.getGroup() == this.group || this.group == 0);
                }).forEach(m -> {
            if (m instanceof AbstractRecruitEntity recruit) {
                CommandEvents.onAggroCommand(this.player, recruit, this.state, group, fromGui);
            } else {
                CommandEvents.onAggroCommand(this.player, m, this.state, group);
            }
        });
    }

    public MessageAggro fromBytes(FriendlyByteBuf buf) {
        this.player = buf.readUUID();
        this.state = buf.readInt();
        this.group = buf.readInt();
        if (this.recruit != null) this.recruit = buf.readUUID();
        this.fromGui = buf.readBoolean();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.player);
        buf.writeInt(this.state);
        buf.writeInt(this.group);
        buf.writeBoolean(this.fromGui);
        if (this.recruit != null) buf.writeUUID(this.recruit);
    }
}