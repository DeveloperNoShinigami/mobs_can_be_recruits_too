package com.talhanation.recruits.client.gui;

import com.talhanation.recruits.client.gui.group.RecruitsGroup;
import com.talhanation.recruits.client.gui.widgets.ScrollDropDownMenu;
import com.talhanation.recruits.entities.IRecruitEntity;
import de.maxhenkel.corelib.inventory.ScreenBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Comparator;
import java.util.List;

/**
 * Base GUI for recruit-style screens that exposes common group
 * selection handling for both real recruits and generic controlled mobs.
 */
public abstract class AbstractRecruitScreen<T extends AbstractContainerMenu> extends ScreenBase<T> {

    /** Shared list of available groups. Populated by network messages. */
    public static List<RecruitsGroup> groups;

    protected final IRecruitEntity recruit;
    protected ScrollDropDownMenu<RecruitsGroup> groupSelectionDropDownMenu;
    protected RecruitsGroup currentGroup;
    protected boolean buttonsSet;

    protected AbstractRecruitScreen(ResourceLocation texture, T menu, Inventory playerInventory, Component title, IRecruitEntity recruit) {
        super(texture, menu, playerInventory, title);
        this.recruit = recruit;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (groups != null && !groups.isEmpty() && !buttonsSet) {
            groups.sort(Comparator.comparingInt(RecruitsGroup::getId));
            this.currentGroup = getCurrentGroup(recruit.getGroup());
            groupSelectionDropDownMenu = new ScrollDropDownMenu<>(currentGroup, leftPos + 77, topPos + 114, 93, 12,
                    groups,
                    RecruitsGroup::getName,
                    selected -> {
                        currentGroup = selected;
                        sendGroupUpdate(currentGroup.getId());
                    });
            groupSelectionDropDownMenu.setBgFillSelected(FastColor.ARGB32.color(255, 139, 139, 139));
            groupSelectionDropDownMenu.visible = Minecraft.getInstance().player.getUUID().equals(recruit.getOwnerUUID());
            addRenderableWidget(groupSelectionDropDownMenu);
            buttonsSet = true;
        }
    }

    private RecruitsGroup getCurrentGroup(int id) {
        if (groups == null) return null;
        for (RecruitsGroup g : groups) {
            if (g.getId() == id) {
                return g;
            }
        }
        return null;
    }

    @Override
    public void mouseMoved(double x, double y) {
        if (groupSelectionDropDownMenu != null) {
            groupSelectionDropDownMenu.onMouseMove(x, y);
        }
        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (groupSelectionDropDownMenu != null && groupSelectionDropDownMenu.isMouseOver(mouseX, mouseY)) {
            groupSelectionDropDownMenu.onMouseClick(mouseX, mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double d) {
        if (groupSelectionDropDownMenu != null) {
            groupSelectionDropDownMenu.mouseScrolled(x, y, d);
        }
        return super.mouseScrolled(x, y, d);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        if (groupSelectionDropDownMenu != null) {
            groupSelectionDropDownMenu.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    /**
     * Send a network message to update the recruit's group.
     */
    protected abstract void sendGroupUpdate(int groupId);
}

