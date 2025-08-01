package com.talhanation.recruits.network;

import com.talhanation.recruits.entities.AbstractRecruitEntity;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;

public class MessageControlledMobGroup implements Message<MessageControlledMobGroup> {
    private int group;
    private UUID mobId;

    public MessageControlledMobGroup() {
    }

    public MessageControlledMobGroup(int group, UUID mobId) {
        this.group = group;
        this.mobId = mobId;
    }

    @Override
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer player = Objects.requireNonNull(context.getSender());
        player.getCommandSenderWorld().getEntitiesOfClass(
                Mob.class,
                player.getBoundingBox().inflate(16.0D),
                m -> !(m instanceof AbstractRecruitEntity) &&
                        m.getPersistentData().getBoolean("RecruitControlled") &&
                        m.getUUID().equals(this.mobId)
        ).forEach(mob -> mob.getPersistentData().putInt("Group", group));
    }

    @Override
    public MessageControlledMobGroup fromBytes(FriendlyByteBuf buf) {
        this.group = buf.readInt();
        this.mobId = buf.readUUID();
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(group);
        buf.writeUUID(mobId);
    }
}
