package com.iafenvoy.iceandfire.config;

public class IafConfig {
    private static IafConfig INSTANCE = null;
    public double dreadQueenMaxHealth = 750;
    //public boolean logCascadingWorldGen = false;
    public boolean generateDragonSkeletons = true;
    public int generateDragonSkeletonChance = 300;
    public int generateDragonDenChance = 260;
    public int generateDragonRoostChance = 480;
    public int dragonDenGoldAmount = 4;
    public boolean spawnGlaciers = true;
    public int glacierSpawnChance = 4;
    public int oreToStoneRatioForDragonCaves = 45;
    public int dragonEggTime = 7200;
    public int dragonGriefing = 0;
    public boolean tamedDragonGriefing = true;
    public int dragonFlapNoiseDistance = 4;
    public int dragonFluteDistance = 8;
    public double dragonHealth = 500;
    public int dragonAttackDamage = 17;
    public double dragonAttackDamageFire = 2F;
    public double dragonAttackDamageIce = 2.5F;
    public double dragonAttackDamageLightning = 3.5F;
    public int maxDragonFlight = 256;
    public int dragonGoldSearchLength = 30;
    public boolean canDragonsHealFromBiting;
    public boolean canDragonsDespawn = true;
    public boolean doDragonsSleep = true;
    public boolean dragonDigWhenStuck = true;
    public int dragonBreakBlockCooldown = 5;
    public boolean dragonDropSkull = true;
    public boolean dragonDropHeart = true;
    public boolean dragonDropBlood = true;
    public int dragonTargetSearchLength = 128;
    public int dragonWanderFromHomeDistance = 40;
    public int dragonHungerTickRate = 3000;
    public boolean spawnHippogryphs = true;
    public int hippogryphSpawnRate = 2;
    public boolean generateGorgonTemple = true;
    public double gorgonMaxHealth = 100D;
    public int spawnPixiesChance = 60;
    public int pixieVillageSize = 5;
    public boolean pixiesStealItems = true;
    public int spawnWanderingCyclopsChance = 900;
    public int spawnCyclopsCaveChance = 170;
    public int cyclopesSheepSearchLength = 17;
    public double cyclopsMaxHealth = 150;
    public double cyclopsAttackStrength = 15;
    public double cyclopsBiteStrength = 40;
    public boolean cyclopsGriefing = true;
    public double sirenMaxHealth = 50D;
    public boolean sirenShader = true;
    public int sirenMaxSingTime = 12000;
    public int sirenTimeBetweenSongs = 2000;
    public int generateSirenChance = 400;
    public int hippocampusSpawnChance = 40;
    public int deathWormTargetSearchLength = 48;
    public double deathWormMaxHealth = 10D;
    public double deathWormAttackStrength = 3D;
    public boolean deathWormAttackMonsters = true;
    public int deathWormSpawnRate = 30;
    public int cockatriceChickenSearchLength = 32;
    public int cockatriceEggChance = 30;
    public double cockatriceMaxHealth = 40.0D;
    public boolean chickensLayRottenEggs = true;
    public boolean spawnCockatrices = true;
    public int cockatriceSpawnRate = 4;
    public int stymphalianBirdTargetSearchLength = 48;
    public int stymphalianBirdFeatherDropChance = 25;
    public double stymphalianBirdFeatherAttackStength = 1F;
    public int stymphalianBirdFlockLength = 40;
    public int stymphalianBirdFlightHeight = 80;
    public boolean stymphalianBirdsDataTagDrops = true;
    public boolean stympahlianBirdAttackAnimals = false;
    public int stymphalianBirdSpawnChance = 80;
    public boolean spawnTrolls = true;
    public int trollSpawnRate = 60;
    public boolean trollsDropWeapon = true;
    public double trollMaxHealth = 50;
    public double trollAttackStrength = 10;
    public boolean villagersFearDragons = true;
    public boolean animalsFearDragons = true;
    public int myrmexPregnantTicks = 2500;
    public int myrmexEggTicks = 3000;
    public int myrmexLarvaTicks = 35000;
    public int myrmexColonyGenChance = 150;
    public int myrmexColonySize = 80;
    public int myrmexMaximumWanderRadius = 50;
    public boolean myrmexHiveIgnoreDaytime = false;
    public double myrmexBaseAttackStrength = 3.0D;
    public boolean spawnAmphitheres = true;
    public int amphithereSpawnRate = 50;
    public float amphithereVillagerSearchLength = 48;
    public int amphithereTameTime = 400;
    public double amphithereFlightSpeed = 1.75F;
    public double amphithereMaxHealth = 50D;
    public double amphithereAttackStrength = 7D;
    public int seaSerpentSpawnChance = 250;
    public boolean seaSerpentGriefing = true;
    public double seaSerpentBaseHealth = 20D;
    public double seaSerpentAttackStrength = 4D;
    public double dragonsteelBaseDamage = 25F;
    public int dragonsteelBaseArmor = 12;
    public float dragonsteelBaseArmorToughness = 6;
    public int dragonsteelBaseDurability = 8000;
    public int dragonsteelBaseDurabilityEquipment = 8000;
    public boolean dragonMovedWronglyFix = false;
    public boolean weezerTinkers = true;
    public double dragonBlockBreakingDropChance = 0.1D;
    public boolean generateMausoleums = true;
    public boolean spawnLiches = true;
    public int lichSpawnRate = 4;
    public int lichSpawnChance = 30;
    public double hydraMaxHealth = 250D;
    public int generateHydraChance = 120;
    public boolean explosiveDragonBreath = false;
    public double weezerTinkersDisarmChance = 0.2F;
    public boolean chunkLoadSummonCrystal = true;
    public double dangerousWorldGenDistanceLimit = 1000;
    public double dangerousWorldGenSeparationLimit = 300;
    public double dragonFlightSpeedMod = 1F;
    public double hippogryphFlightSpeedMod = 1F;
    public double hippocampusSwimSpeedMod = 1F;
    public boolean generateGraveyards = true;
    public double ghostMaxHealth = 30;
    public double ghostAttackStrength = 3;
    public boolean ghostsFromPlayerDeaths = true;

    public int dragonPathfindingThreads = 3;
    public int maxDragonPathingNodes = 5000;
    public boolean pathfindingDebug = false;
    public boolean dragonWeaponFireAbility = true;
    public boolean dragonWeaponIceAbility = true;
    public boolean dragonWeaponLightningAbility = true;
    public int villagerHouseWeight = 5;
    public boolean allowAttributeOverriding = true;

    public static IafConfig getInstance() {
        if (INSTANCE == null)
            INSTANCE = ConfigLoader.load(IafConfig.class, "./config/iceandfire/iaf.json", new IafConfig());
        return INSTANCE;
    }
}