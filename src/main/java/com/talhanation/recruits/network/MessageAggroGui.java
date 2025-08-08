package com.talhanation.recruits.network;

import com.talhanation.recruits.entities.AbstractRecruitEntity;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;

public class MessageAggroGui implements Message<MessageAggroGui> {

    private int state;
    private UUID uuid;

    public MessageAggroGui() {
    }

    public MessageAggroGui(int state, UUID uuid) {
        this.state = state;
        this.uuid = uuid;
    }

    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    public void executeServerSide(NetworkEvent.Context context) {
        ServerPlayer player = Objects.requireNonNull(context.getSender());
        player.getCommandSenderWorld().getEntitiesOfClass(
                Mob.class,
                player.getBoundingBox().inflate(16.0D),
                m -> m.getUUID().equals(this.uuid) && (m instanceof AbstractRecruitEntity || m.getPersistentData().getBoolean("RecruitControlled"))
        ).forEach(m -> {
            if (m instanceof AbstractRecruitEntity recruit) {
                recruit.setState(this.state);
            } else {
                m.getPersistentData().putInt("AggroState", this.state);
                if (m instanceof PathfinderMob pm && this.state == 3) {
                    pm.setTarget(null);
                }
            }
        });
    }

    public MessageAggroGui fromBytes(FriendlyByteBuf buf) {
        this.state = buf.readInt();
        this.uuid = buf.readUUID();
        return this;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(state);
        buf.writeUUID(uuid);
    }
}
