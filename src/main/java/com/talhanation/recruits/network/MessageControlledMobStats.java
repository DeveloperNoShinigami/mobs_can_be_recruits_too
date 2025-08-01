package com.talhanation.recruits.network;

import com.talhanation.recruits.client.gui.ControlledMobScreen;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

public class MessageControlledMobStats implements Message<MessageControlledMobStats> {
    private CompoundTag nbt;

    public MessageControlledMobStats() {
    }

    public MessageControlledMobStats(CompoundTag nbt) {
        this.nbt = nbt;
    }

    @Override
    public Dist getExecutingSide() {
        return Dist.CLIENT;
    }

    @Override
    public void executeClientSide(NetworkEvent.Context context) {
        if (nbt == null) return;
        ControlledMobScreen.level = nbt.getInt("Level");
        ControlledMobScreen.xp = nbt.getInt("Xp");
        ControlledMobScreen.morale = nbt.getFloat("Moral");
        ControlledMobScreen.hunger = nbt.getFloat("Hunger");
    }

    @Override
    public MessageControlledMobStats fromBytes(FriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }
}
