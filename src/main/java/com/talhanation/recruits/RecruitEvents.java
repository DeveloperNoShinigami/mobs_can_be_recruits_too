package com.talhanation.recruits;

import com.talhanation.recruits.compat.IWeapon;
import com.talhanation.recruits.config.RecruitsServerConfig;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import com.talhanation.recruits.entities.ICompanion;
import com.talhanation.recruits.entities.IRecruitEntity;
import com.talhanation.recruits.entities.IRecruitMob;
import com.talhanation.recruits.entities.MessengerEntity;
import com.talhanation.recruits.entities.IRecruitMob;
import com.talhanation.recruits.entities.MobRecruit;
import com.talhanation.recruits.entities.ai.horse.HorseRiddenByRecruitGoal;
import com.talhanation.recruits.init.ModEntityTypes;
import com.talhanation.recruits.inventory.PromoteContainer;
import com.talhanation.recruits.inventory.PromoteMobContainer;
import com.talhanation.recruits.inventory.RecruitInventoryMenu;
import com.talhanation.recruits.inventory.ControlledMobMenu;
import com.talhanation.recruits.network.MessageOpenPromoteScreen;
import com.talhanation.recruits.network.MessageOpenControlledMobPromoteScreen;
import com.talhanation.recruits.world.PillagerPatrolSpawn;
import com.talhanation.recruits.world.RecruitsDiplomacyManager;
import com.talhanation.recruits.world.RecruitsPlayerUnitManager;
import com.talhanation.recruits.world.RecruitsPatrolSpawn;
import com.talhanation.recruits.TeamEvents;
import com.talhanation.recruits.CommandEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.nbt.ListTag;
import com.talhanation.recruits.entities.ai.compat.ControlledMobFollowOwnerGoal;
import com.talhanation.recruits.entities.ai.compat.ControlledMobHoldPosGoal;
import com.talhanation.recruits.entities.ai.compat.ControlledMobWanderGoal;
import com.talhanation.recruits.entities.ai.compat.ControlledMobTargetGoal;
import com.talhanation.recruits.entities.ai.compat.ControlledMobMeleeAttackGoal;
import com.talhanation.recruits.entities.ai.compat.ControlledMobRangedBowAttackGoal;
import com.talhanation.recruits.entities.ai.compat.ControlledMobRestGoal;
import com.talhanation.recruits.entities.ai.compat.ControlledMobRangedMusketAttackGoal;
import com.talhanation.recruits.util.MobRecruitHandler;
import com.talhanation.recruits.util.RecruitHandler;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RecruitEvents {
    private static final Map<ServerLevel, RecruitsPatrolSpawn> RECRUIT_PATROL = new HashMap<>();
    private static final Map<ServerLevel, PillagerPatrolSpawn> PILLAGER_PATROL = new HashMap<>();
    public static RecruitsPlayerUnitManager recruitsPlayerUnitManager;
    public static MinecraftServer server;
    static HashMap<Integer, EntityType<? extends AbstractRecruitEntity>> entitiesByProfession = new HashMap<>() {
        {
            put(0, ModEntityTypes.MESSENGER.get());
            put(1, ModEntityTypes.SCOUT.get());
            put(2, ModEntityTypes.PATROL_LEADER.get());
            put(3, ModEntityTypes.CAPTAIN.get());
        }
    };

    private final RecruitHandler mobRecruitHandler = new MobRecruitHandler();

    public static void promoteRecruit(AbstractRecruitEntity recruit, int profession, String name, ServerPlayer player) {
        EntityType<? extends AbstractRecruitEntity> companionType = entitiesByProfession.get(profession);
        AbstractRecruitEntity abstractRecruit = companionType.create(recruit.getCommandSenderWorld());
        if (abstractRecruit instanceof ICompanion companion) {
            abstractRecruit.setCustomName(Component.literal(name));
            abstractRecruit.copyPosition(recruit);
            companion.applyRecruitValues(recruit);
            companion.setOwnerName(player.getName().getString());

            recruit.discard();
            abstractRecruit.getCommandSenderWorld().addFreshEntity(abstractRecruit);
        }
    }

    /**
     * Promote a controlled mob to a companion role without replacing the entity.
     * The mob keeps its species but gains all companion-related values.
     */
    public static void promoteControlledMob(Mob mob, int profession, String name, ServerPlayer player) {
        EntityType<? extends AbstractRecruitEntity> type = entitiesByProfession.get(profession);
        AbstractRecruitEntity template = type.create(mob.getCommandSenderWorld());
        CompoundTag tag = mob.getPersistentData();
        tag.putInt("CompanionProfession", profession);
        tag.putString("OwnerName", player.getName().getString());
        tag.putString("CustomName", name);

        IRecruitEntity recruit = IRecruitEntity.of(mob);
        recruit.setIsOwned(true);
        recruit.setOwnerUUID(player.getUUID());

        if (template instanceof ICompanion companion) {
            companion.applyRecruitValues(template);
            applyCompanionValuesToControlledMob(template, mob);
        }
        mob.setCustomName(Component.literal(name));
        applyCompanionProfession(mob);
    }

    public static void openPromoteScreen(Player player, AbstractRecruitEntity recruit) {
        if (player instanceof ServerPlayer) {
            NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return recruit.getName();
                }

                @Override
                public AbstractContainerMenu createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
                    return new PromoteContainer(i, playerEntity, recruit);
                }
            }, packetBuffer -> {
                packetBuffer.writeUUID(recruit.getUUID());
            });
        } else {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageOpenPromoteScreen(player, recruit.getUUID()));
        }
    }

    /**
     * Open the promote screen for a controlled mob. Mirrors {@link #openPromoteScreen}
     * but uses a mob instance instead of a recruit entity.
     */
    public static void openControlledMobPromoteScreen(Player player, Mob mob) {
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return mob.getName();
                }

                @Override
                public AbstractContainerMenu createMenu(int i, @NotNull Inventory inv, @NotNull Player p) {
                    return new PromoteMobContainer(i, p, mob);
                }
            }, buffer -> buffer.writeUUID(mob.getUUID()));
        } else {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageOpenControlledMobPromoteScreen(player, mob.getUUID()));
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();

        recruitsPlayerUnitManager = new RecruitsPlayerUnitManager();
        recruitsPlayerUnitManager.load(server.overworld());
    }


    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        recruitsPlayerUnitManager.save(server.overworld());
    }

    @SubscribeEvent
    public void onWorldSave(LevelEvent.Save event){
        recruitsPlayerUnitManager.save(server.overworld());
    }

    @SubscribeEvent
    public void onTeleportEvent(EntityTeleportEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !(event instanceof EntityTeleportEvent.EnderPearl) && !(event instanceof EntityTeleportEvent.ChorusFruit) && !(event instanceof EntityTeleportEvent.EnderEntity)) {
            double targetX = event.getTargetX();
            double targetY = event.getTargetY();
            double targetZ = event.getTargetZ();
            UUID player_uuid = player.getUUID();

            List<AbstractRecruitEntity> recruits = player.getCommandSenderWorld().getEntitiesOfClass(
                    AbstractRecruitEntity.class,
                    player.getBoundingBox()
                            .inflate(64, 32, 64),
                    recruit -> recruit.isAlive() && recruit.getFollowState() == 1 && recruit.getOwnerUUID().equals(player_uuid)
            );

            recruits.forEach(recruit -> recruit.teleportTo(targetX, targetY, targetZ));
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.LevelTickEvent event) {
        if (!event.level.isClientSide && event.level instanceof ServerLevel serverWorld) {
            if (RecruitsServerConfig.ShouldRecruitPatrolsSpawn.get()) {
                RECRUIT_PATROL.computeIfAbsent(serverWorld,
                        serverLevel -> new RecruitsPatrolSpawn(serverWorld));
                RecruitsPatrolSpawn spawner = RECRUIT_PATROL.get(serverWorld);
                spawner.tick();
            }

            if (RecruitsServerConfig.ShouldPillagerPatrolsSpawn.get()) {
                PILLAGER_PATROL.computeIfAbsent(serverWorld,
                        serverLevel -> new PillagerPatrolSpawn(serverWorld));
                PillagerPatrolSpawn pillagerSpawner = PILLAGER_PATROL.get(serverWorld);
                pillagerSpawner.tick();
            }
        }
    }
    private static final Set<Projectile> canceledProjectiles = new HashSet<>();

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity entity = event.getEntity();
        HitResult rayTrace = event.getRayTraceResult();

        if (canceledProjectiles.contains(entity)) {
            return;
        }

        if (!(entity instanceof Projectile projectile)) {
            return;
        }
        Entity owner = projectile.getOwner();

        if(owner == null) return;

        if (rayTrace.getType() != HitResult.Type.ENTITY) {
            return;
        }

        Entity impactEntity = ((EntityHitResult) rayTrace).getEntity();
        String encode = impactEntity.getEncodeId();
        if (encode != null && encode.contains("corpse:corpse")) {
            event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
            return;
        }

        if (!(impactEntity instanceof LivingEntity impactLiving)) {
            return;
        }

        if (projectile instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0) {

            if (!canAttack((LivingEntity) owner, impactLiving)) {
                event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                canceledProjectiles.add(projectile);
                return;
            }
        }

        if (owner instanceof AbstractRecruitEntity recruit) {

            if (impactLiving instanceof Animal animal) {
                Entity passenger = animal.getFirstPassenger();
                if (passenger instanceof AbstractRecruitEntity passengerRecruit) {
                    if (!canAttack(recruit, passengerRecruit)) {

                        event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                        return;
                    }
                } else if (passenger instanceof Player player) {
                    if (!canAttack(recruit, player)) {

                        event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                        return;
                    }
                }
            }

            if (!canAttack(recruit, impactLiving)) {
                event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                return;
            } else {
                recruit.addXp(2);
                recruit.checkLevel();
            }
        } else if (owner instanceof Mob mob && !(mob instanceof AbstractRecruitEntity) && mob.getPersistentData().getBoolean("RecruitControlled")) {
            if (!canAttack(mob, impactLiving)) {
                event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                return;
            } else {
                IRecruitMob recruit = IRecruitMob.of(mob);
                if (recruit instanceof MobRecruit mr) {
                    mr.addXp(2);
                    mr.checkLevel();
                }
            }
        }

        if (owner instanceof AbstractIllager illager && !RecruitsServerConfig.PillagerFriendlyFire.get()) {
            if (illager.isAlliedTo(impactLiving)) {
                event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                canceledProjectiles.add(projectile);
                return;
            }
        }

        if (owner instanceof Player player) {
            if (!canHarmTeam(player, impactLiving)) {
                event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onEntityLeaveWorld(EntityLeaveLevelEvent event) {
        if(event.getLevel().isClientSide()) return;

        Entity entity = event.getEntity();
        if (entity instanceof Projectile projectile) {
            canceledProjectiles.remove(projectile);
        }
    }

    @SubscribeEvent
    public void onPlayerInteractWithCaravan(PlayerInteractEvent.EntityInteract entityInteract) {
        if(entityInteract.getLevel().isClientSide()) return;

        Player player = entityInteract.getEntity();
        Entity interacting = entityInteract.getTarget();

        if (interacting instanceof AbstractChestedHorse chestedHorse) {
            CompoundTag nbt = chestedHorse.getPersistentData();
            if (!nbt.contains("Caravan") || !chestedHorse.hasChest()) {
                return;
            }

            player.getCommandSenderWorld().getEntitiesOfClass(
                    AbstractRecruitEntity.class,
                    player.getBoundingBox().inflate(64F),
                    (recruit) -> !recruit.isOwned() &&
                            (recruit.getName().getString().equals("Caravan Leader") ||
                                    recruit.getName().getString().equals("Caravan Guard"))
            ).forEach((recruit) -> recruit.setTarget(player));
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if(event.getEntity().getCommandSenderWorld().isClientSide()) return;

        {
            Entity sourceEntity = event.getSource().getEntity();
            if (sourceEntity instanceof AbstractRecruitEntity owner && (IWeapon.isMusketModWeapon(owner.getMainHandItem()) || IWeapon.isCGMWeapon(owner.getMainHandItem()))) {
                Entity target = event.getEntity();
                if (target instanceof LivingEntity impactEntity) {

                    if (!canAttack(owner, impactEntity)) {
                        event.setCanceled(true);
                    } else {
                        owner.addXp(2);
                        owner.checkLevel();
                    }
                }
            }
        }

        Entity target = event.getEntity();
        Entity source = event.getSource().getEntity();
        if (target instanceof Mob mobTarget && !(mobTarget instanceof AbstractRecruitEntity) && mobTarget.getPersistentData().getBoolean("RecruitControlled")) {
            IRecruitMob recruit = IRecruitMob.of(mobTarget);
            if (recruit instanceof MobRecruit mr) {
                mr.addXp(1);
                mr.checkLevel();
            }
        }
        if (source instanceof Mob mobSource && !(mobSource instanceof AbstractRecruitEntity) && mobSource.getPersistentData().getBoolean("RecruitControlled")) {
            IRecruitMob recruit = IRecruitMob.of(mobSource);
            if (recruit instanceof MobRecruit mr) {
                mr.addXp(2);
                mr.checkLevel();
            }
        }
        if (source instanceof LivingEntity sourceEntity) {
            if (target.getTeam() == null) return;

            target.getCommandSenderWorld().getEntitiesOfClass(
                    AbstractRecruitEntity.class,
                    target.getBoundingBox().inflate(32D),
                    (recruit) -> recruit.getTarget() == null &&
                            recruit.getTeam() != null &&
                            recruit.getTeam().equals(target.getTeam())
            ).forEach((recruit) -> recruit.setTarget(sourceEntity));
        }
    }

    private static final double DAMAGE_THRESHOLD_PERCENTAGE = 0.75;

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if(event.getEntity().getCommandSenderWorld().isClientSide()) return;

        Entity target = event.getEntity();
        Entity source = event.getSource().getEntity();

        if (!target.getCommandSenderWorld().isClientSide() && target instanceof LivingEntity livingTarget && source instanceof LivingEntity livingSource) {
            if (!canAttack(livingSource, livingTarget)){
                event.setCanceled(true);
            }
            else{
                handleSignificantDamage(livingSource, livingTarget, event.getAmount(), (ServerLevel) livingTarget.getCommandSenderWorld());
            }
        }
    }

    private void handleSignificantDamage(LivingEntity attacker, LivingEntity target, double damage, ServerLevel level) {
        Team attackerTeam = attacker.getTeam();
        Team targetTeam = target.getTeam();

        if (attackerTeam == null || targetTeam == null) return;


        double newHealth = target.getHealth() - damage;
        double damageThreshold = target.getMaxHealth() * DAMAGE_THRESHOLD_PERCENTAGE;


        if (newHealth < damageThreshold) {
            setTeamsAsEnemies(attackerTeam, targetTeam, level);
        }
    }

    private void setTeamsAsEnemies(Team attackerTeam, Team targetTeam, ServerLevel level) {
        String attackerTeamName = attackerTeam.getName();
        String targetTeamName = targetTeam.getName();

        if (TeamEvents.recruitsDiplomacyManager != null) {
            TeamEvents.recruitsDiplomacyManager.setRelation(attackerTeamName, targetTeamName,
                    RecruitsDiplomacyManager.DiplomacyStatus.ENEMY, level);
            TeamEvents.recruitsDiplomacyManager.setRelation(targetTeamName, attackerTeamName,
                    RecruitsDiplomacyManager.DiplomacyStatus.ENEMY, level);
        }
    }

    @SubscribeEvent
    public void onHorseJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof AbstractHorse horse) {
            horse.goalSelector.addGoal(0, new HorseRiddenByRecruitGoal(horse));
        }
    }

    @SubscribeEvent
    public void onMobJoinWorld(EntityJoinLevelEvent event) {
        if(event.getLevel().isClientSide()) return;

        Entity entity = event.getEntity();
        if(entity instanceof Mob mob && !(mob instanceof AbstractRecruitEntity)) {
            CompoundTag nbt = mob.getPersistentData();
            if(nbt.getBoolean("RecruitControlled")) {
                restoreControlledMobInventory(mob);
                if (mob instanceof PathfinderMob pathfinderMob) {
                    applyControlledMobGoals(pathfinderMob);
                }
                applyCompanionProfession(mob);
            }
        }
    }

    @SubscribeEvent
    public void onRecruitJoinWorld(EntityJoinLevelEvent event){
        if(event.getLevel().isClientSide()) return;
        if(!RecruitsServerConfig.SpawnMobRecruits.get()) return;
        Entity ent = event.getEntity();
        if(ent instanceof AbstractRecruitEntity recruit){
            maybeReplaceRecruit(recruit);
        }
    }

    @SubscribeEvent
    public void onControlMobInteract(PlayerInteractEvent.EntityInteract event){
        if(event.getLevel().isClientSide()) return;

        Entity target = event.getTarget();
        if(target instanceof Mob mob && !(target instanceof AbstractRecruitEntity)){
            mobRecruitHandler.handle(event, mob);
        }
    }

    @SubscribeEvent
    public void onControlMobInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event){
        if(event.getLevel().isClientSide()) return;

        Entity target = event.getTarget();
        if(target instanceof Mob mob && !(target instanceof AbstractRecruitEntity)){
            mobRecruitHandler.handle(event, mob);
        }
    }

    @SubscribeEvent
    public void onBlockBreakEvent(BlockEvent.BreakEvent event) {
        if(event.getLevel().isClientSide()) return;
        if (RecruitsServerConfig.AggroRecruitsBlockPlaceBreakEvents.get()) {
            Player blockBreaker = event.getPlayer();
            if (blockBreaker == null) return;

            final boolean[] warn = {false};
            final String[] name = new String[1];
            blockBreaker.getCommandSenderWorld().getEntitiesOfClass(
                    AbstractRecruitEntity.class,
                    blockBreaker.getBoundingBox().inflate(32.0D)
            ).forEach((recruit) -> {
                if (canDamageTargetBlockEvent(recruit, blockBreaker) && recruit.getState() == 1) {
                    recruit.setTarget(blockBreaker);
                }

                if (!warn[0] && canDamageTargetBlockEvent(recruit, blockBreaker) && recruit.getState() == 0 && recruit.isOwned()) {
                    warn[0] = true;
                    name[0] = recruit.getName().toString();
                }
            });

            if (warn[0]) {
                warnPlayer(blockBreaker, TEXT_BLOCK_WARN(name[0]));
            }
        }

        if (RecruitsServerConfig.NeutralRecruitsBlockPlaceBreakEvents.get()) {
            Player blockBreaker = event.getPlayer();
            if (blockBreaker == null) return;

            final boolean[] warn = {false};
            final String[] name = new String[1];

            blockBreaker.getCommandSenderWorld().getEntitiesOfClass(
                    AbstractRecruitEntity.class,
                    blockBreaker.getBoundingBox().inflate(32.0D)
            ).forEach((recruit) -> {
                if (canDamageTargetBlockEvent(recruit, blockBreaker) && recruit.getState() == 0 && recruit.isOwned()) {
                    recruit.setTarget(blockBreaker);
                }

                if (!warn[0] && canDamageTargetBlockEvent(recruit, blockBreaker) && recruit.getState() == 0 && recruit.isOwned()) {
                    warn[0] = true;
                    name[0] = recruit.getName().toString();
                }
            });

            if (warn[0]) {
                warnPlayer(blockBreaker, TEXT_BLOCK_WARN(name[0]));
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        if(event.getLevel().isClientSide()) return;
        if (RecruitsServerConfig.AggroRecruitsBlockPlaceBreakEvents.get()) {
            Entity blockPlacer = event.getEntity();

            if (blockPlacer instanceof LivingEntity livingBlockPlacer) {
                final boolean[] warn = {false};
                final String[] name = new String[1];

                livingBlockPlacer.getCommandSenderWorld().getEntitiesOfClass(
                        AbstractRecruitEntity.class,
                        livingBlockPlacer.getBoundingBox().inflate(32.0D),
                        (recruit) -> canDamageTargetBlockEvent(recruit, livingBlockPlacer)
                ).forEach((recruit) -> {
                    if (recruit.getState() == 1) {
                        recruit.setTarget(livingBlockPlacer);
                    }

                    if (blockPlacer instanceof Player && !warn[0] &&
                            recruit.getState() == 0 && recruit.isOwned()) {
                        warn[0] = true;
                        name[0] = recruit.getName().toString();
                    }
                });

                if (blockPlacer instanceof Player player && warn[0]) {
                    warnPlayer(player, TEXT_BLOCK_WARN(name[0]));
                }
            }
        }

        if (RecruitsServerConfig.NeutralRecruitsBlockPlaceBreakEvents.get()) {
            Entity blockPlacer = event.getEntity();

            final boolean[] warn = {false};
            final String[] name = new String[1];

            if (blockPlacer instanceof LivingEntity livingBlockPlacer) {
                livingBlockPlacer.getCommandSenderWorld().getEntitiesOfClass(
                        AbstractRecruitEntity.class,
                        livingBlockPlacer.getBoundingBox().inflate(32.0D),
                        (recruit) -> canDamageTargetBlockEvent(recruit, livingBlockPlacer) &&
                                recruit.getState() == 0 && recruit.isOwned()
                ).forEach((recruit) -> {
                    recruit.setTarget(livingBlockPlacer);

                    if (blockPlacer instanceof Player && !warn[0]) {
                        warn[0] = true;
                        name[0] = recruit.getName().toString();
                    }
                });

                if (blockPlacer instanceof Player player && warn[0]) {
                    warnPlayer(player, TEXT_BLOCK_WARN(name[0]));
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if(event.getLevel().isClientSide()) return;
        BlockPos pos = event.getHitVec().getBlockPos();
        Player player = event.getEntity();

        BlockState selectedBlock = player.getCommandSenderWorld().getBlockState(pos);
        BlockEntity blockEntity = player.getCommandSenderWorld().getBlockEntity(pos);

        if (selectedBlock.is(BlockTags.BUTTONS) ||
                selectedBlock.is(BlockTags.DOORS) ||
                selectedBlock.is(BlockTags.WOODEN_TRAPDOORS) ||
                selectedBlock.is(BlockTags.WOODEN_BUTTONS) ||
                selectedBlock.is(BlockTags.WOODEN_DOORS) ||
                selectedBlock.is(BlockTags.SHULKER_BOXES) ||
                selectedBlock.is(BlockTags.FENCE_GATES) ||
                selectedBlock.is(BlockTags.ANVIL) ||
                (blockEntity instanceof Container)
        ) {
            if (RecruitsServerConfig.AggroRecruitsBlockInteractingEvents.get()) {
                List<AbstractRecruitEntity> list = player.getCommandSenderWorld().getEntitiesOfClass(
                        AbstractRecruitEntity.class,
                        player.getBoundingBox().inflate(32.0D)
                );
                for (AbstractRecruitEntity recruits : list) {
                    if (canDamageTargetBlockEvent(recruits, player) && recruits.getState() == 1) {
                        recruits.setTarget(player);
                    }
                }

                if (list.stream().anyMatch(recruit -> canDamageTargetBlockEvent(recruit, player) && recruit.getState() == 0 && recruit.isOwned())) {
                    warnPlayer(player, TEXT_INTERACT_WARN(list.get(0).getName().getString()));
                }
            }

            if (RecruitsServerConfig.NeutralRecruitsBlockInteractingEvents.get()) {
                List<AbstractRecruitEntity> list = Objects.requireNonNull(player.getCommandSenderWorld().getEntitiesOfClass(AbstractRecruitEntity.class, player.getBoundingBox().inflate(32.0D)));
                for (AbstractRecruitEntity recruits : list) {
                    if (canDamageTargetBlockEvent(recruits, player) && recruits.getState() == 0 && recruits.isOwned()) {
                        recruits.setTarget(player);
                    }
                }

                if (list.stream().anyMatch(recruit -> canDamageTargetBlockEvent(recruit, player) && recruit.getState() == 0 && recruit.isOwned())) {
                    warnPlayer(player, TEXT_INTERACT_WARN(list.get(0).getName().getString()));
                }
            }
        }
    }

    public boolean canDamageTargetBlockEvent(AbstractRecruitEntity recruit, LivingEntity target) {
        if (recruit.isOwned() && target instanceof AbstractRecruitEntity recruitEntityTarget) {
            if (recruit.getOwnerUUID().equals(recruitEntityTarget.getOwnerUUID())) {
                return false;
            }
            else if (recruit.getTeam() != null && recruitEntityTarget.getTeam() != null && recruit.getTeam().equals(recruitEntityTarget.getTeam())){
                return false;
            }
        } else if (recruit.isOwned() && target instanceof Player player) {
            if (recruit.getOwnerUUID().equals(player.getUUID())) {
                return false;
            }
        } else if (target instanceof AbstractRecruitEntity recruitEntityTarget && recruit.getProtectUUID() != null && recruitEntityTarget.getProtectUUID() != null && recruit.getProtectUUID().equals(recruitEntityTarget.getProtectUUID())) {
            return false;
        }
        return RecruitEvents.canHarmTeamNoFriendlyFire(recruit, target);
    }

    public static boolean canAttack(LivingEntity attacker, LivingEntity target) {
        if (target == null || !target.isAlive()) return false;

        if (target instanceof Player player) {
            return canAttackPlayer(attacker, player);
        } else if (target instanceof AbstractRecruitEntity targetRecruit) {
            return canAttackRecruit(attacker, targetRecruit);
        } else if (target instanceof Mob mob && mob.getPersistentData().getBoolean("RecruitControlled")) {
            return canAttackControlledMob(attacker, mob);
        } else if (target instanceof Animal animal) {
            return canAttackAnimal(attacker, animal);
        } else {
            return canHarmTeam(attacker, target);
        }
    }

    public static boolean canAttackAnimal(LivingEntity attacker, Animal animal) {
        if (attacker instanceof AbstractRecruitEntity recruit ){
            if(recruit.getVehicle() != null && recruit.getVehicle().getUUID().equals(animal.getUUID())) return false;

            if (recruit.getProtectUUID() != null && recruit.getProtectUUID().equals(recruit.getProtectUUID())) return false;

            if(animal.isVehicle()){
                if(animal.getFirstPassenger() instanceof AbstractRecruitEntity targetRecruit) return canAttackRecruit(attacker, targetRecruit);
                if(animal.getFirstPassenger() instanceof Player playerTarget) return canAttackPlayer(attacker, playerTarget);
            }
        }
        return canHarmTeam(attacker, animal);
    }


    public static boolean canAttackPlayer(LivingEntity attacker, Player player) {
        if (attacker instanceof AbstractRecruitEntity recruit) {
            if(player.getUUID().equals(recruit.getOwnerUUID())
                    || player.getUUID().equals(recruit.getProtectUUID())
                    || player.isCreative()
                    || player.isSpectator())
                return false;
        } else if (attacker instanceof Mob mob && mob.getPersistentData().getBoolean("RecruitControlled")) {
            IRecruitEntity recruit = IRecruitEntity.of(mob);
            if (recruit.isOwned() && recruit.isOwnedBy(player.getUUID()))
                return false;
        }
        return canHarmTeam(attacker, player);
    }

    public static boolean canAttackRecruit(LivingEntity attacker, AbstractRecruitEntity targetRecruit) {
        if (attacker.equals(targetRecruit)) return false;

        if (attacker instanceof AbstractRecruitEntity attackerRecruit) {
            if (attackerRecruit.isOwned() && targetRecruit.isOwned() &&
                    attackerRecruit.getOwnerUUID().equals(targetRecruit.getOwnerUUID())) {
                return false;
            }

            if (attackerRecruit.getTeam() != null && targetRecruit.getTeam() != null &&
                    attackerRecruit.getTeam().equals(targetRecruit.getTeam()) &&
                    !attackerRecruit.getTeam().isAllowFriendlyFire()) {
                return false;
            }

            if (attackerRecruit.getProtectUUID() != null &&
                    attackerRecruit.getProtectUUID().equals(targetRecruit.getProtectUUID())) {
                return false;
            }

            if(targetRecruit instanceof MessengerEntity messenger && messenger.isAtMission()) return false;
        } else if (attacker instanceof Mob mob && mob.getPersistentData().getBoolean("RecruitControlled")) {
            IRecruitEntity attackerRecruit = IRecruitEntity.of(mob);
            if (attackerRecruit.isOwned() && targetRecruit.isOwned() &&
                    attackerRecruit.isOwnedBy(targetRecruit.getOwnerUUID())) {
                return false;
            }
            if (mob.getTeam() != null && targetRecruit.getTeam() != null &&
                    mob.getTeam().equals(targetRecruit.getTeam()) && !mob.getTeam().isAllowFriendlyFire()) {
                return false;
            }
        }

        return canHarmTeam(attacker, targetRecruit);
    }

    private static boolean canAttackControlledMob(LivingEntity attacker, Mob mob) {
        CompoundTag nbt = mob.getPersistentData();
        if (attacker.getType() == mob.getType()) {
            return false;
        }

        if (attacker instanceof AbstractRecruitEntity attackerRecruit) {
            IRecruitEntity targetRecruit = IRecruitEntity.of(mob);
            if (attackerRecruit.isOwned() && targetRecruit.isOwned() &&
                attackerRecruit.getOwnerUUID() != null &&
                attackerRecruit.getOwnerUUID().equals(targetRecruit.getOwnerUUID())) {
                return false;
            }
            if (attackerRecruit.getTeam() != null && mob.getTeam() != null &&
                attackerRecruit.getTeam().equals(mob.getTeam()) &&
                !attackerRecruit.getTeam().isAllowFriendlyFire()) {
                return false;
            }
        } else if (attacker instanceof Mob attackerMob && attackerMob.getPersistentData().getBoolean("RecruitControlled")) {
            IRecruitEntity attackerRecruit = IRecruitEntity.of(attackerMob);
            IRecruitEntity targetRecruit = IRecruitEntity.of(mob);
            if (attackerRecruit.isOwned() && targetRecruit.isOwned() &&
                    attackerRecruit.getOwnerUUID() != null &&
                    attackerRecruit.getOwnerUUID().equals(targetRecruit.getOwnerUUID())) {
                return false;
            }
            if (attackerMob.getTeam() != null && mob.getTeam() != null &&
                    attackerMob.getTeam().equals(mob.getTeam()) && !attackerMob.getTeam().isAllowFriendlyFire()) {
                return false;
            }
        }
        return canHarmTeam(attacker, mob);
    }

    public static boolean isAlly(Team team1, Team team2) {
        if (team1 == null || team2 == null || TeamEvents.recruitsDiplomacyManager == null) {
            return false;
        }
        return TeamEvents.recruitsDiplomacyManager.getRelation(team1.getName(), team2.getName()) ==
                RecruitsDiplomacyManager.DiplomacyStatus.ALLY;
    }

    public static boolean isEnemy(Team team1, Team team2) {
        if (team1 == null || team2 == null || TeamEvents.recruitsDiplomacyManager == null) {
            return false;
        }
        return TeamEvents.recruitsDiplomacyManager.getRelation(team1.getName(), team2.getName()) ==
                RecruitsDiplomacyManager.DiplomacyStatus.ENEMY;
    }

    public static boolean isNeutral(Team team1, Team team2) {
        if (team1 == null || team2 == null || TeamEvents.recruitsDiplomacyManager == null) {
            return true;
        }
        return TeamEvents.recruitsDiplomacyManager.getRelation(team1.getName(), team2.getName()) ==
                RecruitsDiplomacyManager.DiplomacyStatus.NEUTRAL;
    }

    public static boolean canHarmTeam(LivingEntity attacker, LivingEntity target) {
        Team attackerTeam = attacker.getTeam();
        Team targetTeam = target.getTeam();

        if (attackerTeam == null || targetTeam == null) return true;


        if (attackerTeam.equals(targetTeam) && !attackerTeam.isAllowFriendlyFire()) return false;


        return !isAlly(attackerTeam, targetTeam);
    }

    public static boolean canHarmTeamNoFriendlyFire(LivingEntity attacker, LivingEntity target) {
        Team team = attacker.getTeam();
        Team team1 = target.getTeam();

        if (team == null) {
            return true;

        } else if (team1 == null) {
            return true;
        }
        else if(team == team1){
            return false;
        }
        else {
            RecruitsDiplomacyManager.DiplomacyStatus relation = TeamEvents.recruitsDiplomacyManager.getRelation(team.getName(), team1.getName());

            return relation != RecruitsDiplomacyManager.DiplomacyStatus.ALLY;
        }
    }

    @SubscribeEvent
    public void onRecruitDeath(LivingDeathEvent event) {
        Entity target = event.getEntity();

        if (target instanceof AbstractRecruitEntity recruit) {
            if (!recruit.getIsOwned() || server.overworld().isClientSide()) return;

            //Morale loss when recruits teammate die
            UUID owner = recruit.getOwnerUUID();
            recruit.getCommandSenderWorld().getEntitiesOfClass(
                    AbstractRecruitEntity.class,
                    recruit.getBoundingBox().inflate(64.0D),
                    (entity) -> entity.getOwnerUUID() != null && entity.getOwnerUUID().equals(owner)
            ).forEach((entity) -> {
                float currentMoral = entity.getMorale();
                float newMorale = currentMoral - 0.2F;
                entity.setMoral(Math.max(newMorale, 0F));
            });
        }
    }

    @SubscribeEvent
    public void onControlledMobDeath(LivingDeathEvent event) {
        Entity target = event.getEntity();
        if(!(target instanceof Mob mob) || target instanceof AbstractRecruitEntity) return;
        CompoundTag nbt = mob.getPersistentData();
        if(nbt.getBoolean("RecruitControlled")) {
            dropControlledMobInventory(mob);
        }
    }

    @SubscribeEvent
    public void onControlledMobKill(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        Entity source = event.getSource().getEntity();
        if (source instanceof Mob mob && !(mob instanceof AbstractRecruitEntity) && mob.getPersistentData().getBoolean("RecruitControlled")) {
            IRecruitMob recruit = IRecruitMob.of(mob);
            if (recruit instanceof MobRecruit mr) {
                mr.addXp(5);
                mr.setKills(mr.getKills() + 1);
                LivingEntity victim = event.getEntity();
                if (victim instanceof Player) {
                    mr.addXp(45);
                }
                if (victim instanceof Raider) {
                    mr.addXp(5);
                }
                if (victim instanceof WitherBoss) {
                    mr.addXp(99);
                }
                if (victim instanceof IronGolem) {
                    mr.addXp(49);
                }
                if (victim instanceof EnderDragon) {
                    mr.addXp(999);
                }
                mr.checkLevel();
            }
        }
    }

    private final List<AbstractArrow> trackedArrows = new ArrayList<>();
    private int tickCounter = 0;

    @SubscribeEvent
    public void onWorldTickArrowCleaner(TickEvent.LevelTickEvent event) {//for 1.18 and 1.19 use TickEvent.WorldTickEvent
        if (event.level.isClientSide()) return;
        if (!RecruitsServerConfig.AllowArrowCleaning.get()) return;
        if (event.phase != TickEvent.Phase.END) return;
        if (server == null) return;


        if (++tickCounter < 100) return; // Alle 5 Sekunden
        tickCounter = 0;


        List<AbstractArrow> arrows = event.level.getEntitiesOfClass(AbstractArrow.class, event.level.getWorldBorder().getCollisionShape().bounds());
        trackedArrows.addAll(arrows);


        Iterator<AbstractArrow> iterator = trackedArrows.iterator();
        while (iterator.hasNext()) {
            AbstractArrow arrow = iterator.next();
            if (arrow.pickup == AbstractArrow.Pickup.DISALLOWED && arrow.inGroundTime > 300) {
                arrow.discard();
                iterator.remove();
            }
        }
    }
    private void removeArrow(Entity entity){
        if(entity instanceof AbstractArrow arrow && arrow.pickup == AbstractArrow.Pickup.DISALLOWED && arrow.inGroundTime > 300){
            entity.discard();
        }
    }

    @SubscribeEvent
    public void onControlledMobTick(LivingEvent.LivingTickEvent event){
        if(event.getEntity().level().isClientSide()) return;
        if(!(event.getEntity() instanceof Mob mob) || mob instanceof AbstractRecruitEntity) return;
        CompoundTag nbt = mob.getPersistentData();
        if(!nbt.getBoolean("RecruitControlled") || !IRecruitEntity.of(mob).isOwned()) return;

        if(RecruitsServerConfig.RecruitsPayment.get()){
            int timer = nbt.getInt("PaymentTimer");
            if(timer > 0){
                nbt.putInt("PaymentTimer", timer - 1);
            }
            if(timer == 0){
                doControlledNoPaymentAction(mob);
                resetControlledMobPaymentTimer(mob);
            }
        }
    }

    public static void resetControlledMobPaymentTimer(Mob mob){
        mob.getPersistentData().putInt("PaymentTimer", AbstractRecruitEntity.getPaymentIntervalTicks());
    }

    public static void doControlledNoPaymentAction(Mob mob){
        AbstractRecruitEntity.NoPaymentAction action = RecruitsServerConfig.RecruitsNoPaymentAction.get();
        CompoundTag nbt = mob.getPersistentData();
        IRecruitEntity recruit = IRecruitEntity.of(mob);
        switch (action){
            case DISBAND_KEEP_TEAM -> recruit.setIsOwned(false);
            case DISBAND -> {
                recruit.setIsOwned(false);
                recruit.setGroup(0);
            }
            case DESPAWN -> mob.discard();
            case MORALE_LOSS -> {}
        }
    }

    public byte getSavedWarning(Player player) {
        CompoundTag playerNBT = player.getPersistentData();
        CompoundTag nbt = playerNBT.getCompound(Player.PERSISTED_NBT_TAG);

        return nbt.getByte("RecruitWarnings");
    }

    public void saveCurrentWarning(Player player, byte x) {
        CompoundTag playerNBT = player.getPersistentData();
        CompoundTag nbt = playerNBT.getCompound(Player.PERSISTED_NBT_TAG);

        nbt.putByte("RecruitWarnings", x);
        playerNBT.put(Player.PERSISTED_NBT_TAG, nbt);
    }

    private void warnPlayer(Player player, Component component) {
        saveCurrentWarning(player, (byte) (getSavedWarning(player) + 1));

        if (getSavedWarning(player) >= 0) {
            player.sendSystemMessage(component);
            saveCurrentWarning(player, (byte) -10);
        }
    }

    public static MutableComponent TEXT_BLOCK_WARN(String name) {
        return Component.translatable("chat.recruits.text.block_placing_warn", name);
    }

    public static MutableComponent TEXT_INTERACT_WARN(String name) {
        return Component.translatable("chat.recruits.text.block_interact_warn", name);
    }

    /**
     * Prepare a vanilla mob to behave as a recruit by setting up the same NBT
     * keys used by {@link com.talhanation.recruits.entities.AbstractRecruitEntity}.
     * Defaults mirror those written in {@code addAdditionalSaveData}: level 1,
     * zero XP, 50 hunger and morale and a fresh payment timer.
     */
    public static void initializeControlledMob(Mob mob) {
        if (mob instanceof PathfinderMob pathfinderMob) {
            applyControlledMobGoals(pathfinderMob);
        }
        CompoundTag nbt = mob.getPersistentData();
        nbt.putBoolean("RecruitControlled", true);
        if(!nbt.contains("HireCost")) nbt.putInt("HireCost", 1);
        IRecruitEntity recruit = IRecruitEntity.of(mob);
        recruit.setIsOwned(false);
        recruit.setGroup(0);
        recruit.setOwnerUUID(null);
        nbt.putInt("FollowState", 0);
        nbt.putInt("AggroState", 0); // neutral by default; mobs engage only enemies or threats
        nbt.putBoolean("Listen", true);
        nbt.putInt("PaymentTimer", AbstractRecruitEntity.getPaymentIntervalTicks());
      
        // initialize fields also used by recruits so that newly controlled mobs
        // behave consistently with freshly spawned recruits
        nbt.putInt("Xp", 0);                   // start with no experience
        nbt.putInt("Level", 1);                // level 1 like applySpawnValues()
        nbt.putFloat("Hunger", 50F);           // default hunger
        nbt.putFloat("Moral", 50F);            // default morale
        restoreControlledMobInventory(mob);
    }

    public static void applyControlledMobGoals(PathfinderMob pathfinderMob) {
        if (RecruitsServerConfig.ReplaceMobAI.get()) {
            try {
                java.lang.reflect.Field f = pathfinderMob.goalSelector.getClass().getDeclaredField("availableGoals");
                f.setAccessible(true);
                ((java.util.Set<?>) f.get(pathfinderMob.goalSelector)).clear();
                ((java.util.Set<?>) f.get(pathfinderMob.targetSelector)).clear();
            } catch (Exception ignored) {
            }
        }
        pathfinderMob.goalSelector.addGoal(8, new ControlledMobWanderGoal(pathfinderMob, 1.0D));
        pathfinderMob.goalSelector.addGoal(9, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(pathfinderMob, Player.class, 8.0F));
        pathfinderMob.goalSelector.addGoal(10, new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(pathfinderMob));
        pathfinderMob.goalSelector.addGoal(7, new ControlledMobFollowOwnerGoal(pathfinderMob, 1.0D, 6.0F, 2.0F));
        pathfinderMob.goalSelector.addGoal(6, new ControlledMobHoldPosGoal(pathfinderMob, 1.0D));
        pathfinderMob.goalSelector.addGoal(5, new ControlledMobRestGoal(pathfinderMob));
        ItemStack mainHand = pathfinderMob.getItemBySlot(EquipmentSlot.MAINHAND);
        if (isMusket(mainHand)) {
            pathfinderMob.goalSelector.addGoal(4, new ControlledMobRangedMusketAttackGoal(pathfinderMob, 3.0D));
        } else if (pathfinderMob instanceof RangedAttackMob ranged) {
            pathfinderMob.goalSelector.addGoal(4, new ControlledMobRangedBowAttackGoal<>((PathfinderMob & RangedAttackMob) ranged, 1.0D, 20, 15.0F));
        } else {
            Goal attack = new ControlledMobMeleeAttackGoal(pathfinderMob, 1.2D, true);
            pathfinderMob.goalSelector.addGoal(4, wrapAggroCheck(pathfinderMob, attack));
        }
        pathfinderMob.targetSelector.addGoal(1, new HurtByTargetGoal(pathfinderMob));
        pathfinderMob.targetSelector.addGoal(2, wrapAggroCheck(pathfinderMob, new ControlledMobTargetGoal(pathfinderMob)));
    }

    private static Goal wrapAggroCheck(PathfinderMob mob, Goal goal) {
        return new Goal() {
            {
                this.setFlags(goal.getFlags());
            }

            @Override
            public boolean canUse() {
                return mob.getPersistentData().getInt("AggroState") != 3 && goal.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return mob.getPersistentData().getInt("AggroState") != 3 && goal.canContinueToUse();
            }

            @Override
            public void start() {
                goal.start();
            }

            @Override
            public void stop() {
                goal.stop();
            }

            @Override
            public void tick() {
                goal.tick();
            }

            @Override
            public boolean requiresUpdateEveryTick() {
                return goal.requiresUpdateEveryTick();
            }
        };
    }

    private static boolean isMusket(ItemStack stack) {
        String id = stack.getDescriptionId();
        return id.equals("item.musketmod.musket") ||
                id.equals("item.musketmod.musket_with_bayonet") ||
                id.equals("item.musketmod.musket_with_scope") ||
                id.equals("item.musketmod.blunderbuss") ||
                id.equals("item.musketmod.pistol") ||
                IWeapon.isCGMWeapon(stack);
    }

    private static void maybeReplaceRecruit(AbstractRecruitEntity recruit){
        List<String> ids = RecruitsServerConfig.ControlledMobIds.get();
        if(ids.isEmpty()) return;
        ResourceLocation id = ResourceLocation.tryParse(ids.get(recruit.getRandom().nextInt(ids.size())));
        if(id == null || !ForgeRegistries.ENTITY_TYPES.containsKey(id)) return;
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(id);
        if(!(type instanceof EntityType)) return;
        Entity e = type.create(recruit.getCommandSenderWorld());
        if(!(e instanceof Mob mob)) return;
        mob.moveTo(recruit.getX(), recruit.getY(), recruit.getZ(), recruit.getYRot(), recruit.getXRot());
        initializeControlledMob(mob);
        recruit.getCommandSenderWorld().addFreshEntity(mob);
        if(RecruitsServerConfig.ReplaceRecruits.get()){
            recruit.discard();
        }
    }
  
    public static void restoreControlledMobInventory(Mob mob) {
        CompoundTag tag = mob.getPersistentData();
        if (!tag.contains("MobInventory")) return;
        ListTag list = tag.getList("MobInventory", 10);
        ItemStack[] extra = new ItemStack[RecruitInventoryMenu.INV_SIZE];
        for(int i = 0; i < list.size(); i++) {
            CompoundTag ct = list.getCompound(i);
            int slot = ct.getByte("Slot") & 255;
            if (slot < extra.length) {
                extra[slot] = ItemStack.of(ct);
            }
        }
        mob.setItemSlot(EquipmentSlot.HEAD, getOrEmpty(extra,0));
        mob.setItemSlot(EquipmentSlot.CHEST, getOrEmpty(extra,1));
        mob.setItemSlot(EquipmentSlot.LEGS, getOrEmpty(extra,2));
        mob.setItemSlot(EquipmentSlot.FEET, getOrEmpty(extra,3));
        mob.setItemSlot(EquipmentSlot.OFFHAND, getOrEmpty(extra,4));
        mob.setItemSlot(EquipmentSlot.MAINHAND, getOrEmpty(extra,5));
        if(tag.contains("MobData")){
            CompoundTag data = tag.getCompound("MobData");
            for(String key : ControlledMobMenu.EXTRA_KEYS){
                if(data.contains(key)) tag.put(key, data.get(key).copy());
            }
        }
        if (!(mob instanceof IRecruitMob)) {
            MobRecruit.get(mob).reloadInventory();
        }
    }

    private static ItemStack getOrEmpty(ItemStack[] arr, int idx) {
        return idx >= 0 && idx < arr.length && arr[idx] != null ? arr[idx] : ItemStack.EMPTY;
    }

    public static void dropControlledMobInventory(Mob mob) {
        CompoundTag tag = mob.getPersistentData();
        if (!tag.contains("MobInventory")) return;
        ListTag list = tag.getList("MobInventory", 10);
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = ItemStack.of(list.getCompound(i));
            if (!stack.isEmpty()) mob.spawnAtLocation(stack);
        }
        tag.remove("MobInventory");
        tag.remove("MobData");
    }

    /**
     * Copy companion-specific values from a recruit template back into a controlled mob.
     */
    private static void applyCompanionValuesToControlledMob(AbstractRecruitEntity recruit, Mob mob) {
        CompoundTag tag = mob.getPersistentData();

        // Preserve existing values if present so promotion keeps mob progress.
        float hunger = tag.contains("Hunger") ? tag.getFloat("Hunger") : recruit.getHunger();
        float moral = tag.contains("Moral") ? tag.getFloat("Moral") : recruit.getMorale();
        int level = tag.contains("Level") ? tag.getInt("Level") : recruit.getXpLevel();
        int xp = tag.contains("Xp") ? tag.getInt("Xp") : recruit.getXp();

        tag.putFloat("Hunger", hunger);
        tag.putFloat("Moral", moral);
        tag.putInt("Level", level);
        tag.putInt("Xp", xp);

        ItemStack[] current = new ItemStack[RecruitInventoryMenu.INV_SIZE];
        if(tag.contains("MobInventory")) {
            ListTag existing = tag.getList("MobInventory", 10);
            for(int i=0;i<existing.size();i++) {
                CompoundTag ct = existing.getCompound(i);
                int slot = ct.getByte("Slot") & 255;
                if(slot < current.length) current[slot] = ItemStack.of(ct);
            }
        }

        ListTag list = new ListTag();
        for(int i=6;i<recruit.getInventory().getContainerSize();i++) {
            ItemStack stack = current[i] != null ? current[i] : recruit.getInventory().getItem(i);
            if(!stack.isEmpty()) {
                CompoundTag ct = new CompoundTag();
                ct.putByte("Slot", (byte)i);
                stack.save(ct);
                list.add(ct);
            }
        }
        tag.put("MobInventory", list);
        if (!(mob instanceof IRecruitMob)) {
            MobRecruit.get(mob).reloadInventory();
        }
    }

    public static void applyCompanionProfession(Mob mob) {
        CompoundTag tag = mob.getPersistentData();
        if (!tag.contains("CompanionProfession")) return;
        int profession = tag.getInt("CompanionProfession");
        EntityType<? extends AbstractRecruitEntity> type = entitiesByProfession.get(profession);
        if (type == null) return;
        AbstractRecruitEntity template = type.create(mob.getCommandSenderWorld());
        if (template instanceof ICompanion companion) {
            companion.applyRecruitValues(template);
            applyCompanionValuesToControlledMob(template, mob);
        }
        if (mob instanceof PathfinderMob pathfinderMob) {
            applyControlledMobGoals(pathfinderMob);
        }
    }
  
}
