package com.talhanation.recruits.network;

import com.talhanation.recruits.RecruitEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.network.NetworkEvent;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class MessagePromoteControlledMobTest {

    @Test
    public void testPromoteRegardlessOfDistance() {
        UUID id = UUID.randomUUID();
        MessagePromoteControlledMob msg = new MessagePromoteControlledMob(id, 1, "Bob");

        NetworkEvent.Context ctx = mock(NetworkEvent.Context.class);
        ServerPlayer player = mock(ServerPlayer.class);
        ServerLevel level = mock(ServerLevel.class);
        Mob mob = mock(Mob.class);
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("RecruitControlled", true);

        when(ctx.getSender()).thenReturn(player);
        when(player.level()).thenReturn(level);
        when(level.getEntity(id)).thenReturn(mob);
        when(mob.getPersistentData()).thenReturn(tag);

        try (MockedStatic<RecruitEvents> events = mockStatic(RecruitEvents.class)) {
            msg.executeServerSide(ctx);
            events.verify(() -> RecruitEvents.promoteControlledMob(mob, 1, "Bob", player));
        }
    }

    @Test
    public void testNoPromotionWhenEntityMissing() {
        UUID id = UUID.randomUUID();
        MessagePromoteControlledMob msg = new MessagePromoteControlledMob(id, 1, "Bob");

        NetworkEvent.Context ctx = mock(NetworkEvent.Context.class);
        ServerPlayer player = mock(ServerPlayer.class);
        ServerLevel level = mock(ServerLevel.class);

        when(ctx.getSender()).thenReturn(player);
        when(player.level()).thenReturn(level);
        when(level.getEntity(id)).thenReturn(null);

        try (MockedStatic<RecruitEvents> events = mockStatic(RecruitEvents.class)) {
            msg.executeServerSide(ctx);
            events.verifyNoInteractions();
        }
    }
}
