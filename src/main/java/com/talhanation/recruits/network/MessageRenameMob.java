package com.talhanation.recruits.network;

import com.talhanation.recruits.entities.AbstractRecruitEntity;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;

public class MessageRenameMob implements Message<MessageRenameMob> {

    private UUID mobId;
    private String name;

    public MessageRenameMob() {}

    public MessageRenameMob(UUID mobId, String name) {
        this.mobId = mobId;
        this.name = name;
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
        ).forEach(mob -> mob.setCustomName(Component.literal(name)));
    }

    @Override
    public MessageRenameMob fromBytes(FriendlyByteBuf buf) {
        this.mobId = buf.readUUID();
        this.name = buf.readUtf();
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(mobId);
        buf.writeUtf(name);
    }
}
