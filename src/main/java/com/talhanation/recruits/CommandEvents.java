package com.talhanation.recruits;

import com.talhanation.recruits.client.gui.group.RecruitsGroup;
import com.talhanation.recruits.config.RecruitsServerConfig;
import com.talhanation.recruits.entities.*;
import com.talhanation.recruits.inventory.CommandMenu;
import com.talhanation.recruits.inventory.ControlledMobMenu;
import com.talhanation.recruits.network.*;
import com.talhanation.recruits.util.FormationMember;
import com.talhanation.recruits.util.FormationUtils;
import com.talhanation.recruits.util.MobFormationAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandEvents {
    public static final MutableComponent TEXT_EVERYONE = Component.translatable("chat.recruits.text.everyone");
    public static final MutableComponent TEXT_GROUP = Component.translatable("chat.recruits.text.group");

    //0 = wander
    //1 = follow
    //2 = hold your position
    //3 = back to position
    //4 = hold my position
    //5 = Protect
    //6 = move
    //7 = forward
    //8 = backward
    public static void onMovementCommand(ServerPlayer player, List<Mob> recruits, int movementState, int formation) {
        List<FormationMember> recruitList = new ArrayList<>();
        for (Mob mob : recruits) {
            if (mob instanceof FormationMember fm) {
                recruitList.add(fm);
            } else {
                recruitList.add(new MobFormationAdapter(mob));
            }
        }

        if(formation != 0 && (movementState == 2|| movementState == 4 || movementState == 6 || movementState == 7 || movementState == 8)) {
            Vec3 targetPos = null;

            switch (movementState){
               case 2 -> {//hold your position
                   targetPos = FormationUtils.getGeometricMedian(recruitList, (ServerLevel) player.getCommandSenderWorld());
               }

               case 4 -> {//hold my position
                    targetPos = player.position();
               }

               case 6 -> {//move
                   HitResult hitResult = player.pick(200, 1F, true);
                   targetPos = hitResult.getLocation();
               }

               case 7 -> {//forward
                   Vec3 center = FormationUtils.getGeometricMedian(recruitList, (ServerLevel) player.getCommandSenderWorld());
                   Vec3 forward = player.getForward();
                   Vec3 pos = center.add(forward.scale(getForwardScale(recruitList)));
                   BlockPos blockPos = FormationUtils.getPositionOrSurface(
                           player.getCommandSenderWorld(),
                           new BlockPos((int) pos.x, (int) pos.y, (int) pos.z)
                   );

                   targetPos = new Vec3(pos.x, blockPos.getY(), pos.z);
               }

               case 8 -> {//backward
                   Vec3 center = FormationUtils.getGeometricMedian(recruitList, (ServerLevel) player.getCommandSenderWorld());
                   Vec3 forward = player.getForward();
                   Vec3 pos = center.add(forward.scale(-getForwardScale(recruitList)));
                   BlockPos blockPos = FormationUtils.getPositionOrSurface(
                           player.getCommandSenderWorld(),
                           new BlockPos((int) pos.x, (int) pos.y, (int) pos.z)
                   );

                   targetPos = new Vec3(pos.x, blockPos.getY(), pos.z);
               }
            }
            applyFormation(formation, recruitList, player, targetPos);
        }
        else{
            for(Mob mob : recruits){
                IRecruitEntity recruit = IRecruitEntity.of(mob);
                if(mob instanceof AbstractRecruitEntity recruitEntity){
                    int state = recruitEntity.getFollowState();

                    switch (movementState) {
                        case 0 -> { if (state != 0) recruitEntity.setFollowState(0); }
                        case 1 -> { if (state != 1) recruitEntity.setFollowState(1); }
                        case 2 -> { if (state != 2) recruitEntity.setFollowState(2); }
                        case 3 -> { if (state != 3) recruitEntity.setFollowState(3); }
                        case 4 -> { if (state != 4) recruitEntity.setFollowState(4); }
                        case 5 -> { if (state != 5) recruitEntity.setFollowState(5); }
                        case 6 -> {
                            HitResult hitResult = player.pick(100, 1F, true);
                            if (hitResult.getType() == HitResult.Type.BLOCK) {
                                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                                BlockPos blockpos = blockHitResult.getBlockPos();
                                recruitEntity.setMovePos(blockpos);
                                recruitEntity.setFollowState(0);
                                recruitEntity.setShouldMovePos(true);
                            }
                        }
                        case 7 -> {
                            Vec3 forward = player.getForward();
                            Vec3 pos = recruitEntity.position().add(forward.scale(getForwardScale(recruitEntity)));
                            BlockPos blockPos = FormationUtils.getPositionOrSurface(
                                    player.getCommandSenderWorld(),
                                    new BlockPos((int) pos.x, (int) pos.y, (int) pos.z)
                            );
                            Vec3 tPos = new Vec3(pos.x, blockPos.getY(), pos.z);
                            recruitEntity.setHoldPos(tPos);
                            recruitEntity.ownerRot = player.getYRot();
                            recruitEntity.setFollowState(3);
                        }
                        case 8 -> {
                            Vec3 forward = player.getForward();
                            Vec3 pos = recruitEntity.position().add(forward.scale(-getForwardScale(recruitEntity)));
                            BlockPos blockPos = FormationUtils.getPositionOrSurface(
                                    player.getCommandSenderWorld(),
                                    new BlockPos((int) pos.x, (int) pos.y, (int) pos.z)
                            );
                            Vec3 tPos = new Vec3(pos.x, blockPos.getY(), pos.z);
                            recruitEntity.setHoldPos(tPos);
                            recruitEntity.ownerRot = player.getYRot();
                            recruitEntity.setFollowState(3);
                        }
                    }
                    recruitEntity.isInFormation = false;
                    recruitEntity.setUpkeepTimer(recruitEntity.getUpkeepCooldown());
                    if (recruitEntity.getShouldMount()) recruitEntity.setShouldMount(false);
                    checkPatrolLeaderState(recruit);
                    recruitEntity.forcedUpkeep = false;
                } else {
                    applyControlledMobMovement(recruit, movementState, player);
                }
            }

        }
    }

    private static double getForwardScale(List<? extends FormationMember> recruits) {
        for (FormationMember member : recruits){
            if(member.getMob() instanceof CaptainEntity recruit) return getForwardScale(recruit);
        }
        return 10;
    }
    private static double getForwardScale(IRecruitEntity recruit) {
        return (recruit instanceof CaptainEntity captain && captain.smallShipsController.ship != null && captain.smallShipsController.ship.isCaptainDriver()) ? 25 : 10;
    }
    public static void applyFormation(int formation, List<? extends FormationMember> recruits, ServerPlayer player, Vec3 targetPos) {
        switch (formation){
            case 1 ->{//LINE UP
                FormationUtils.lineUpFormation(player, recruits, targetPos);
            }
            case 2 ->{//SQUARE
                FormationUtils.squareFormation(player, recruits, targetPos);
            }
            case 3 ->{//TRIANGLE
                FormationUtils.triangleFormation(player, recruits, targetPos);
            }
            case 4 ->{//HOLLOW CIRCLE
                FormationUtils.hollowCircleFormation(player, recruits, targetPos);
            }
            case 5 ->{//HOLLOW SQUARE
                FormationUtils.hollowSquareFormation(player, recruits, targetPos);
            }
            case 6 ->{//V Formation
                FormationUtils.vFormation(player, recruits, targetPos);
            }
            case 7 ->{//CIRCLE
                FormationUtils.circleFormation(player, recruits, targetPos);
            }
            case 8 ->{//MOVEMENT
                FormationUtils.movementFormation(player, recruits, targetPos);
            }
        }
    }

    public static void onMovementCommandGUI(IRecruitEntity recruit, int movementState) {
        if(recruit instanceof AbstractRecruitEntity ar) {
            int state = ar.getFollowState();

            switch (movementState) {
                case 0 -> { if (state != 0) ar.setFollowState(0); }
                case 1 -> { if (state != 1) ar.setFollowState(1); }
                case 2 -> { if (state != 2) ar.setFollowState(2); }
                case 3 -> { if (state != 3) ar.setFollowState(3); }
                case 4 -> { if (state != 4) ar.setFollowState(4); }
                case 5 -> { if (state != 5) ar.setFollowState(5); }
            }

            ar.setUpkeepTimer(ar.getUpkeepCooldown());
            if (ar.getShouldMount()) ar.setShouldMount(false);
            checkPatrolLeaderState(ar);
            ar.forcedUpkeep = false;
        } else if(recruit instanceof MobRecruit mr) {
            Mob mob = mr.getMob();
            CompoundTag nbt = mob.getPersistentData();
            switch (movementState) {
                case 0 -> nbt.putInt("FollowState", 0);
                case 1 -> nbt.putInt("FollowState", 1);
                case 2 -> {
                    nbt.putInt("FollowState", 2);
                    nbt.putDouble("HoldX", mob.getX());
                    nbt.putDouble("HoldY", mob.getY());
                    nbt.putDouble("HoldZ", mob.getZ());
                }
                case 3 -> nbt.putInt("FollowState", 3);
                case 4 -> nbt.putInt("FollowState", 4);
                case 5 -> nbt.putInt("FollowState", 5);
            }
        }
    }

    public static void checkPatrolLeaderState(IRecruitEntity recruit) {
        if(recruit instanceof AbstractLeaderEntity leader) {
            AbstractLeaderEntity.State patrolState = AbstractLeaderEntity.State.fromIndex(leader.getPatrollingState());
            if(patrolState == AbstractLeaderEntity.State.PATROLLING || patrolState == AbstractLeaderEntity.State.WAITING) {
                leader.setPatrolState(AbstractLeaderEntity.State.PAUSED);
            }
            else if(patrolState == AbstractLeaderEntity.State.RETREATING || patrolState == AbstractLeaderEntity.State.UPKEEP){
                leader.resetPatrolling();
                leader.setPatrolState(AbstractLeaderEntity.State.IDLE);
            }
        }
    }

    public static void onAggroCommand(UUID player_uuid, IRecruitEntity recruit, int x_state, int group, boolean fromGui) {
        if (recruit instanceof AbstractRecruitEntity ar && ar.isEffectedByCommand(player_uuid, group)){
            int state = ar.getState();
            switch (x_state) {

                case 0:
                    if (state != 0)
                        ar.setState(0);
                    break;

                case 1:
                    if (state != 1)
                        ar.setState(1);
                    break;

                case 2:
                    if (state != 2)
                        ar.setState(2);
                    break;

                case 3:
                    if (state != 3)
                        ar.setState(3);
                    break;
            }
        }
    }

    public static void onAggroCommand(UUID player_uuid, Mob mob, int x_state, int group) {
        if (isControlledMob(mob, player_uuid, group)) {
            mob.getPersistentData().putInt("AggroState", x_state);
        }
    }

    public static void onStrategicFireCommand(Player player, UUID player_uuid, IRecruitEntity recruit, int group, boolean should) {
        if (recruit instanceof AbstractRecruitEntity ar && ar.isEffectedByCommand(player_uuid, group)){

            if (ar instanceof IStrategicFire bowman){
                HitResult hitResult = player.pick(100, 1F, false);
                bowman.setShouldStrategicFire(should);
                if (hitResult != null) {
                    if (hitResult.getType() == HitResult.Type.BLOCK) {
                        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                        BlockPos blockpos = blockHitResult.getBlockPos();
                        bowman.setStrategicFirePos(blockpos);
                    }
                }
            }
        }
    }

    public static void onStrategicFireCommand(Player player, UUID player_uuid, Mob mob, int group, boolean should) {
        if (isControlledMob(mob, player_uuid, group)) {
            CompoundTag nbt = mob.getPersistentData();
            nbt.putBoolean("ShouldStrategicFire", should);
            if (should) {
                HitResult hitResult = player.pick(100, 1F, false);
                if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
                    nbt.putInt("StrategicFireX", pos.getX());
                    nbt.putInt("StrategicFireY", pos.getY());
                    nbt.putInt("StrategicFireZ", pos.getZ());
                }
            }
        }
    }

    public static void openCommandScreen(Player player) {
        if (player instanceof ServerPlayer) {
            updateCommandScreen((ServerPlayer)player);
            NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {

                @Override
                public @NotNull Component getDisplayName() {
                    return Component.literal("command_screen");
                }

                @Override
                public @NotNull AbstractContainerMenu createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
                    return new CommandMenu(i, playerEntity);
                }
            }, packetBuffer -> {packetBuffer.writeUUID(player.getUUID());});
        } else {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageCommandScreen(player));
        }
    }

    public static void openMobInventoryScreen(Player player, Mob mob){
        if(player instanceof ServerPlayer serverPlayer){
            updateRecruitInventoryScreen(serverPlayer);
            CompoundTag nbt = new CompoundTag();
            CompoundTag data = mob.getPersistentData();
            if (data.contains("Xp")) nbt.putInt("Xp", data.getInt("Xp"));
            if (data.contains("Level")) nbt.putInt("Level", data.getInt("Level"));
            if (data.contains("Moral")) nbt.putFloat("Moral", data.getFloat("Moral"));
            if (data.contains("Hunger")) nbt.putFloat("Hunger", data.getFloat("Hunger"));
            Main.SIMPLE_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new MessageControlledMobStats(nbt));

            CompoundTag inv = new CompoundTag();

            ListTag list = new ListTag();

            // include current equipment in the sync tag so the client always sees
            // the correct items even if the entity state hasn't fully propagated
            for (int i = 0; i < ControlledMobMenu.SLOT_IDS.length; i++) {
                ItemStack stack = mob.getItemBySlot(ControlledMobMenu.SLOT_IDS[i]);
                if (!stack.isEmpty()) {
                    CompoundTag ct = new CompoundTag();
                    ct.putByte("Slot", (byte) i);
                    stack.save(ct);
                    list.add(ct);
                }
            }

            if (data.contains("MobInventory")) {
                list.addAll(data.getList("MobInventory", 10));
            }

            inv.put("MobInventory", list);

            if (data.contains("MobData")) {
                inv.put("MobData", data.getCompound("MobData"));
            }

            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return mob.getName();
                }

                @Override
                public @NotNull AbstractContainerMenu createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player p) {
                    return new ControlledMobMenu(i, mob, playerInventory, inv);
                }
            }, buf -> {
                buf.writeUUID(mob.getUUID());
                buf.writeNbt(inv);
            });
        }
    }
    @SubscribeEvent
    public void onServerPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.player instanceof ServerPlayer serverPlayer && serverPlayer.tickCount % 20 == 0){
            int formation = getSavedFormation(serverPlayer);

            if(formation > 0){
                int[] savedPos = getSavedFormationPos(serverPlayer);
                if(savedPos.length == 0) {
                    savedPos = new int[]{(int) serverPlayer.getX(), (int) serverPlayer.getZ()};
                    saveFormationPos(serverPlayer, savedPos);
                }
                int savedX = savedPos[0];
                int savedZ = savedPos[1];
                Vec3 oldPos = new Vec3(savedX, serverPlayer.getY(), savedZ);
                Vec3 targetPosition = serverPlayer.position();

                if(targetPosition.distanceToSqr(oldPos) > 50){

                    List<Mob> list = Objects.requireNonNull(serverPlayer).getCommandSenderWorld().getEntitiesOfClass(
                                    Mob.class,
                                    serverPlayer.getBoundingBox().inflate(200)
                            );
                    list.removeIf(m -> {
                        if(m instanceof AbstractRecruitEntity recruitEntity) {
                            return Arrays.stream(getActiveGroups(serverPlayer)).noneMatch(x -> recruitEntity.isEffectedByCommand(serverPlayer.getUUID(), x));
                        }
                        IRecruitEntity recruit = IRecruitEntity.of(m);
                        return !(m.getPersistentData().getBoolean("RecruitControlled") && recruit.isOwned() && recruit.isOwnedBy(serverPlayer.getUUID()));
                    });
                    List<FormationMember> members = new ArrayList<>();
                    for(Mob m : list) {
                        if(m instanceof FormationMember fm) members.add(fm); else members.add(new MobFormationAdapter(m));
                    }

                    applyFormation(formation, members, serverPlayer, targetPosition);
                    int[] position = new int[]{(int) targetPosition.x, (int) targetPosition.z};
                    saveFormationPos(serverPlayer, position);
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        CompoundTag playerData = event.getEntity().getPersistentData();
        CompoundTag data = playerData.getCompound(Player.PERSISTED_NBT_TAG);
            if (!data.contains("MaxRecruits")) data.putInt("MaxRecruits", RecruitsServerConfig.MaxRecruitsForPlayer.get());
            if (!data.contains("CommandingGroup")) data.putInt("CommandingGroup", 0);
            if (!data.contains("TotalRecruits")) data.putInt("TotalRecruits", 0);
            if (!data.contains("ActiveGroups")) data.putIntArray("ActiveGroups", new int[0]);
            if (!data.contains("Formation")) data.putInt("Formation", 0);
            if (!data.contains("FormationPos")) data.putIntArray("FormationPos", new int[]{(int) event.getEntity().getX(), (int) event.getEntity().getZ()});

        playerData.put(Player.PERSISTED_NBT_TAG, data);
    }

    public static int getSavedFormation(Player player) {
        CompoundTag playerNBT = player.getPersistentData();
        CompoundTag nbt = playerNBT.getCompound(Player.PERSISTED_NBT_TAG);

        return nbt.getInt("Formation");
    }

    public static void saveFormation(Player player, int formation) {
        CompoundTag playerNBT = player.getPersistentData();
        CompoundTag nbt = playerNBT.getCompound(Player.PERSISTED_NBT_TAG);

        nbt.putInt( "Formation", formation);
        playerNBT.put(Player.PERSISTED_NBT_TAG, nbt);
    }
    public static int[] getSavedFormationPos(Player player) {
        CompoundTag playerNBT = player.getPersistentData();
        CompoundTag nbt = playerNBT.getCompound(Player.PERSISTED_NBT_TAG);

        return nbt.getIntArray("FormationPos");
    }

    public static void saveFormationPos(Player player, int[] pos) {
        CompoundTag playerNBT = player.getPersistentData();
        CompoundTag nbt = playerNBT.getCompound(Player.PERSISTED_NBT_TAG);

        nbt.putIntArray( "FormationPos", pos);
        playerNBT.put(Player.PERSISTED_NBT_TAG, nbt);
    }
    public static int[] getActiveGroups(Player player) {
        CompoundTag playerNBT = player.getPersistentData();
        CompoundTag nbt = playerNBT.getCompound(Player.PERSISTED_NBT_TAG);

        return nbt.getIntArray("ActiveGroups");
    }

    public static void saveActiveGroups(Player player, int[] count) {
        CompoundTag playerNBT = player.getPersistentData();
        CompoundTag nbt = playerNBT.getCompound(Player.PERSISTED_NBT_TAG);

        nbt.putIntArray( "ActiveGroups", count);
        playerNBT.put(Player.PERSISTED_NBT_TAG, nbt);
    }

    public static void handleRecruiting(Player player, IRecruitEntity recruit){
        if(!(recruit instanceof AbstractRecruitEntity ar)) return;
        String name = ar.getName().getString() + ": ";
        int sollPrice = ar.getCost();
        Inventory playerInv = player.getInventory();
        int playerEmeralds = 0;

        String str = RecruitsServerConfig.RecruitCurrency.get();
        Optional<Holder<Item>> holder = ForgeRegistries.ITEMS.getHolder(ResourceLocation.tryParse(str));

        ItemStack currencyItemStack = holder.map(itemHolder -> itemHolder.value().getDefaultInstance()).orElseGet(Items.EMERALD::getDefaultInstance);

        Item currency = currencyItemStack.getItem();//

        //checkPlayerMoney
        for (int i = 0; i < playerInv.getContainerSize(); i++){
            ItemStack itemStackInSlot = playerInv.getItem(i);
            Item itemInSlot = itemStackInSlot.getItem();
            if (itemInSlot.equals(currency)){
                playerEmeralds = playerEmeralds + itemStackInSlot.getCount();
            }
        }

        boolean playerCanPay = playerEmeralds >= sollPrice;

        if (playerCanPay || player.isCreative()){
            if(ar.hire(player)) {
                //give player tradeGood
                //remove playerEmeralds ->add left
                //
                playerEmeralds = playerEmeralds - sollPrice;

                //merchantEmeralds = merchantEmeralds + sollPrice;

                //remove playerEmeralds
                for (int i = 0; i < playerInv.getContainerSize(); i++) {
                    ItemStack itemStackInSlot = playerInv.getItem(i);
                    Item itemInSlot = itemStackInSlot.getItem();
                    if (itemInSlot.equals(currency)) {
                        playerInv.removeItemNoUpdate(i);
                    }
                }

                //add leftEmeralds to playerInventory
                ItemStack emeraldsLeft = currencyItemStack.copy();
                emeraldsLeft.setCount(playerEmeralds);
                playerInv.add(emeraldsLeft);


                if(player.getTeam() != null){
                    if(player.getCommandSenderWorld().isClientSide){
                        Main.SIMPLE_CHANNEL.sendToServer(new MessageAddRecruitToTeam(player.getTeam().getName(), 1));
                    }
                    else {
                        ServerPlayer serverPlayer = (ServerPlayer) player;
                        TeamEvents.addNPCToData(serverPlayer.serverLevel(), player.getTeam().getName(), 1);
                    }
                }
            }
        }
        else
            player.sendSystemMessage(TEXT_HIRE_COSTS(name, sollPrice, currency));
    }

    public static void onMountButton(UUID player_uuid, IRecruitEntity recruit, UUID mount_uuid, int group) {
        if (recruit instanceof AbstractRecruitEntity ar && ar.isEffectedByCommand(player_uuid, group)){
            if(mount_uuid != null) ar.shouldMount(true, mount_uuid);
            else if(ar.getMountUUID() != null) ar.shouldMount(true, ar.getMountUUID());
            ar.dismount = 0;
        }
    }

    public static void onMountButton(UUID player_uuid, Mob mob, UUID mount_uuid, int group) {
        if (isControlledMob(mob, player_uuid, group)) {
            CompoundTag nbt = mob.getPersistentData();
            nbt.putBoolean("ShouldMount", true);
            if (mount_uuid != null) {
                nbt.putUUID("MountUUID", mount_uuid);
            }
        }
    }

    public static void onDismountButton(UUID player_uuid, IRecruitEntity recruit, int group) {
        if (recruit instanceof AbstractRecruitEntity ar && ar.isEffectedByCommand(player_uuid, group)){
            ar.shouldMount(false, null);
            if(ar.isPassenger()){
                ar.stopRiding();
                ar.dismount = 180;
            }
        }
    }

    public static void onDismountButton(UUID player_uuid, Mob mob, int group) {
        if (isControlledMob(mob, player_uuid, group)) {
            CompoundTag nbt = mob.getPersistentData();
            nbt.putBoolean("ShouldMount", false);
            if (mob.isPassenger()) {
                mob.stopRiding();
            }
        }
    }

    public static void onProtectButton(UUID player_uuid, IRecruitEntity recruit, UUID protect_uuid, int group) {
        if (recruit instanceof AbstractRecruitEntity ar && ar.isEffectedByCommand(player_uuid, group)){
            ar.shouldProtect(true, protect_uuid);
        }
    }

    public static void onProtectButton(UUID player_uuid, Mob mob, UUID protect_uuid, int group) {
        if (isControlledMob(mob, player_uuid, group)) {
            CompoundTag nbt = mob.getPersistentData();
            nbt.putBoolean("ShouldProtect", true);
            if (protect_uuid != null) {
                nbt.putUUID("ProtectUUID", protect_uuid);
            } else {
                nbt.remove("ProtectUUID");
            }
        }
    }

    public static void onClearTargetButton(UUID player_uuid, IRecruitEntity recruit, int group) {
        if (recruit instanceof AbstractRecruitEntity ar && ar.isEffectedByCommand(player_uuid, group)){
            ar.setTarget(null);
            ar.setLastHurtByPlayer(null);
            ar.setLastHurtMob(null);
            ar.setLastHurtByMob(null);
        }
    }

    public static void onClearTargetButton(UUID player_uuid, Mob mob, int group) {
        if (isControlledMob(mob, player_uuid, group)) {
            mob.setTarget(null);
        }
    }

    public static void onClearUpkeepButton(UUID player_uuid, IRecruitEntity recruit, int group) {
        if (recruit instanceof AbstractRecruitEntity ar && ar.isEffectedByCommand(player_uuid, group)){
            ar.clearUpkeepEntity();
            ar.clearUpkeepPos();
        }
    }

    public static void onClearUpkeepButton(UUID player_uuid, Mob mob, int group) {
        if (isControlledMob(mob, player_uuid, group)) {
            CompoundTag nbt = mob.getPersistentData();
            nbt.remove("UpkeepUUID");
            nbt.remove("UpkeepPosX");
            nbt.remove("UpkeepPosY");
            nbt.remove("UpkeepPosZ");
        }
    }
    public static void onUpkeepCommand(UUID player_uuid, IRecruitEntity recruit, int group, boolean isEntity, UUID entity_uuid, BlockPos blockPos) {
        if (recruit instanceof AbstractRecruitEntity ar && ar.isEffectedByCommand(player_uuid, group)){
            if (isEntity) {
                ar.setUpkeepUUID(Optional.of(entity_uuid));
                ar.clearUpkeepPos();
            }
            else {
                ar.setUpkeepPos(blockPos);
                ar.clearUpkeepEntity();
            }
            ar.forcedUpkeep = true;
            ar.setUpkeepTimer(0);
            onClearTargetButton(player_uuid, (IRecruitEntity) ar, group);
        }
    }

    public static void onUpkeepCommand(UUID player_uuid, Mob mob, int group, boolean isEntity, UUID entity_uuid, BlockPos blockPos) {
        if (isControlledMob(mob, player_uuid, group)) {
            CompoundTag nbt = mob.getPersistentData();
            if (isEntity && entity_uuid != null) {
                nbt.putUUID("UpkeepUUID", entity_uuid);
                nbt.remove("UpkeepPosX");
                nbt.remove("UpkeepPosY");
                nbt.remove("UpkeepPosZ");
            } else {
                nbt.putInt("UpkeepPosX", blockPos.getX());
                nbt.putInt("UpkeepPosY", blockPos.getY());
                nbt.putInt("UpkeepPosZ", blockPos.getZ());
                nbt.remove("UpkeepUUID");
            }
        }
    }

    public static void onShieldsCommand(ServerPlayer serverPlayer, UUID player_uuid, IRecruitEntity recruit, int group, boolean shields) {
        if (recruit instanceof AbstractRecruitEntity ar && ar.isEffectedByCommand(player_uuid, group)){
            ar.setShouldBlock(shields);
        }
    }

    public static void onShieldsCommand(ServerPlayer serverPlayer, UUID player_uuid, Mob mob, int group, boolean shields) {
        if (isControlledMob(mob, player_uuid, group)) {
            mob.getPersistentData().putBoolean("ShouldBlock", shields);
        }
    }

    public static void onRangedFireCommand(ServerPlayer serverPlayer, UUID player_uuid, IRecruitEntity recruit, int group, boolean should) {
        if (recruit instanceof AbstractRecruitEntity ar && ar.isEffectedByCommand(player_uuid, group)){
            ar.setShouldRanged(should);
        }
    }

    public static void onRangedFireCommand(ServerPlayer serverPlayer, UUID player_uuid, Mob mob, int group, boolean should) {
        if (isControlledMob(mob, player_uuid, group)) {
            mob.getPersistentData().putBoolean("ShouldRanged", should);
        }
    }

    public static void onRestCommand(ServerPlayer serverPlayer, UUID player_uuid, IRecruitEntity recruit, int group, boolean should) {
        if (recruit instanceof AbstractRecruitEntity ar && ar.isEffectedByCommand(player_uuid, group)){
            ar.setShouldRest(should);
        }
    }

    public static void onRestCommand(ServerPlayer serverPlayer, UUID player_uuid, Mob mob, int group, boolean should) {
        if (isControlledMob(mob, player_uuid, group)) {
            mob.getPersistentData().putBoolean("ShouldRest", should);
        }
    }

    private static MutableComponent TEXT_HIRE_COSTS(String name, int sollPrice, Item item) {
        return Component.translatable("chat.recruits.text.hire_costs", name, String.valueOf(sollPrice), item.getDescription().getString());
    }

    private static final List<RecruitsGroup> GROUP_DEFAULT_SETTING = new ArrayList<>(
            Arrays.asList(
                    new RecruitsGroup(0, "No Group", false),
                    new RecruitsGroup(1, "Infantry", false),
                    new RecruitsGroup(2, "Ranged", false),
                    new RecruitsGroup(3, "Cavalry", false)
            )
    );
    public static void updateCommandScreen(ServerPlayer player) {
        Main.SIMPLE_CHANNEL.send(PacketDistributor.PLAYER.with(()-> player), new MessageToClientUpdateCommandScreen(getCompoundTagFromRecruitsGroupList(getAvailableGroups(player))));
    }

    public static void updateRecruitInventoryScreen(ServerPlayer player) {
        Main.SIMPLE_CHANNEL.send(PacketDistributor.PLAYER.with(()-> player), new MessageToClientUpdateRecruitInventoryScreen(getCompoundTagFromRecruitsGroupList(loadPlayersGroupsFromNBT(player))));
    }

    public static List<RecruitsGroup> getAvailableGroups(ServerPlayer player) {
        List<Mob> list = Objects.requireNonNull(player.getCommandSenderWorld().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(120)));
        list.removeIf(m -> {
            if(m instanceof AbstractRecruitEntity recruitEntity) {
                return !recruitEntity.isEffectedByCommand(player.getUUID(), 0);
            }
            if(!m.getPersistentData().getBoolean("RecruitControlled")) return true;
            IRecruitEntity recruit = IRecruitEntity.of(m);
            return !(recruit.isOwned() && recruit.isOwnedBy(player.getUUID()));
        });

        List<RecruitsGroup> allGroups = loadPlayersGroupsFromNBT(player);

        Map<Integer, Integer> groupCounts = new HashMap<>();

        for (Mob mob : list) {
            int groupId = mob instanceof AbstractRecruitEntity recruitEntity ? recruitEntity.getGroup() : mob.getPersistentData().getInt("Group");
            groupCounts.put(groupId, groupCounts.getOrDefault(groupId, 0) + 1);
        }

        // Liste der verfügbaren Gruppen erstellen und die Anzahl der Rekruten sowie den disabled-Status aktualisieren
        List<RecruitsGroup> availableGroups = new ArrayList<>();
        for (RecruitsGroup group : allGroups) {
            if (groupCounts.containsKey(group.getId())) {
                group.setCount(groupCounts.get(group.getId()));
                availableGroups.add(group);
            }
        }

        return availableGroups;
    }

    public static List<RecruitsGroup> loadPlayersGroupsFromNBT(Player player) {
        CompoundTag playerNBT = player.getPersistentData();
        CompoundTag nbt = playerNBT.getCompound(Player.PERSISTED_NBT_TAG);

        List<RecruitsGroup> groups = getRecruitsGroupListFormNBT(nbt);

        if(groups.isEmpty())
            groups = GROUP_DEFAULT_SETTING;

        return groups;
    }

    public static void savePlayersGroupsToNBT(ServerPlayer player, List<RecruitsGroup> groups, boolean update) {
        CompoundTag playerNBT = player.getPersistentData();
        CompoundTag nbt = playerNBT.getCompound(Player.PERSISTED_NBT_TAG);

        if(update)
            updateCompoundTag(groups, nbt, player);
        else{
            overrideCompoundTag(groups, nbt, player);
        }


        playerNBT.put(Player.PERSISTED_NBT_TAG, nbt);
    }

    public static List<RecruitsGroup> getRecruitsGroupListFormNBT(CompoundTag nbt){
        List<RecruitsGroup> groups = new ArrayList<>();

        if(nbt.contains("recruits-groups")){
            ListTag groupList = nbt.getList("recruits-groups", 10);
            for (int i = 0; i < groupList.size(); ++i) {
                CompoundTag compoundnbt = groupList.getCompound(i);
                int id = compoundnbt.getInt("id");
                int count = compoundnbt.getInt("count");
                String name = compoundnbt.getString("name");
                boolean disabled = compoundnbt.getBoolean("disabled");

                RecruitsGroup recruitsGroup = new RecruitsGroup(id, name, disabled);
                recruitsGroup.setCount(count);

                groups.add(recruitsGroup);
            }
        }
        return groups;
    }

    public static CompoundTag updateCompoundTag(List<RecruitsGroup> groups, CompoundTag nbt, ServerPlayer player) {
        List<RecruitsGroup> currentList = loadPlayersGroupsFromNBT(player);

        Map<Integer, RecruitsGroup> groupMap = new HashMap<>();
        for (RecruitsGroup group : currentList) {
            groupMap.put(group.getId(), group);
        }

        for (RecruitsGroup group : groups) {
            if (group != null) {
                groupMap.put(group.getId(), group);
            }
        }

        ListTag groupList = new ListTag();
        for (RecruitsGroup group : groupMap.values()) {
            CompoundTag compoundnbt = new CompoundTag();
            compoundnbt.putInt("id", group.getId());
            compoundnbt.putInt("count", group.getCount());
            compoundnbt.putString("name", group.getName());
            compoundnbt.putBoolean("disabled", group.isDisabled());

            groupList.add(compoundnbt);
        }
        nbt.put("recruits-groups", groupList);

        return nbt;
    }

    public static CompoundTag overrideCompoundTag(List<RecruitsGroup> groups, CompoundTag nbt, ServerPlayer player) {
        ListTag groupList = new ListTag();
        for (RecruitsGroup group : groups) {
            CompoundTag compoundnbt = new CompoundTag();
            compoundnbt.putInt("id", group.getId());
            compoundnbt.putInt("count", group.getCount());
            compoundnbt.putString("name", group.getName());
            compoundnbt.putBoolean("disabled", group.isDisabled());

            groupList.add(compoundnbt);
        }

        nbt.put("recruits-groups", groupList);

        return nbt;
    }

    public static CompoundTag getCompoundTagFromRecruitsGroupList(List<RecruitsGroup> groups){
        CompoundTag nbt = new CompoundTag();
        ListTag groupList = new ListTag();
        for (RecruitsGroup group : groups) {
            CompoundTag compoundnbt = new CompoundTag();
            compoundnbt.putInt("id", group.getId());
            compoundnbt.putInt("count", group.getCount());
            compoundnbt.putString("name", group.getName());
            compoundnbt.putBoolean("disabled", group.isDisabled());

            groupList.add(compoundnbt);
        }
        nbt.put("recruits-groups", groupList);

        return nbt;
    }

    private static boolean isControlledMob(Mob mob, UUID player_uuid, int group) {
        CompoundTag nbt = mob.getPersistentData();
        if(!nbt.getBoolean("RecruitControlled")) return false;
        IRecruitEntity recruit = IRecruitEntity.of(mob);
        return recruit.isOwned() && recruit.isOwnedBy(player_uuid) &&
                (group == 0 || recruit.getGroup() == group);
    }

    private static void applyControlledMobMovement(IRecruitEntity recruit, int movementState, ServerPlayer player) {
        Mob mob = recruit instanceof MobRecruit mr ? mr.getMob() : (Mob) recruit;
        CompoundTag nbt = mob.getPersistentData();
        if (nbt.getBoolean("ShouldRest")) {
            return;
        }
        switch (movementState) {
            case 0 -> {
                nbt.putInt("FollowState", 0);
                mob.getNavigation().stop();
            }
            case 1 -> nbt.putInt("FollowState", 1);
            case 2 -> {
                nbt.putInt("FollowState", 2);
                nbt.putDouble("HoldX", mob.getX());
                nbt.putDouble("HoldY", mob.getY());
                nbt.putDouble("HoldZ", mob.getZ());
            }
            case 4 -> {
                nbt.putInt("FollowState", 2);
                nbt.putDouble("HoldX", player.getX());
                nbt.putDouble("HoldY", player.getY());
                nbt.putDouble("HoldZ", player.getZ());
            }
            case 6 -> {
                HitResult hitResult = player.pick(100, 1F, true);
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                    BlockPos pos = blockHitResult.getBlockPos();
                    nbt.putInt("FollowState", 2);
                    nbt.putDouble("HoldX", pos.getX() + 0.5D);
                    nbt.putDouble("HoldY", pos.getY());
                    nbt.putDouble("HoldZ", pos.getZ() + 0.5D);
                    mob.getNavigation().moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 1.0D);
                }
            }
            case 3 -> {
                nbt.putInt("FollowState", 3);
                if (nbt.contains("HoldX") && nbt.contains("HoldY") && nbt.contains("HoldZ")) {
                    double x = nbt.getDouble("HoldX");
                    double y = nbt.getDouble("HoldY");
                    double z = nbt.getDouble("HoldZ");
                    mob.getNavigation().moveTo(x, y, z, 1.0D);
                }
            }
            case 5 -> nbt.putInt("FollowState", 5);
            case 7 -> {
                Vec3 forward = player.getForward();
                Vec3 pos = player.position().add(forward.scale(10));
                BlockPos blockPos = FormationUtils.getPositionOrSurface(
                        player.getCommandSenderWorld(),
                        new BlockPos((int) pos.x, (int) pos.y, (int) pos.z)
                );
                Vec3 tPos = new Vec3(pos.x, blockPos.getY(), pos.z);
                nbt.putInt("FollowState", 3);
                nbt.putDouble("HoldX", tPos.x);
                nbt.putDouble("HoldY", tPos.y);
                nbt.putDouble("HoldZ", tPos.z);
                mob.getNavigation().moveTo(tPos.x, tPos.y, tPos.z, 1.0D);
            }
            case 8 -> {
                Vec3 forward = player.getForward();
                Vec3 pos = player.position().add(forward.scale(-10));
                BlockPos blockPos = FormationUtils.getPositionOrSurface(
                        player.getCommandSenderWorld(),
                        new BlockPos((int) pos.x, (int) pos.y, (int) pos.z)
                );
                Vec3 tPos = new Vec3(pos.x, blockPos.getY(), pos.z);
                nbt.putInt("FollowState", 3);
                nbt.putDouble("HoldX", tPos.x);
                nbt.putDouble("HoldY", tPos.y);
                nbt.putDouble("HoldZ", tPos.z);
                mob.getNavigation().moveTo(tPos.x, tPos.y, tPos.z, 1.0D);
            }
        }
    }
}
