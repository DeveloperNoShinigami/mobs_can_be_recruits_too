package com.talhanation.recruits.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.talhanation.recruits.Main;
import com.talhanation.recruits.CommandEvents;
import com.talhanation.recruits.inventory.ControlledMobMenu;
import com.talhanation.recruits.RecruitEvents;
import com.talhanation.recruits.network.MessageControlledMobGroup;
import com.talhanation.recruits.network.MessageControlledMobStats;
import com.talhanation.recruits.network.MessageRenameMob;
import com.talhanation.recruits.client.gui.group.RecruitsGroup;
import com.talhanation.recruits.client.gui.widgets.ScrollDropDownMenu;
import de.maxhenkel.corelib.inventory.ScreenBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.lwjgl.glfw.GLFW;
import java.util.UUID;
import java.util.Comparator;

@OnlyIn(Dist.CLIENT)
public class ControlledMobScreen extends ScreenBase<ControlledMobMenu> {
    private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Main.MOD_ID, "textures/gui/recruit_gui.png");
    private static final int fontColor = 4210752;

    public static int xp;
    public static int level;
    public static float morale;
    public static float hunger;

    private static final MutableComponent TEXT_PROMOTE = Component.translatable("gui.recruits.inv.text.promote");
    private static final MutableComponent TOOLTIP_PROMOTE = Component.translatable("gui.recruits.inv.tooltip.promote");

    private final Mob mob;
    private EditBox nameField;
    private ScrollDropDownMenu<RecruitsGroup> groupSelectionDropDownMenu;
    private RecruitsGroup currentGroup;
    private boolean buttonsSet;

    public ControlledMobScreen(ControlledMobMenu container, Inventory playerInventory, Component title) {
        super(RESOURCE_LOCATION, container, playerInventory, Component.literal(""));
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
        Button promoteButton = addRenderableWidget(new ExtendedButton(leftPos + imageWidth + 5, topPos + 24, 70, 20,
                TEXT_PROMOTE,
                btn -> RecruitEvents.openControlledMobPromoteScreen(minecraft.player, mob)));
        promoteButton.setTooltip(Tooltip.create(TOOLTIP_PROMOTE));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (nameField != null) nameField.tick();
        if(RecruitInventoryScreen.groups != null && !RecruitInventoryScreen.groups.isEmpty() && !buttonsSet){
            RecruitInventoryScreen.groups.sort(Comparator.comparingInt(RecruitsGroup::getId));
            this.currentGroup = getCurrentGroup(mob.getPersistentData().getInt("Group"));
            groupSelectionDropDownMenu = new ScrollDropDownMenu<>(currentGroup, leftPos + 77, topPos + 114, 93, 12,
                    RecruitInventoryScreen.groups,
                    RecruitsGroup::getName,
                    selected -> {
                        this.currentGroup = selected;
                        Main.SIMPLE_CHANNEL.sendToServer(new MessageControlledMobGroup(currentGroup.getId(), mob.getUUID()));
                    }
            );
            groupSelectionDropDownMenu.setBgFillSelected(FastColor.ARGB32.color(255, 139, 139, 139));
            UUID owner = mob.getPersistentData().getUUID("Owner");
            groupSelectionDropDownMenu.visible = owner != null && owner.equals(Minecraft.getInstance().player.getUUID());
            addRenderableWidget(groupSelectionDropDownMenu);
            buttonsSet = true;
        }
    }

    private RecruitsGroup getCurrentGroup(int id){
        for(RecruitsGroup g : RecruitInventoryScreen.groups){
            if(g.getId() == id) return g;
        }
        return null;
    }

    @Override
    public void mouseMoved(double x, double y) {
        if(groupSelectionDropDownMenu != null) groupSelectionDropDownMenu.onMouseMove(x,y);
        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(groupSelectionDropDownMenu != null && groupSelectionDropDownMenu.isMouseOver(mouseX, mouseY)){
            groupSelectionDropDownMenu.onMouseClick(mouseX, mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double d) {
        if(groupSelectionDropDownMenu != null) groupSelectionDropDownMenu.mouseScrolled(x,y,d);
        return super.mouseScrolled(x,y,d);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        if(groupSelectionDropDownMenu != null) {
            groupSelectionDropDownMenu.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
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
}
