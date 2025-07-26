package com.talhanation.recruits;

import com.talhanation.recruits.util.FormationUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommandEventsTest {

    private static void invokeMovement(Mob mob, int state, ServerPlayer player) throws Exception {
        Method m = CommandEvents.class.getDeclaredMethod("applyControlledMobMovement", Mob.class, int.class, ServerPlayer.class);
        m.setAccessible(true);
        m.invoke(null, mob, state, player);
    }

    @Test
    public void testState3Return() throws Exception {
        Mob mob = mock(Mob.class);
        PathNavigation nav = mock(PathNavigation.class);
        when(mob.getNavigation()).thenReturn(nav);
        CompoundTag tag = new CompoundTag();
        when(mob.getPersistentData()).thenReturn(tag);
        tag.putDouble("HoldX", 5.0);
        tag.putDouble("HoldY", 64.0);
        tag.putDouble("HoldZ", 6.0);

        ServerPlayer player = mock(ServerPlayer.class);

        invokeMovement(mob, 3, player);

        assertEquals(3, tag.getInt("FollowState"));
        verify(nav).moveTo(5.0, 64.0, 6.0, 1.0D);
    }

    @Test
    public void testState5Protect() throws Exception {
        Mob mob = mock(Mob.class);
        when(mob.getNavigation()).thenReturn(mock(PathNavigation.class));
        CompoundTag tag = new CompoundTag();
        when(mob.getPersistentData()).thenReturn(tag);
        ServerPlayer player = mock(ServerPlayer.class);

        invokeMovement(mob, 5, player);

        assertEquals(5, tag.getInt("FollowState"));
    }

    @Test
    public void testState7Forward() throws Exception {
        Mob mob = mock(Mob.class);
        PathNavigation nav = mock(PathNavigation.class);
        when(mob.getNavigation()).thenReturn(nav);
        CompoundTag tag = new CompoundTag();
        when(mob.getPersistentData()).thenReturn(tag);
        when(mob.position()).thenReturn(Vec3.ZERO);

        ServerPlayer player = mock(ServerPlayer.class);
        when(player.getForward()).thenReturn(new Vec3(1, 0, 0));
        when(player.position()).thenReturn(Vec3.ZERO);
        when(player.getCommandSenderWorld()).thenReturn(mock(ServerLevel.class));

        try (MockedStatic<FormationUtils> utils = mockStatic(FormationUtils.class)) {
            utils.when(() -> FormationUtils.getPositionOrSurface(any(), any())).thenReturn(new BlockPos(10, 64, 0));

            invokeMovement(mob, 7, player);
        }

        assertEquals(3, tag.getInt("FollowState"));
        assertEquals(10.0, tag.getDouble("HoldX"));
        assertEquals(64.0, tag.getDouble("HoldY"));
        assertEquals(0.0, tag.getDouble("HoldZ"));
        verify(nav).moveTo(10.0, 64.0, 0.0, 1.0D);
    }

    @Test
    public void testState8Backward() throws Exception {
        Mob mob = mock(Mob.class);
        PathNavigation nav = mock(PathNavigation.class);
        when(mob.getNavigation()).thenReturn(nav);
        CompoundTag tag = new CompoundTag();
        when(mob.getPersistentData()).thenReturn(tag);
        when(mob.position()).thenReturn(Vec3.ZERO);

        ServerPlayer player = mock(ServerPlayer.class);
        when(player.getForward()).thenReturn(new Vec3(1, 0, 0));
        when(player.position()).thenReturn(Vec3.ZERO);
        when(player.getCommandSenderWorld()).thenReturn(mock(ServerLevel.class));

        try (MockedStatic<FormationUtils> utils = mockStatic(FormationUtils.class)) {
            utils.when(() -> FormationUtils.getPositionOrSurface(any(), any())).thenReturn(new BlockPos(-10, 64, 0));

            invokeMovement(mob, 8, player);
        }

        assertEquals(3, tag.getInt("FollowState"));
        assertEquals(-10.0, tag.getDouble("HoldX"));
        assertEquals(64.0, tag.getDouble("HoldY"));
        assertEquals(0.0, tag.getDouble("HoldZ"));
        verify(nav).moveTo(-10.0, 64.0, 0.0, 1.0D);
    }
}
