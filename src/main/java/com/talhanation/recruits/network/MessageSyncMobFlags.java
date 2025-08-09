package com.talhanation.recruits.network;

import de.maxhenkel.corelib.net.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

/**
 * Client-bound packet that mirrors selected persistent data flags for a mob.
 * <p>
 * The GUI reads these values directly from {@link Entity#getPersistentData()},
 * so they must be kept in sync when the server updates them.
 */
public class MessageSyncMobFlags implements Message<MessageSyncMobFlags> {

    private UUID uuid;
    private int aggroState;
    private int followState;
    private boolean listen;
    private String className;

    public MessageSyncMobFlags() {
    }

    public MessageSyncMobFlags(UUID uuid, int aggroState, int followState, boolean listen, String className) {
        this.uuid = uuid;
        this.aggroState = aggroState;
        this.followState = followState;
        this.listen = listen;
        this.className = className;
    }

    @Override
    public Dist getExecutingSide() {
        return Dist.CLIENT;
    }

    @Override
    public void executeClientSide(NetworkEvent.Context context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) {
            return;
        }
        Entity entity = null;
        for (Entity e : level.entitiesForRendering()) {
            if (uuid.equals(e.getUUID())) {
                entity = e;
                break;
            }
        }
        if (entity == null) {
            return;
        }
        // Mirror updated flags into the entity's persistent data
        entity.getPersistentData().putInt("AggroState", aggroState);
        entity.getPersistentData().putInt("FollowState", followState);
        entity.getPersistentData().putBoolean("Listen", listen);
        entity.getPersistentData().putString("Class", className);
    }

    @Override
    public MessageSyncMobFlags fromBytes(FriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
        this.aggroState = buf.readInt();
        this.followState = buf.readInt();
        this.listen = buf.readBoolean();
        this.className = buf.readUtf(32767);
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeInt(aggroState);
        buf.writeInt(followState);
        buf.writeBoolean(listen);
        buf.writeUtf(className);
    }
}
