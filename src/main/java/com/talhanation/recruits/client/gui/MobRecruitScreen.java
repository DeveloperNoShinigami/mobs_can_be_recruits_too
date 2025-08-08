package com.talhanation.recruits.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.Main;
import com.talhanation.recruits.RecruitEvents;
import com.talhanation.recruits.inventory.ControlledMobMenu;
import com.talhanation.recruits.network.MessageControlledMobGroup;
import com.talhanation.recruits.network.MessageRenameMob;
import com.talhanation.recruits.entities.IRecruitEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.lwjgl.glfw.GLFW;

/**
 * GUI for generic controlled mobs that share recruit functionality.
 */
@OnlyIn(Dist.CLIENT)
public class MobRecruitScreen extends AbstractRecruitScreen<ControlledMobMenu> {

    private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Main.MOD_ID, "textures/gui/recruit_gui.png");
    private static final int fontColor = 4210752;

    public static int xp;
    public static int level;
    public static int kills;
    public static float morale;
    public static float hunger;

    private static final MutableComponent TEXT_PROMOTE = Component.translatable("gui.recruits.inv.text.promote");
    private static final MutableComponent TOOLTIP_PROMOTE = Component.translatable("gui.recruits.inv.tooltip.promote");
    private static final MutableComponent TOOLTIP_DISABLED_PROMOTE = Component.translatable("gui.recruits.inv.tooltip.promote_disabled");

    private final Mob mob;
    private EditBox nameField;
    private Button promoteButton;

    public MobRecruitScreen(ControlledMobMenu container, Inventory playerInventory, Component title) {
        super(RESOURCE_LOCATION, container, playerInventory, Component.literal(""), IRecruitEntity.of(container.getMob()));
        this.mob = container.getMob();
        imageWidth = 176;
        imageHeight = 223;
    }

    @Override
    protected void init() {
        super.init();
        Component name = mob.getCustomName() != null ? mob.getCustomName() : mob.getName();
        nameField = new EditBox(font, leftPos + 5, topPos - 23, 90, 20, name);
        nameField.setValue(name.getString());
        nameField.setMaxLength(32);
        nameField.setBordered(true);
        addRenderableWidget(nameField);
        setInitialFocus(nameField);
        addRenderableWidget(new ExtendedButton(leftPos + imageWidth + 5, topPos, 70, 20,
                Component.literal("Commands"),
                button -> CommandEvents.openCommandScreen(minecraft.player)));
        promoteButton = addRenderableWidget(new ExtendedButton(leftPos + imageWidth + 5, topPos + 24, 70, 20,
                TEXT_PROMOTE,
                btn -> RecruitEvents.openControlledMobPromoteScreen(minecraft.player, mob)));
        boolean canPromote = level >= 3;
        promoteButton.setTooltip(Tooltip.create(canPromote ? TOOLTIP_PROMOTE : TOOLTIP_DISABLED_PROMOTE));
        promoteButton.active = canPromote;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (nameField != null) nameField.tick();
        if (promoteButton != null) {
            boolean canPromote = level >= 3;
            promoteButton.active = canPromote;
            promoteButton.setTooltip(Tooltip.create(canPromote ? TOOLTIP_PROMOTE : TOOLTIP_DISABLED_PROMOTE));
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.7F, 0.7F, 1F);

        int k = 112;
        int l = 32;
        int gap = 42;

        guiGraphics.drawString(font, "Lvl.:", k, l, fontColor, false);
        guiGraphics.drawString(font, String.valueOf(level), k + gap, l, fontColor, false);
        guiGraphics.drawString(font, "Exp.:", k, l + 10, fontColor, false);
        guiGraphics.drawString(font, String.valueOf(xp), k + gap, l + 10, fontColor, false);
        guiGraphics.drawString(font, "Morale:", k, l + 20, fontColor, false);
        guiGraphics.drawString(font, String.valueOf((int) morale), k + gap, l + 20, fontColor, false);
        guiGraphics.drawString(font, "Hunger:", k, l + 30, fontColor, false);
        guiGraphics.drawString(font, String.valueOf((int) hunger), k + gap, l + 30, fontColor, false);
        guiGraphics.drawString(font, "Kills:", k, l + 40, fontColor, false);
        guiGraphics.drawString(font, String.valueOf(kills), k + gap, l + 40, fontColor, false);
        guiGraphics.pose().popPose();
    }

    @Override
    public boolean keyPressed(int key, int a, int b) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        setFocused(nameField);
        return nameField.keyPressed(key, a, b) || nameField.canConsumeInput() || super.keyPressed(key, a, b);
    }

    @Override
    public void onClose() {
        super.onClose();
        Main.SIMPLE_CHANNEL.sendToServer(new MessageRenameMob(mob.getUUID(), nameField.getValue()));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);

        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, i + 50, j + 82, 30,
                (float) (i + 50) - mouseX, (float) (j + 75 - 50) - mouseY, this.mob);
    }

    @Override
    protected void sendGroupUpdate(int groupId) {
        Main.SIMPLE_CHANNEL.sendToServer(new MessageControlledMobGroup(groupId, mob.getUUID()));
    }
}

