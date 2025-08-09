package com.talhanation.recruits.network;

import com.talhanation.recruits.entities.AbstractRecruitEntity;
import com.talhanation.recruits.entities.MobRecruit;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;

public class MessageCombatFlagGui implements Message<MessageCombatFlagGui> {

    private UUID uuid;
    private int flag;
    private boolean value;

    public MessageCombatFlagGui() {
    }

    public MessageCombatFlagGui(UUID uuid, int flag, boolean value) {
        this.uuid = uuid;
        this.flag = flag;
        this.value = value;
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
                m -> m.getUUID().equals(this.uuid) &&
                        (m instanceof AbstractRecruitEntity || m.getPersistentData().getBoolean("RecruitControlled"))
        ).forEach(m -> {
            MobRecruit recruit = MobRecruit.get(m);
            switch (this.flag) {
                case 0 -> recruit.setShouldRanged(this.value);
                case 1 -> recruit.setShouldBlock(this.value);
                case 2 -> recruit.setShouldRest(this.value);
            }
        });
    }

    @Override
    public MessageCombatFlagGui fromBytes(FriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
        this.flag = buf.readInt();
        this.value = buf.readBoolean();
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeInt(flag);
        buf.writeBoolean(value);
    }
}
