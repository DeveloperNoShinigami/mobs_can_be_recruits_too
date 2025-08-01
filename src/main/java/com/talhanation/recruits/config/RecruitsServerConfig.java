package com.talhanation.recruits.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.talhanation.recruits.entities.AbstractRecruitEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class RecruitsServerConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SERVER;
    public static ForgeConfigSpec.BooleanValue RecruitTablesPOIReleasing;
    public static ForgeConfigSpec.BooleanValue OverrideIronGolemSpawn;
    public static ForgeConfigSpec.BooleanValue PillagerFriendlyFire;
    public static ForgeConfigSpec.BooleanValue PillagerSpawn;
    public static ForgeConfigSpec.BooleanValue PillagerAttackMonsters;
    public static ForgeConfigSpec.BooleanValue ShouldPillagersRaidNaturally;
    public static ForgeConfigSpec.BooleanValue MonstersAttackPillagers;
    public static ForgeConfigSpec.IntValue MaxSpawnRecruitsInVillage;
    public static ForgeConfigSpec.IntValue MaxRecruitsForPlayer;
    public static ForgeConfigSpec.IntValue RecruitsMaxXpForLevelUp;
    public static ForgeConfigSpec.IntValue RecruitsMaxXpLevel;
    public static ForgeConfigSpec.BooleanValue PillagerIncreasedCombatRange;
    public static ForgeConfigSpec.BooleanValue VindicatorSpawnItems;
    public static ForgeConfigSpec.BooleanValue PillagerSpawnItems;
    public static ForgeConfigSpec.IntValue MaxAssassinCount;
    public static ForgeConfigSpec.IntValue RecruitCost;
    public static ForgeConfigSpec.IntValue BowmanCost;
    public static ForgeConfigSpec.IntValue ShieldmanCost;
    public static ForgeConfigSpec.IntValue NomadCost;
    public static ForgeConfigSpec.IntValue HorsemanCost;
    public static ForgeConfigSpec.IntValue CrossbowmanCost;
    public static ForgeConfigSpec.ConfigValue<List<String>> TargetBlackList;
    public static ForgeConfigSpec.ConfigValue<List<String>> MountWhiteList;
    public static ForgeConfigSpec.ConfigValue<List<List<String>>> RecruitStartEquipments;
    public static ForgeConfigSpec.ConfigValue<List<List<String>>> ShieldmanStartEquipments;
    public static ForgeConfigSpec.ConfigValue<List<List<String>>> BowmanStartEquipments;
    public static ForgeConfigSpec.ConfigValue<List<List<String>>> CrossbowmanStartEquipments;
    public static ForgeConfigSpec.ConfigValue<List<List<String>>> HorsemanStartEquipments;
    public static ForgeConfigSpec.ConfigValue<List<List<String>>> NomadStartEquipments;
    public static ForgeConfigSpec.ConfigValue<List<String>> AcceptedDamagesourceImmunity;
    public static ForgeConfigSpec.ConfigValue<List<String>> FoodBlackList;
    public static ForgeConfigSpec.BooleanValue RecruitHunger;
    public static ForgeConfigSpec.ConfigValue<List<String>> RecruitFoodList;
    public static ForgeConfigSpec.BooleanValue AggroRecruitsBlockPlaceBreakEvents;
    public static ForgeConfigSpec.BooleanValue NeutralRecruitsBlockPlaceBreakEvents;
    public static ForgeConfigSpec.BooleanValue AggroRecruitsBlockInteractingEvents;
    public static ForgeConfigSpec.BooleanValue NeutralRecruitsBlockInteractingEvents;
    public static ForgeConfigSpec.BooleanValue ShouldRecruitPatrolsSpawn;
    public static ForgeConfigSpec.BooleanValue ShouldPillagerPatrolsSpawn;
    public static ForgeConfigSpec.DoubleValue RecruitPatrolsSpawnChance;
    public static ForgeConfigSpec.DoubleValue PillagerPatrolsSpawnChance;
    public static ForgeConfigSpec.ConfigValue<String> RecruitCurrency;
    public static ForgeConfigSpec.BooleanValue NoDamageImmunity;
    public static ForgeConfigSpec.IntValue PillagerPatrolSpawnInterval;
    public static ForgeConfigSpec.IntValue RecruitPatrolSpawnInterval;
    public static ForgeConfigSpec.IntValue RecruitPatrolDespawnTime;
    public static ForgeConfigSpec.IntValue FactionCreationCost;
    public static ForgeConfigSpec.BooleanValue GlobalTeamFriendlyFireSetting;
    public static ForgeConfigSpec.BooleanValue GlobalTeamSeeFriendlyInvisibleSetting;
    public static ForgeConfigSpec.BooleanValue GlobalTeamSetting;
    public static ForgeConfigSpec.BooleanValue RecruitHorseUnitsHorse;
    public static ForgeConfigSpec.BooleanValue RangedRecruitsNeedArrowsToShoot;
    public static ForgeConfigSpec.BooleanValue RecruitsChunkLoading;
    public static ForgeConfigSpec.BooleanValue UpdateCheckerServerside;
    public static ForgeConfigSpec.BooleanValue CompatCorpseMod;
    public static ForgeConfigSpec.BooleanValue UniversalMobControl;
    public static ForgeConfigSpec.BooleanValue ReplaceMobAI;
    public static ForgeConfigSpec.ConfigValue<List<String>> AdditionalGunItems;
    public static ForgeConfigSpec.BooleanValue PerMobCurrency;
    public static ForgeConfigSpec.ConfigValue<List<String>> MobCurrencyMap;
    public static ForgeConfigSpec.ConfigValue<List<String>> ControlledMobIds;
    public static ForgeConfigSpec.BooleanValue SpawnMobRecruits;
    public static ForgeConfigSpec.BooleanValue ReplaceRecruits;
    public static ForgeConfigSpec.BooleanValue UseAsyncPathfinding;
    public static ForgeConfigSpec.IntValue AsyncPathfindingThreadsCount;
    public static ForgeConfigSpec.BooleanValue UseAsyncTargetFinding;
    public static ForgeConfigSpec.IntValue AsyncTargetFindingThreadsCount;
    public static ForgeConfigSpec.IntValue MaxPlayersInFaction;
    public static ForgeConfigSpec.IntValue MaxNPCsInFaction;
    public static ForgeConfigSpec.BooleanValue ShouldFactionEditingBeAllowed;
    public static ForgeConfigSpec.BooleanValue ShouldFactionManagingBeAllowed;
    public static ForgeConfigSpec.BooleanValue AllowArrowCleaning;
    public static ForgeConfigSpec.BooleanValue RecruitsPayment;
    public static ForgeConfigSpec.IntValue RecruitsPaymentInterval;
    public static ForgeConfigSpec.IntValue RecruitsPaymentAmount;
    public static ForgeConfigSpec.EnumValue<AbstractRecruitEntity.NoPaymentAction> RecruitsNoPaymentAction;
    public static ForgeConfigSpec.BooleanValue QuickStartPillagerRaid;
    public static ArrayList<String> TARGET_BLACKLIST = new ArrayList<>(
            Arrays.asList("minecraft:creeper", "minecraft:ghast", "minecraft:enderman", "minecraft:zombified_piglin", "corpse:corpse", "minecraft:armorstand"));
    public static ArrayList<String> FOOD_BLACKLIST = new ArrayList<>(
            Arrays.asList("minecraft:poisonous_potato", "minecraft:spider_eye", "minecraft:pufferfish"));
    public static ArrayList<String> RECRUIT_FOOD = new ArrayList<>(
            Arrays.asList("minecraft:bread", "minecraft:cooked_beef", "minecraft:cooked_chicken"));
    public static ArrayList<String> MOUNTS = new ArrayList<>(
            Arrays.asList("minecraft:mule", "minecraft:donkey", "minecraft:horse", "minecraft:llama", "minecraft:pig", "minecraft:boat", "minecraft:minecart", "smallships:cog", "smallships:brigg", "smallships:galley", "smallships:drakkar", "camels:camel"));
    public static ArrayList<List<String>> START_EQUIPMENT_RECRUIT = new ArrayList<>(
            List.of(Arrays.asList("minecraft:wooden_sword", "", "", "", "", ""),
                    Arrays.asList("minecraft:stone_sword", "", "", "", "", "")
            ));
    public static ArrayList<List<String>> START_EQUIPMENT_SHIELDMAN = new ArrayList<>(
            List.of(Arrays.asList("minecraft:stone_sword", "minecraft:shield", "", "", "", ""),
                    Arrays.asList("minecraft:wooden_axe", "minecraft:shield", "", "", "", "")
            ));
    public static ArrayList<List<String>> START_EQUIPMENT_HORSEMAN = new ArrayList<>(
            List.of(Arrays.asList("minecraft:stone_sword", "minecraft:shield", "", "", "", ""),
                    Arrays.asList("minecraft:iron_sword", "minecraft:shield", "", "", "", "")
            ));
    public static ArrayList<List<String>> START_EQUIPMENT_BOWMAN = new ArrayList<>(
            List.of(Arrays.asList("minecraft:bow", "", "", "", "", "")
            ));
    public static ArrayList<List<String>> START_EQUIPMENT_NOMAD = new ArrayList<>(
            List.of(Arrays.asList("minecraft:bow", "", "", "", "", "")
            ));
    public static ArrayList<List<String>> START_EQUIPMENT_CROSSBOWMAN = new ArrayList<>(
            List.of(Arrays.asList("minecraft:crossbow", "", "", "", "", "")
            ));
    public static ArrayList<String> DAMAGESOURCE = new ArrayList<>(
            Arrays.asList("inFire", "lava", "sweetBerryBush", "cactus", "lightningBolt", "inWall", "hotFloor", "outOfWorld", "drown"));//add drowning

    public static ArrayList<String> list = new ArrayList<>();

    static {
        BUILDER.comment("Recruits Config:").push("Recruits");

        UpdateCheckerServerside = BUILDER.comment("""
                        
                        UpdateCheckerServerside
                        \t(takes effect after restart)
                        \t
                        Should the client side update checker be active?
                        It is recommended to keep it enabled to receive information about new bug fixes and features.""
                        default: true""")

                .worldRestart()
                .define("UpdateCheckerServerside", true);

        RecruitCurrency = BUILDER.comment("""
                        
                        Currency
                        \t(takes effect after restart)
                        \tThe Item defined here, will be used to hire recruits. For example: ["minecraft:diamond"]\tdefault: ["minecraft:emerald"]""")
                .worldRestart()
                .define("RecruitCurrency", "minecraft:emerald");

        RecruitsMaxXpForLevelUp = BUILDER.comment("""
                        
                        Max XP a Recruit needs to Level Up.
                        \t(takes effect after restart)
                        \tdefault: 250""")
                .worldRestart()
                .defineInRange("RecruitsMaxXpForLevelUp", 250, 50, 14530);

        RecruitsMaxXpLevel = BUILDER.comment("""
                        
                        The max. Level a recruit can get.-
                        \t(takes effect after restart)
                        \tdefault: 20""")
                .worldRestart()
                .defineInRange("RecruitsMaxXpLevel", 20, 10, 1453);

        MaxRecruitsForPlayer = BUILDER.comment("""
                        
                        Max amount a normal player can recruit (Faction Leaders are limited to a different setting)
                        \t(takes effect after restart)
                        \tdefault: 100""")
                .worldRestart()
                .defineInRange("MaxRecruitsForPlayer", 100, 1, 1453);

        TargetBlackList = BUILDER.comment("""
                        
                        Target Blacklist
                        \t(takes effect after restart)
                        \tEntities in this list won't be targeted at all, for example: ["minecraft:creeper", "minecraft:sheep", ...]""")
                .worldRestart()
                .define("TargetBlackList", TARGET_BLACKLIST);

        FoodBlackList = BUILDER.comment("""

                        List of foods that recruits should not eat.
                        \t(takes effect after restart)
                        \tFood items in this list will not be eaten by recruits and also not be picked up from upkeep.""")
                .worldRestart()
                .define("FoodBlackList", FOOD_BLACKLIST);

        RecruitFoodList = BUILDER.comment("""
                        List of foods recruits are allowed to eat. Leave empty to allow any food item not blacklisted.
                        \t(takes effect after restart)
                        default: ["minecraft:bread", "minecraft:cooked_beef", "minecraft:cooked_chicken"]""")
                .worldRestart()
                .define("RecruitFoodList", RECRUIT_FOOD);

        RecruitHunger = BUILDER.comment("""
                        Enable hunger mechanics for recruits.
                        \t(takes effect after restart)
                        default: true""")
                .worldRestart()
                .define("RecruitHunger", true);

        MountWhiteList = BUILDER.comment("""
                        
                        Mount Whitelist
                        \t(takes effect after restart)
                        \tONLY Entities in this list can be mounted by a recruit, for example: ["minecraft:boat", "smallships:cog", ...]""")
                .worldRestart()
                .define("MountWhitelist", MOUNTS);

        RecruitCost = BUILDER.comment("""
                        
                        The amount of currency required to hire a recruit.
                        \t(takes effect after restart)
                        \tdefault: 4""")
                .worldRestart()
                .defineInRange("RecruitCost", 4, 0, 1453);

        BowmanCost = BUILDER.comment("""
                        
                        The amount of currency required to hire a bowman.
                        \t(takes effect after restart)
                        \tdefault: 6""")
                .worldRestart()
                .defineInRange("BowmanCost", 6, 0, 1453);

        CrossbowmanCost = BUILDER.comment("""
                        
                        The amount of currency required to hire a crossbowman.
                        \t(takes effect after restart)
                        \tdefault: 8""")
                .worldRestart()
                .defineInRange("CrossbowmanCost", 8, 0, 1453);

        ShieldmanCost = BUILDER.comment("""
                        
                        The amount of currency required to hire a shieldman.
                        \t(takes effect after restart)
                        \tdefault: 10""")
                .worldRestart()
                .defineInRange("ShieldmanCost", 10, 0, 1453);

        HorsemanCost = BUILDER.comment("""
                        
                        The amount of currency required to hire a horseman.
                        \t(takes effect after restart)
                        \tdefault: 20""")
                .worldRestart()
                .defineInRange("HorsemanCost", 20, 0, 1453);

        NomadCost = BUILDER.comment("""
                        
                        The amount of currency required to hire a nomad.
                        \t(takes effect after restart)
                        \tdefault: 19""")
                .worldRestart()
                .defineInRange("NomadCost", 19, 0, 1453);

        RecruitsPayment = BUILDER.comment("""
                        
                        RecruitsPayment
                        \t(takes effect after restart)
                        \t
                        Should recruits need to be payed in intervals?
                        default: false""")

                .worldRestart()
                .define("RecruitsPayment", false);

        RecruitsPaymentInterval = BUILDER.comment("""
                        
                        RecruitsPaymentInterval
                        \t(takes effect after restart)
                        \t
                        The interval in minutes recruits need to be payed?
                        default: 15""")

                .worldRestart()
                .defineInRange("RecruitsPaymentInterval", 15,1,1453);
        RecruitsPaymentAmount = BUILDER.comment("""
                        
                        RecruitsPaymentAmount
                        \t(takes effect after restart)
                        \t
                        The amount of currency recruits need to be payed?
                        default: 1""")

                .worldRestart()
                .defineInRange("RecruitsPaymentAmount", 1,1,1453);

        RecruitsNoPaymentAction = BUILDER.comment("""
                        
                        RecruitsNoPaymentAction
                        \t(takes effect after restart)
                        \t
                        The action the recruits will do when they don't get payed.
                        default: MORALE_LOSS""")

                .worldRestart()
                .defineEnum("RecruitsNoPaymentAction", AbstractRecruitEntity.NoPaymentAction.MORALE_LOSS);

        RecruitHorseUnitsHorse = BUILDER.comment("""
                        
                        RecruitHorseUnitsHorse
                        \t(takes effect after restart)
                        \t
                        Should the Horse units spawn with a horse?""
                        default: true""")

                .worldRestart()
                .define("RecruitHorseUnitsHorse", true);

        RangedRecruitsNeedArrowsToShoot = BUILDER.comment("""
                        
                        RangedRecruitsNeedArrowsToShoot
                        \t(takes effect after restart)
                        \t
                        Should ranged units need arrows to shoot?
                        If enabled ranged units will resupply arrows from upkeep chest and spawn with arrows.
                        ""
                        default: false""")

                .worldRestart()
                .define("RangedRecruitsNeedArrowsToShoot", false);

        RecruitsChunkLoading = BUILDER.comment("""
                        RecruitsChunkLoading
                        \t(takes effect after restart)
                        \t
                        Should recruit-companions load chunks? Disabling would make patrolling in to unloaded chunk impossible.
                        default: true""")

                .worldRestart()
                .define("RecruitsChunkLoading", true);

        /*
        Village Config
         */
        BUILDER.pop();
        BUILDER.comment("Recruit Village Config:").push("Villages");

        RecruitTablesPOIReleasing = BUILDER.comment("""
                        
                        Should Villager Recruits that were created with Tables release the POI for other Villagers?
                        True -> allows multiple villagers to become a recruit with one table.
                        False -> only one villager can become a recruit with one table.
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("RecruitTablesPOIReleasing", true);

        OverrideIronGolemSpawn = BUILDER.comment("""
                        
                        Should Recruits instead of Iron Golems spawn in Villages 
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("OverrideIronGolemSpawn", true);

        MaxSpawnRecruitsInVillage = BUILDER.comment("""
                        
                        Max Recruits in a Village Chunk to Spawn
                        \t(takes effect after restart)
                        \tdefault: 1""")
                .worldRestart()
                .defineInRange("MaxSpawnRecruitsInVillage", 1, 1, 1453);

         /*
        Equipment Config
         */
        BUILDER.pop();
        BUILDER.comment("Recruits Equipment Config:").push("Equipments");
        BUILDER.comment("""
                
                Following lists will be used to generate starting armor for different recruit types. Each recruit will be equipped according to a random equipment-set defined below.
                
                To create a equipment-set note the following order: ["main-hand", "off-hand", "feet", "legs", "chest", "head"]
                Each set has ONLY 6 entries. Each set is separated with square brackets: RecruitStartEquipments=[[1], [2], [3], ...].
                For example, the following configuration will randomize newly spawned recruits either 1 or 2:
                    1. full leather armor set with wooden sword and shield.
                    2. only gold sword.
                
                    RecruitStartEquipments=[["minecraft:wooden_sword", "minecraft:shield","minecraft:leather_boots","minecraft:leather_leggings","minecraft:leather_chestplate", "minecraft:leather_helmet"], ["minecraft:gold_sword", "", "", "", "", ""]]
                
                There is no limit of armor-sets and modded armor / weapons are also compatible. The mod item-id can be accessed with /give-command.    
                """);
        RecruitStartEquipments = BUILDER.comment("""   
                        
                        Recruit Start Equipments
                        Default:  [["minecraft:wooden_sword", "", "", "", "", ""], ["minecraft:stone_sword", "", "", "", "", ""]]
                        """)
                .worldRestart()
                .define("RecruitStartEquipments", START_EQUIPMENT_RECRUIT);

        ShieldmanStartEquipments = BUILDER.comment("""
                        
                        Shieldman Start Equipments
                        Default: [["minecraft:stone_sword", "minecraft:shield", "", "", "", ""], ["minecraft:wooden_axe", "minecraft:shield", "", "", "", ""]]
                        """)
                .worldRestart()
                .define("ShieldmanStartEquipments", START_EQUIPMENT_SHIELDMAN);

        BowmanStartEquipments = BUILDER.comment("""
                        
                        Bowman Start Equipments
                        Default: [["minecraft:bow", "", "", "", "", ""]]
                        """)
                .worldRestart()
                .define("BowmanStartEquipments", START_EQUIPMENT_BOWMAN);

        CrossbowmanStartEquipments = BUILDER.comment("""
                        
                        Crossbowman Start Equipments
                        Default: [["minecraft:crossbow", "", "", "", "", ""]]
                        """)
                .worldRestart()
                .define("CrossbowmanStartEquipments", START_EQUIPMENT_CROSSBOWMAN);

        HorsemanStartEquipments = BUILDER.comment("""
                        
                        Horseman Start Equipments
                        Default: [["minecraft:stone_sword", "minecraft:shield", "", "", "", ""], ["minecraft:iron_sword", "minecraft:shield", "", "", "", ""]]
                        """)
                .worldRestart()
                .define("HorsemanStartEquipments", START_EQUIPMENT_HORSEMAN);

        NomadStartEquipments = BUILDER.comment("""
                        
                        Nomad Start Equipments
                        Default: [["minecraft:bow", "", "", "", "", ""]]
                        """)
                .worldRestart()
                .define("NomadStartEquipments", START_EQUIPMENT_NOMAD);

        /*
        Pillager Config
         */

        BUILDER.pop();
        BUILDER.comment("Monster and Pillager Config:").push("Hostiles");

        PillagerFriendlyFire = BUILDER.comment("""
                        
                        Should Pillagers do friendly fire
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("PillagerFriendlyFire", true);

        PillagerSpawn = BUILDER.comment("""
                        
                        Should Pillagers spawn naturally
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("PillagerSpawn", false);

        PillagerAttackMonsters = BUILDER.comment("""
                        
                        Should Pillagers attack Monsters
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("PillagerAttackMonsters", false);

        MonstersAttackPillagers = BUILDER.comment("""
                        
                        Should Monsters attack Pillagers
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("MonstersAttackPillagers", false);

        ShouldPillagersRaidNaturally = BUILDER.comment("""
                        
                        Should Pillagers attack all Living
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("ShouldPillagersRaidNaturally", false);

        PillagerIncreasedCombatRange = BUILDER.comment("""
                        
                        Should Pillagers have increased Combat Range, so they can shoot from far away.
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("PillagerIncreasedCombatRange", false);

        VindicatorSpawnItems = BUILDER.comment("""
                        
                        Should Vindicators can spawn with shield and sword and AI to use these.
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("VindicatorSpawnItems", false);

        PillagerSpawnItems = BUILDER.comment("""
                        
                        Should Pillagers can spawn with shield and sword and AI to use these.
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("PillagerSpawnItems", false);

        QuickStartPillagerRaid = BUILDER.comment("""
                        
                        Should the player that burns a ominous banner item get the "Bad Omen" effect?
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("QuickStartPillagerRaid", true);

        /*
        Block Event Config
         */

        BUILDER.pop();
        BUILDER.comment("Block Event Config:").push("BlockEvents");

        AggroRecruitsBlockPlaceBreakEvents = BUILDER.comment("""
                        
                        Should Aggressive Recruits attack enemy players that are placing or breaking blocks immediately?
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("AggroRecruitsBlockPlaceBreakEvents", true);

        NeutralRecruitsBlockPlaceBreakEvents = BUILDER.comment("""
                        
                        Should Neutral Recruits attack enemy players that are placing or breaking blocks immediately?
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("NeutralRecruitsBlockPlaceBreakEvents", true);

        AggroRecruitsBlockInteractingEvents = BUILDER.comment("""
                        
                        Should Aggressive Recruits attack enemy players that are interacting with blocks immediately?
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("AggroRecruitsBlockInteractingEvents", true);

        NeutralRecruitsBlockInteractingEvents = BUILDER.comment("""
                        
                        Should Neutral Recruits attack enemy players that are interacting with blocks immediately?
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("NeutralRecruitsBlockInteractingEvents", true);

        /*
        Patrol Config
         */

        BUILDER.pop();
        BUILDER.comment("Recruit Patrols Config:").push("Patrols");

        ShouldRecruitPatrolsSpawn = BUILDER.comment("""
                        
                        Should Recruits spawn as Patrols in the world?
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("ShouldRecruitPatrolsSpawn", true);

        RecruitPatrolsSpawnChance = BUILDER.comment("""
                        
                        Chance that a Recruit Patrol can spawn. (higher values = higher chance to spawn)
                        \t(takes effect after restart)
                        \tdefault: 15.0""")
                .worldRestart()
                .defineInRange("RecruitPatrolsSpawnChance", 15.0D, 0.0D, 100.0D);

        RecruitPatrolSpawnInterval = BUILDER.comment("""
                        
                        The interval in minutes a Recruit Patrol and Caravan can spawn.
                        \t(takes effect after restart)
                        \tdefault: 30""")
                .worldRestart()
                .defineInRange("RecruitPatrolSpawnInterval", 30, 1, 60);

        RecruitPatrolDespawnTime = BUILDER.comment("""
                        
                        The time in minutes a Recruit Patrol and Caravan will despawn.
                        \t(takes effect after restart)
                        \tdefault: 45""")
                .worldRestart()
                .defineInRange("RecruitPatrolDespawnTime", 45, 1, 1453);


        ShouldPillagerPatrolsSpawn = BUILDER.comment("""
                        
                        Should modded Pillager Patrols spawn in the world?
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("ShouldPillagerPatrolsSpawn", false);

        PillagerPatrolsSpawnChance = BUILDER.comment("""
                        
                        Chance that a modded Pillager Patrol can spawn. (higher values = higher chance to spawn)
                        \t(takes effect after restart)
                        \tdefault: 25.0""")
                .worldRestart()
                .defineInRange("PillagerPatrolsSpawnChance", 25.0D, 0.0D, 100.0D);

        PillagerPatrolSpawnInterval = BUILDER.comment("""
                        
                        The interval in minutes a Pillager Patrol can spawn.
                        \t(takes effect after restart)
                        \tdefault: 45""")
                .worldRestart()
                .defineInRange("PillagerPatrolSpawnInterval", 45, 1, 60);


        /*
        MaxAssassinCount = BUILDER.comment("\n" +"WIP: Max Assassins to buy from the Assassin Leader" + "\n" +
                        "\t" + "(takes effect after restart)" + "\n" +
                        "\t" + "default: 16")
                .worldRestart()
                .defineInRange("MaxAssassinCount", 16, 1, 64);

         */

        BUILDER.pop();
        BUILDER.comment("General Damage Config:").push("Damage");

        NoDamageImmunity = BUILDER.comment("""
                        No damage Immunity
                        \tShould Immunity between hits be disabled?
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("NoDamageImmunity", false);

        AcceptedDamagesourceImmunity = BUILDER.comment("""
                        List of damagesource that accept immunity 
                        \t(takes effect after restart)
                        \tDamagesource in this list will apply a immunity of 0.5s to the entity like normal.""")
                .worldRestart()
                .define("AcceptedDamagesourceImmunity", DAMAGESOURCE);
       /*
        Teams Config
        */

        BUILDER.pop();
        BUILDER.comment("Recruit Faction Config:").push("Factions");

        FactionCreationCost = BUILDER.comment("""
                        
                        The amount of currency needed to create a faction. Set 0 to disable.
                        \t(takes effect after restart)
                        \tdefault: 10""")
                .worldRestart()
                .defineInRange("FactionCreationCost", 10, 0, 1453);

        MaxPlayersInFaction = BUILDER.comment("""

                        The amount of players allowed in a faction. Set 0 for infinite.
                        \t(takes effect after restart)
                        \tdefault: 5""")
                .worldRestart()
                .defineInRange("MaxPlayersInFaction", 5, 0, 1453);

        MaxNPCsInFaction = BUILDER.comment("""

                        The amount of recruits allowed in a faction. Set 0 for infinite.
                        \t(takes effect after restart)
                        \tdefault: 500""")
                .worldRestart()
                .defineInRange("MaxNPCsInFaction", 500, 0, 1453);

        ShouldFactionEditingBeAllowed = BUILDER.comment("""

                        Should editing the faction be allowed by their leaders. (Editing Faction Colors, Name ,...)
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("ShouldFactionEditingBeAllowed", true);

        ShouldFactionManagingBeAllowed = BUILDER.comment("""

                        Should managing the faction be allowed by their leaders. (Add/Remove Players...)
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("ShouldFactionManagingBeAllowed", true);


        BUILDER.comment("Global Faction/Team Settings")

                .push("Global Faction/Team Settings");

        GlobalTeamSetting = BUILDER.comment("""
                        
                        Should Recruits override following team settings on world start for all factions?
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("GlobalTeamSetting", true);

        GlobalTeamFriendlyFireSetting = BUILDER.comment("""
                        Override Friendly fire true/false for all factions on world start
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("GlobalTeamFriendlyFireSetting", false);

        GlobalTeamSeeFriendlyInvisibleSetting = BUILDER.comment("""
                        Override SeeFriendlyInvisible true/false for all factions on world start.
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("GlobalTeamSeeFriendlyInvisibleSetting", true);

        BUILDER.pop();
        BUILDER.pop();
        BUILDER.comment("Recruit Mod compatibility Config:").push("Compat");

        CompatCorpseMod = BUILDER.comment("""
                        This feature is only possible when corpse-mod is installed. Not possible when rpgz-mod is loaded.
                        Should recruits spawn corpse of corpse-mod when dead?.
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("CompatCorpseMod", true);

        UniversalMobControl = BUILDER.comment("""
                        Enable recruits AI for all mobs.
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("UniversalMobControl", false);

        ReplaceMobAI = BUILDER.comment("""
                        If true, replaces existing mob AI when UniversalMobControl is active.
                        Otherwise adds recruit behaviours on top of existing AI.
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("ReplaceMobAI", true);

        AdditionalGunItems = BUILDER.comment("""
                        Additional gun items from other mods treated as firearms.
                        Provide a list of item registry names.
                        \t(takes effect after restart)
                        \tdefault: []""")
                .worldRestart()
                .define("AdditionalGunItems", new ArrayList<>());

        PerMobCurrency = BUILDER.comment("""
                        Use different hire currency for specific mobs. If false, uses global RecruitCurrency.
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("PerMobCurrency", false);

        MobCurrencyMap = BUILDER.comment("""
                        Mapping of mob id to currency item id in the format 'mobid=itemid'.
                        Only used when PerMobCurrency is true.
                        \t(takes effect after restart)
                        \tdefault: []""")
                .worldRestart()
                .define("MobCurrencyMap", new ArrayList<>());

        ControlledMobIds = BUILDER.comment("""
                        List of mob ids that should receive recruit behaviour when UniversalMobControl is disabled.
                        \t(takes effect after restart)
                        \tdefault: []""")
                .worldRestart()
                .define("ControlledMobIds", new ArrayList<>());

        SpawnMobRecruits = BUILDER.comment("""
                        Spawn a mob from ControlledMobIds whenever a recruit entity is created.
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("SpawnMobRecruits", false);

        ReplaceRecruits = BUILDER.comment("""
                        Remove the original recruit entity after spawning the configured mob.
                        Only has effect when SpawnMobRecruits is enabled.
                        \t(takes effect after restart)
                        \tdefault: false""")
                .worldRestart()
                .define("ReplaceRecruits", false);

        BUILDER.pop();
        BUILDER.comment("Recruit Mod performance Config:").push("Performance");

        AllowArrowCleaning = BUILDER.comment("""
                        This feature is cleans up unnecessary Arrows from ground that are eating client and server performance.
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("AllowArrowCleaning", true);

        /*
         Async things config
         */

        BUILDER.comment("Async Settings").push("Async");

        UseAsyncPathfinding = BUILDER.comment("""
                        Use asynchronous pathfinding run on multithread executor.
                        Improves TPS on huge numbers of recruits (and somehow FPS) a lot,
                            but useless if machine has only one physical core available.
                        Can lead to some small delays in path finding.
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("UsePathfindingAsync", true);

        AsyncPathfindingThreadsCount = BUILDER.comment("""
                        How much threads to use for pathfinding.
                        Needs to be calibrated manually.
                        Usually good value is n/4, where n is amount of logical cores (threads) you have on CPU.
                        \t(takes effect after restart)
                        \tdefault: 1""")
                .worldRestart()
                .defineInRange("AsyncPathfindingThreads", 1, 1, Runtime.getRuntime().availableProcessors());

        UseAsyncTargetFinding = BUILDER.comment("""
                        Use asynchronous target finding run on multithread executor.
                        Improves TPS on huge numbers of recruits (and somehow FPS) a lot,
                            but useless if machine has only one physical core available.
                        Can lead to some small delays in target finding (recruits finding who to attack),
                            but I have seen none of case when I was testing so should work fine.
                        \t(takes effect after restart)
                        \tdefault: true""")
                .worldRestart()
                .define("UseTargetFindingAsync", true);

        AsyncTargetFindingThreadsCount = BUILDER.comment("""
                        How much threads to use for target finding.
                        Needs to be calibrated manually.
                        Usually good value is n/6, where n is amount of logical cores (threads) you have on CPU.
                        \t(takes effect after restart)
                        \tdefault: 1""")
                .worldRestart()
                .defineInRange("AsyncTargetFindingThreads", 1, 1, Runtime.getRuntime().availableProcessors());

        SERVER = BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}

