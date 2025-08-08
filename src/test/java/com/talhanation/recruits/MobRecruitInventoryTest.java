package com.talhanation.recruits;

import com.talhanation.recruits.entities.MobRecruit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MobRecruitInventoryTest {

    @Test
    public void handHeldItemsPersistAfterReload() {
        Mob mob = mock(Mob.class);
        CompoundTag tag = new CompoundTag();
        when(mob.getPersistentData()).thenReturn(tag);

        Map<EquipmentSlot, ItemStack> equipment = new EnumMap<>(EquipmentSlot.class);
        equipment.put(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
        equipment.put(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
        equipment.put(EquipmentSlot.HEAD, ItemStack.EMPTY);
        equipment.put(EquipmentSlot.CHEST, ItemStack.EMPTY);
        equipment.put(EquipmentSlot.LEGS, ItemStack.EMPTY);
        equipment.put(EquipmentSlot.FEET, ItemStack.EMPTY);

        when(mob.getItemBySlot(any())).thenAnswer(inv -> {
            EquipmentSlot slot = inv.getArgument(0);
            return equipment.getOrDefault(slot, ItemStack.EMPTY);
        });
        doAnswer(inv -> {
            EquipmentSlot slot = inv.getArgument(0);
            ItemStack stack = inv.getArgument(1);
            equipment.put(slot, stack);
            return null;
        }).when(mob).setItemSlot(any(), any());

        MobRecruit recruit = new MobRecruit(mob);

        assertEquals(Items.DIAMOND_SWORD, recruit.getInventory().getItem(5).getItem());
        assertEquals(Items.SHIELD, recruit.getInventory().getItem(4).getItem());

        recruit.reloadInventory();

        assertEquals(Items.DIAMOND_SWORD, recruit.getInventory().getItem(5).getItem());
        assertEquals(Items.SHIELD, recruit.getInventory().getItem(4).getItem());
    }
}
