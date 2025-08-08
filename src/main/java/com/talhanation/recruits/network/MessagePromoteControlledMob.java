package com.talhanation.recruits.network;

import com.talhanation.recruits.RecruitEvents;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;

public class MessagePromoteControlledMob implements Message<MessagePromoteControlledMob> {
    private UUID mobId;
    private int profession;
    private String name;

    public MessagePromoteControlledMob() {}

    public MessagePromoteControlledMob(UUID mobId, int profession, String name) {
        this.mobId = mobId;
        this.profession = profession;
        this.name = name;
    }

    @Override
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer player = Objects.requireNonNull(context.getSender());
        Entity entity = ((ServerLevel) player.level()).getEntity(this.mobId);
        if (entity instanceof Mob mob
                && !(mob instanceof AbstractRecruitEntity)
                && mob.getPersistentData().getBoolean("RecruitControlled")) {
            RecruitEvents.promoteControlledMob(mob, profession, name, player);
        }
    }

    @Override
    public MessagePromoteControlledMob fromBytes(FriendlyByteBuf buf) {
        this.mobId = buf.readUUID();
        this.profession = buf.readInt();
        this.name = buf.readUtf();
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(mobId);
        buf.writeInt(profession);
        buf.writeUtf(name);
    }
}
