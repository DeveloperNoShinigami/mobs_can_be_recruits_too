package com.talhanation.recruits.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.Main;
import com.talhanation.recruits.RecruitEvents;
import com.talhanation.recruits.inventory.ControlledMobMenu;
import com.talhanation.recruits.network.MessageAggroGui;
import com.talhanation.recruits.network.MessageClearTargetGui;
import com.talhanation.recruits.network.MessageControlledMobGroup;
import com.talhanation.recruits.network.MessageFollowGui;
import com.talhanation.recruits.network.MessageMountEntityGui;
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

    private static final MutableComponent TEXT_PASSIVE = Component.translatable("gui.recruits.inv.text.passive");
    private static final MutableComponent TEXT_NEUTRAL = Component.translatable("gui.recruits.inv.text.neutral");
    private static final MutableComponent TEXT_AGGRESSIVE = Component.translatable("gui.recruits.inv.text.aggressive");
    private static final MutableComponent TEXT_RAID = Component.translatable("gui.recruits.inv.text.raid");
    private static final MutableComponent TOOLTIP_PASSIVE = Component.translatable("gui.recruits.inv.tooltip.passive");
    private static final MutableComponent TOOLTIP_NEUTRAL = Component.translatable("gui.recruits.inv.tooltip.neutral");
    private static final MutableComponent TOOLTIP_AGGRESSIVE = Component.translatable("gui.recruits.inv.tooltip.aggressive");
    private static final MutableComponent TOOLTIP_RAID = Component.translatable("gui.recruits.inv.tooltip.raid");

    private static final MutableComponent TEXT_PROMOTE = Component.translatable("gui.recruits.inv.text.promote");
    private static final MutableComponent TOOLTIP_PROMOTE = Component.translatable("gui.recruits.inv.tooltip.promote");
    private static final MutableComponent TOOLTIP_DISABLED_PROMOTE = Component.translatable("gui.recruits.inv.tooltip.promote_disabled");

    private static final MutableComponent TEXT_FOLLOW = Component.translatable("gui.recruits.inv.text.follow");
    private static final MutableComponent TEXT_WANDER = Component.translatable("gui.recruits.inv.text.wander");
    private static final MutableComponent TEXT_HOLD_POS = Component.translatable("gui.recruits.inv.text.holdPos");
    private static final MutableComponent TEXT_CLEAR_TARGET = Component.translatable("gui.recruits.inv.text.clearTargets");
    private static final MutableComponent TEXT_MOUNT = Component.translatable("gui.recruits.command.text.mount");

    private static final MutableComponent TEXT_INFO_FOLLOW = Component.translatable("gui.recruits.inv.info.text.follow");
    private static final MutableComponent TEXT_INFO_WANDER = Component.translatable("gui.recruits.inv.info.text.wander");
    private static final MutableComponent TEXT_INFO_HOLD_POS = Component.translatable("gui.recruits.inv.info.text.hold_pos");
    private static final MutableComponent TEXT_INFO_PASSIVE = Component.translatable("gui.recruits.inv.info.text.passive");
    private static final MutableComponent TEXT_INFO_NEUTRAL = Component.translatable("gui.recruits.inv.info.text.neutral");
    private static final MutableComponent TEXT_INFO_AGGRESSIVE = Component.translatable("gui.recruits.inv.info.text.aggressive");
    private static final MutableComponent TEXT_INFO_RAID = Component.translatable("gui.recruits.inv.info.text.raid");
    private static final MutableComponent TEXT_INFO_PROTECT = Component.translatable("gui.recruits.inv.info.text.protect");
    private static final MutableComponent TEXT_INFO_WORKING = Component.translatable("gui.recruits.inv.info.text.working");

    private static final MutableComponent TOOLTIP_WANDER = Component.translatable("gui.recruits.inv.tooltip.wander");
    private static final MutableComponent TOOLTIP_FOLLOW = Component.translatable("gui.recruits.inv.tooltip.follow");
    private static final MutableComponent TOOLTIP_HOLD_POS = Component.translatable("gui.recruits.inv.tooltip.holdPos");
    private static final MutableComponent TOOLTIP_CLEAR_TARGET = Component.translatable("gui.recruits.inv.tooltip.clearTargets");
    private static final MutableComponent TOOLTIP_MOUNT = Component.translatable("gui.recruits.inv.tooltip.mount");

    private final Mob mob;
    private EditBox nameField;
    private Button promoteButton;
    private int follow;
    private int aggro;

    public MobRecruitScreen(ControlledMobMenu container, Inventory playerInventory, Component title) {
        super(RESOURCE_LOCATION, container, playerInventory, Component.literal(""), IRecruitEntity.of(container.getMob()));
        this.mob = container.getMob();
        imageWidth = 176;
        imageHeight = 223;
    }

    @Override
    protected void init() {
        super.init();
        // Remove existing widgets to prevent duplicates if the screen is reopened
        clearWidgets();

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
        promoteButton.setTooltip(Tooltip.create(TOOLTIP_PROMOTE));

        int zeroLeftPos = leftPos + 180;
        int zeroTopPos = topPos + 10;
        int topPosGab = 5;

        ExtendedButton buttonPassive = new ExtendedButton(zeroLeftPos - 270, zeroTopPos + (20 + topPosGab) * 0, 80, 20, TEXT_PASSIVE,
                btn -> {
                    aggro = mob.getPersistentData().getInt("AggroState");
                    if (aggro != 3) {
                        aggro = 3;
                        Main.SIMPLE_CHANNEL.sendToServer(new MessageAggroGui(aggro, mob.getUUID()));
                    }
                });
        buttonPassive.setTooltip(Tooltip.create(TOOLTIP_PASSIVE));
        addRenderableWidget(buttonPassive);

        ExtendedButton buttonNeutral = new ExtendedButton(zeroLeftPos - 270, zeroTopPos + (20 + topPosGab) * 1, 80, 20, TEXT_NEUTRAL,
                btn -> {
                    aggro = mob.getPersistentData().getInt("AggroState");
                    if (aggro != 0) {
                        aggro = 0;
                        Main.SIMPLE_CHANNEL.sendToServer(new MessageAggroGui(aggro, mob.getUUID()));
                    }
                });
        buttonNeutral.setTooltip(Tooltip.create(TOOLTIP_NEUTRAL));
        addRenderableWidget(buttonNeutral);

        ExtendedButton buttonAggressive = new ExtendedButton(zeroLeftPos - 270, zeroTopPos + (20 + topPosGab) * 2, 80, 20, TEXT_AGGRESSIVE,
                btn -> {
                    aggro = mob.getPersistentData().getInt("AggroState");
                    if (aggro != 1) {
                        aggro = 1;
                        Main.SIMPLE_CHANNEL.sendToServer(new MessageAggroGui(aggro, mob.getUUID()));
                    }
                });
        buttonAggressive.setTooltip(Tooltip.create(TOOLTIP_AGGRESSIVE));
        addRenderableWidget(buttonAggressive);

        ExtendedButton buttonRaid = new ExtendedButton(zeroLeftPos - 270, zeroTopPos + (20 + topPosGab) * 3, 80, 20, TEXT_RAID,
                btn -> {
                    aggro = mob.getPersistentData().getInt("AggroState");
                    if (aggro != 2) {
                        aggro = 2;
                        Main.SIMPLE_CHANNEL.sendToServer(new MessageAggroGui(aggro, mob.getUUID()));
                    }
                });
        buttonRaid.setTooltip(Tooltip.create(TOOLTIP_RAID));
        addRenderableWidget(buttonRaid);

        ExtendedButton buttonClearTarget = new ExtendedButton(zeroLeftPos - 270, zeroTopPos + (20 + topPosGab) * 4, 80, 20, TEXT_CLEAR_TARGET,
                btn -> Main.SIMPLE_CHANNEL.sendToServer(new MessageClearTargetGui(MobRecruitScreen.this.minecraft.player.getUUID(), mob.getUUID())));
        buttonClearTarget.setTooltip(Tooltip.create(TOOLTIP_CLEAR_TARGET));
        addRenderableWidget(buttonClearTarget);

        ExtendedButton buttonMount = new ExtendedButton(zeroLeftPos - 270, zeroTopPos + (20 + topPosGab) * 5, 80, 20, TEXT_MOUNT,
                btn -> Main.SIMPLE_CHANNEL.sendToServer(new MessageMountEntityGui(mob.getUUID(), false)));
        buttonMount.setTooltip(Tooltip.create(TOOLTIP_MOUNT));
        addRenderableWidget(buttonMount);

        ExtendedButton buttonWander = new ExtendedButton(zeroLeftPos, zeroTopPos + (20 + topPosGab) * 0, 80, 20, TEXT_WANDER,
                btn -> {
                    follow = mob.getPersistentData().getInt("FollowState");
                    if (follow != 0) {
                        follow = 0;
                        Main.SIMPLE_CHANNEL.sendToServer(new MessageFollowGui(follow, mob.getUUID()));
                    }
                });
        buttonWander.setTooltip(Tooltip.create(TOOLTIP_WANDER));
        addRenderableWidget(buttonWander);

        ExtendedButton buttonFollow = new ExtendedButton(zeroLeftPos, zeroTopPos + (20 + topPosGab) * 1, 80, 20, TEXT_FOLLOW,
                btn -> {
                    follow = mob.getPersistentData().getInt("FollowState");
                    if (follow != 1) {
                        follow = 1;
                        Main.SIMPLE_CHANNEL.sendToServer(new MessageFollowGui(follow, mob.getUUID()));
                    }
                });
        buttonFollow.setTooltip(Tooltip.create(TOOLTIP_FOLLOW));
        addRenderableWidget(buttonFollow);

        ExtendedButton buttonHoldPos = new ExtendedButton(zeroLeftPos, zeroTopPos + (20 + topPosGab) * 2, 80, 20, TEXT_HOLD_POS,
                btn -> {
                    follow = mob.getPersistentData().getInt("FollowState");
                    if (follow != 2) {
                        follow = 2;
                        Main.SIMPLE_CHANNEL.sendToServer(new MessageFollowGui(follow, mob.getUUID()));
                    }
                });
        buttonHoldPos.setTooltip(Tooltip.create(TOOLTIP_HOLD_POS));
        addRenderableWidget(buttonHoldPos);
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

        int k2 = 79;
        int l2 = 19;
        this.follow = mob.getPersistentData().getInt("FollowState");
        this.aggro = mob.getPersistentData().getInt("AggroState");
        String followText = switch (this.follow) {
            case 0 -> TEXT_INFO_WANDER.getString();
            case 1 -> TEXT_INFO_FOLLOW.getString();
            case 2, 3, 4 -> TEXT_INFO_HOLD_POS.getString();
            case 5 -> TEXT_INFO_PROTECT.getString();
            case 6 -> TEXT_INFO_WORKING.getString();
            default -> "";
        };
        guiGraphics.drawString(font, followText, k2 + 15, l2 + 58, fontColor, false);

        String aggroText = switch (this.aggro) {
            case 0 -> TEXT_INFO_NEUTRAL.getString();
            case 1 -> TEXT_INFO_AGGRESSIVE.getString();
            case 2 -> TEXT_INFO_RAID.getString();
            case 3 -> TEXT_INFO_PASSIVE.getString();
            default -> "";
        };
        int fnt = this.aggro == 3 ? 16733525 : fontColor;
        guiGraphics.drawString(font, aggroText, k2 + 15, l2 + 56 + 15, fnt, false);
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

