package com.talhanation.recruits;

import com.talhanation.recruits.config.RecruitsServerConfig;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraftforge.common.ForgeConfigSpec;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RecruitBehaviorTest {

    @Test
    public void testXpLevelPersistence() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Xp", 42);
        tag.putInt("Level", 3);

        AbstractRecruitEntity recruit = mock(AbstractRecruitEntity.class);
        doCallRealMethod().when(recruit).readAdditionalSaveData(any());

        recruit.readAdditionalSaveData(tag);

        verify(recruit).setXp(42);
        verify(recruit).setXpLevel(3);
    }

    @Test
    public void testHungerReduction() {
        AbstractRecruitEntity recruit = mock(AbstractRecruitEntity.class);
        doCallRealMethod().when(recruit).updateHunger();
        when(recruit.getHunger()).thenReturn(10f);
        when(recruit.getFollowState()).thenReturn(2);

        recruit.updateHunger();

        verify(recruit).setHunger(10f - (2f/60f));
    }

    @Test
    public void testNoPaymentActionTriggered() {
        ForgeConfigSpec.BooleanValue payment = mock(ForgeConfigSpec.BooleanValue.class);
        when(payment.get()).thenReturn(true);
        RecruitsServerConfig.RecruitsPayment = payment;

        ForgeConfigSpec.EnumValue<AbstractRecruitEntity.NoPaymentAction> action = mock(ForgeConfigSpec.EnumValue.class);
        when(action.get()).thenReturn(AbstractRecruitEntity.NoPaymentAction.DISBAND);
        RecruitsServerConfig.RecruitsNoPaymentAction = action;

        SimpleContainer inv = mock(SimpleContainer.class);

        AbstractRecruitEntity recruit = mock(AbstractRecruitEntity.class);
        doCallRealMethod().when(recruit).checkPayment(any());
        doReturn(true).when(recruit).isOwned();
        doReturn(inv).when(recruit).getInventory();
        doReturn(false).when(recruit).isPaymentInContainer(any());
        doNothing().when(recruit).resetPaymentTimer();
        doNothing().when(recruit).disband(any(), anyBoolean(), anyBoolean());
        when(recruit.getOwner()).thenReturn(null);

        recruit.checkPayment(inv);

        verify(recruit).disband(null, false, true);
    }
}
