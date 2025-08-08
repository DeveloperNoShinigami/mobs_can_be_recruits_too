package com.talhanation.recruits.inventory;

import com.mojang.datafixers.util.Pair;
import com.talhanation.recruits.init.ModScreens;
import com.talhanation.recruits.inventory.RecruitInventoryMenu;
import com.talhanation.recruits.entities.IRecruitMob;
import com.talhanation.recruits.entities.MobRecruit;
import de.maxhenkel.corelib.inventory.ContainerBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ControlledMobMenu extends ContainerBase {
    private final Container mobInventory;
    private final Mob mob;
    private static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
            InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
            InventoryMenu.EMPTY_ARMOR_SLOT_HELMET
    };
    public static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
            EquipmentSlot.OFFHAND,
            EquipmentSlot.MAINHAND
    };

    private static final int INV_SIZE = RecruitInventoryMenu.INV_SIZE;
    private static final String NBT_KEY = "MobInventory";
    private static final String DATA_KEY = "MobData";
    public static final String[] EXTRA_KEYS = new String[]{
            "FollowState", "HoldX", "HoldY", "HoldZ", "Group", "AggroState",
            "ShouldBlock", "ShouldRanged", "ShouldRest", "ShouldStrategicFire",
            "StrategicFireX", "StrategicFireY", "StrategicFireZ", "UpkeepUUID",
            "UpkeepPosX", "UpkeepPosY", "UpkeepPosZ", "Owner", "Owned", "HireCost"
    };

    private static SimpleContainer loadInventory(Mob mob, CompoundTag syncTag){
        SimpleContainer inv = new SimpleContainer(INV_SIZE);

        CompoundTag tag = syncTag != null ? syncTag : mob.getPersistentData();
      
        if(tag.contains(NBT_KEY)){
            ListTag list = tag.getList(NBT_KEY, 10);
            for(int i=0;i<list.size();i++){
                CompoundTag ct = list.getCompound(i);
                int slot = ct.getByte("Slot") & 255;
                if(slot < inv.getContainerSize()){
                    inv.setItem(slot, ItemStack.of(ct));
                }
            }
        }else{
            // fall back to the entity's current equipment when no sync tag is available
            inv.setItem(0, mob.getItemBySlot(EquipmentSlot.HEAD));
            inv.setItem(1, mob.getItemBySlot(EquipmentSlot.CHEST));
            inv.setItem(2, mob.getItemBySlot(EquipmentSlot.LEGS));
            inv.setItem(3, mob.getItemBySlot(EquipmentSlot.FEET));
            inv.setItem(4, mob.getItemBySlot(EquipmentSlot.OFFHAND));
            inv.setItem(5, mob.getItemBySlot(EquipmentSlot.MAINHAND));
        }

        if(tag.contains(DATA_KEY)){
            CompoundTag data = tag.getCompound(DATA_KEY);
            for(String key : EXTRA_KEYS){
                if(data.contains(key)) mob.getPersistentData().put(key, data.get(key).copy());
            }
        }
        return inv;
    }

    private void saveInventory(){
        CompoundTag tag = mob.getPersistentData();
        ListTag list = new ListTag();
        for(int i=6;i<mobInventory.getContainerSize();i++){
            ItemStack stack = mobInventory.getItem(i);
            if(!stack.isEmpty()){
                CompoundTag ct = new CompoundTag();
                ct.putByte("Slot", (byte)i);
                stack.save(ct);
                list.add(ct);
            }
        }
        tag.put(NBT_KEY, list);
        CompoundTag data = new CompoundTag();
        for(String key : EXTRA_KEYS){
            if(tag.contains(key)) data.put(key, tag.get(key).copy());
        }
        tag.put(DATA_KEY, data);
        mob.setItemSlot(EquipmentSlot.HEAD, mobInventory.getItem(0));
        mob.setItemSlot(EquipmentSlot.CHEST, mobInventory.getItem(1));
        mob.setItemSlot(EquipmentSlot.LEGS, mobInventory.getItem(2));
        mob.setItemSlot(EquipmentSlot.FEET, mobInventory.getItem(3));
        mob.setItemSlot(EquipmentSlot.OFFHAND, mobInventory.getItem(4));
        mob.setItemSlot(EquipmentSlot.MAINHAND, mobInventory.getItem(5));
        if (!(mob instanceof IRecruitMob)) {
            MobRecruit.get(mob).reloadInventory();
        }
    }

    public ControlledMobMenu(int id, Mob mob, Inventory playerInventory){
        this(mob, playerInventory, id, loadInventory(mob, null));
    }

    public ControlledMobMenu(int id, Mob mob, Inventory playerInventory, CompoundTag tag){
        this(mob, playerInventory, id, loadInventory(mob, tag));
    }

    private ControlledMobMenu(Mob mob, Inventory playerInventory, int id, Container container){
        super(ModScreens.CONTROLLED_MOB_CONTAINER_TYPE.get(), id, playerInventory, container);
        this.mob = mob;
        this.mobInventory = container;

        addPlayerInventorySlots();
        addMobHandSlots();
        addMobEquipmentSlots();
        addMobInventorySlots();
    }

    public Mob getMob(){
        return mob;
    }

    @Override
    public int getInvOffset() {
        return 56;
    }

    private void addMobHandSlots(){
        this.addSlot(new Slot(mobInventory,4,44,90){
            @Override
            public boolean mayPlace(ItemStack stack){
                return !mob.isUsingItem() && stack.getItem() instanceof ShieldItem;
            }
            @Override
            public boolean mayPickup(Player player){
                return !mob.isUsingItem();
            }
            @Override
            public void set(ItemStack stack){
                super.set(stack);
                mob.setItemSlot(EquipmentSlot.OFFHAND, stack);
            }
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon(){
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });

        this.addSlot(new Slot(mobInventory,5,26,90){
            @Override
            public boolean mayPlace(ItemStack stack){
                return mob.canHoldItem(stack);
            }
            @Override
            public void set(ItemStack stack){
                super.set(stack);
                mob.setItemSlot(EquipmentSlot.MAINHAND, stack);
            }
        });
    }

    private void addMobEquipmentSlots(){
        for(int slotIndex=0; slotIndex<4; ++slotIndex){
            final EquipmentSlot slotType = SLOT_IDS[slotIndex];
            this.addSlot(new Slot(mobInventory, slotIndex,8,18 + slotIndex * 18){
                @Override
                public int getMaxStackSize(){
                    return 1;
                }
                @Override
                public boolean mayPlace(ItemStack stack){
                    return stack.canEquip(slotType, mob) || (stack.getItem() instanceof BannerItem && slotType.equals(EquipmentSlot.HEAD));
                }
                @Override
                public void set(ItemStack stack){
                    super.set(stack);
                    mob.setItemSlot(slotType, stack);
                }
                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon(){
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[slotType.getIndex()]);
                }
            });
        }
    }

    private void addMobInventorySlots(){
        int columns = RecruitInventoryMenu.INV_COLUMNS;
        int rows = (mobInventory.getContainerSize() - 6) / columns;
        for(int k=0;k<rows;++k){
            for(int l=0;l<columns;++l){
                this.addSlot(new Slot(mobInventory,6 + l + k * columns, 2 * 18 + 82 + l * 18, 18 + k * 18));
            }
        }
    }

    @Override
    public boolean stillValid(Player playerIn){
        return mob.isAlive() && mob.distanceTo(playerIn) < 8.0F;
    }

    @Override
    public void removed(Player playerIn){
        super.removed(playerIn);
        saveInventory();
    }

    public ItemStack quickMoveStack(Player playerIn, int index){
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.getSlot(index);
        if(slot != null && slot.hasItem()){
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if(index <= 35){
                if(this.getSlot(38).mayPlace(stack) && !this.getSlot(38).hasItem()){
                    if(!this.moveItemStackTo(stack,38,this.slots.size(),false)) return ItemStack.EMPTY;
                } else if(this.getSlot(39).mayPlace(stack) && !this.getSlot(39).hasItem()){
                    if(!this.moveItemStackTo(stack,39,this.slots.size(),false)) return ItemStack.EMPTY;
                } else if(this.getSlot(40).mayPlace(stack) && !this.getSlot(40).hasItem()){
                    if(!this.moveItemStackTo(stack,40,this.slots.size(),false)) return ItemStack.EMPTY;
                } else if(this.getSlot(41).mayPlace(stack) && !this.getSlot(41).hasItem()){
                    if(!this.moveItemStackTo(stack,41,this.slots.size(),false)) return ItemStack.EMPTY;
                } else if(this.getSlot(36).mayPlace(stack) && !this.getSlot(36).hasItem()){
                    if(!this.moveItemStackTo(stack,36,this.slots.size(),false)) return ItemStack.EMPTY;
                } else if(this.getSlot(37).mayPlace(stack) && !this.getSlot(37).hasItem()){
                    if(!this.moveItemStackTo(stack,37,this.slots.size(),false)) return ItemStack.EMPTY;
                } else if(!this.moveItemStackTo(stack,42,this.slots.size(),false)) return ItemStack.EMPTY;
            } else if(!this.moveItemStackTo(stack,0,35,false)) return ItemStack.EMPTY;

            if(stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        }
        return itemstack;
    }
}
