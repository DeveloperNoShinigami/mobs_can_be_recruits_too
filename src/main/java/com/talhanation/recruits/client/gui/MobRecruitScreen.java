package com.talhanation.recruits.client.gui;

import com.talhanation.recruits.inventory.ControlledMobMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Simple wrapper screen that reuses the existing recruit GUI for
 * generic controlled mobs.
 */
public class MobRecruitScreen extends ControlledMobScreen {
    public MobRecruitScreen(ControlledMobMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }
}
