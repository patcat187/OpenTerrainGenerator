package com.pg85.otg.configuration.biome;

import com.pg85.otg.OTG;
import com.pg85.otg.common.materials.LocalMaterialData;
import com.pg85.otg.common.materials.LocalMaterials;
import com.pg85.otg.configuration.ConfigFile;
import com.pg85.otg.configuration.ConfigFunction;
import com.pg85.otg.configuration.biome.BiomeConfigFinder.BiomeConfigStub;
import com.pg85.otg.configuration.biome.settings.ReplacedBlocksMatrix;
import com.pg85.otg.configuration.biome.settings.WeightedMobSpawnGroup;
import com.pg85.otg.configuration.biome.settings.ReplacedBlocksMatrix.ReplacedBlocksInstruction;
import com.pg85.otg.configuration.io.SettingsMap;
import com.pg85.otg.configuration.settingType.Setting;
import com.pg85.otg.configuration.standard.BiomeStandardValues;
import com.pg85.otg.configuration.standard.PluginStandardValues;
import com.pg85.otg.configuration.standard.StandardBiomeTemplate;
import com.pg85.otg.configuration.standard.WorldStandardValues;
import com.pg85.otg.configuration.world.WorldConfig;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.generator.resource.*;
import com.pg85.otg.generator.surface.SimpleSurfaceGenerator;
import com.pg85.otg.generator.surface.SurfaceGenerator;
import com.pg85.otg.generator.terrain.TerrainShapeBase;
import com.pg85.otg.logging.LogMarker;
import com.pg85.otg.util.BiomeResourceLocation;
import com.pg85.otg.util.helpers.StreamHelper;
import com.pg85.otg.util.helpers.StringHelper;
import com.pg85.otg.util.minecraft.defaults.BiomeRegistryNames;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class BiomeConfig extends ConfigFile
{
	private final BiomeResourceLocation registryKey;	
    private StandardBiomeTemplate defaultSettings;
    public WorldConfig worldConfig;
	
    public String biomeExtends;
    private boolean doResourceInheritance = true;

    public String riverBiome;
    public float riverHeight;
    public float riverVolatility;
    public double[] riverHeightMatrix;

    public int biomeSize;
    public int biomeSizeWhenIsle;
    public int biomeSizeWhenBorder;
    public int biomeRarity;
    public int biomeRarityWhenIsle;

    public int biomeColor;

    public List<String> biomeIsBorder;
    public List<String> isleInBiome;
    public List<String> notBorderNear;

    // Surface config
    public float biomeHeight;
    public float biomeVolatility;
    public int smoothRadius;
    public int CHCSmoothRadius;

    public float biomeTemperature;
    public float biomeWetness;

    public LocalMaterialData stoneBlock;
    public LocalMaterialData surfaceBlock;
    public LocalMaterialData groundBlock;
    public ReplacedBlocksMatrix replacedBlocks;
    public SurfaceGenerator surfaceAndGroundControl;

    public String replaceToBiomeName;

    public boolean useWorldWaterLevel;
    public int waterLevelMax;
    public int waterLevelMin;
    public LocalMaterialData waterBlock;
    public LocalMaterialData iceBlock;
    public LocalMaterialData cooledLavaBlock;

    public int riverWaterLevel;

    private int configWaterLevelMax;
    private int configWaterLevelMin;
    private LocalMaterialData configWaterBlock;
    private LocalMaterialData configIceBlock;
    private LocalMaterialData configCooledLavaBlock;
    private int configRiverWaterLevel;

    public int skyColor;
    public int waterColor;

    public int grassColor;
    public int grassColor2;
    public boolean grassColorIsMultiplier;
    public int foliageColor;
    public int foliageColor2;
    public boolean foliageColorIsMultiplier;
    
    public int fogColor;
    public float fogDensity;
    public float fogTimeWeight;
    public float fogRainWeight;
    public float fogThunderWeight;

    public List<ConfigFunction<BiomeConfig>> resourceSequence = new ArrayList<ConfigFunction<BiomeConfig>>();
    private List<CustomStructureGen> customStructures = new ArrayList<CustomStructureGen>(); // Used as a cache for fast querying, not saved
    
    public boolean inheritSaplingResource;
    private Map<SaplingType, SaplingGen> saplingGrowers = new EnumMap<SaplingType, SaplingGen>(SaplingType.class);
    private Map<LocalMaterialData, SaplingGen> customSaplingGrowers = new HashMap<>();
    private Map<LocalMaterialData, SaplingGen> customBigSaplingGrowers = new HashMap<>();

    public CustomStructureGen structureGen;
    private ArrayList<String> biomeObjectStrings;

    public double maxAverageHeight;
    public double maxAverageDepth;
    public double volatility1;
    public double volatility2;
    public double volatilityWeight1;
    public double volatilityWeight2;
    private double volatilityRaw1;
    private double volatilityRaw2;
    private double volatilityWeightRaw1;
    private double volatilityWeightRaw2;
    public boolean disableNotchHeightControl;
    public double[] heightMatrix;

    // Structures
    public boolean strongholdsEnabled;
    public boolean oceanMonumentsEnabled;
    public boolean woodLandMansionsEnabled;
    public boolean netherFortressesEnabled;

    // Forge Biome Dict Id

    public String biomeDictId;

	// Mob spawning and mob inheritance (also used to inherit modded mobs from vanilla biomes for Forge))

	private String inheritMobsBiomeName;

    // Spawn Config
    public List<WeightedMobSpawnGroup> spawnMonsters = new ArrayList<WeightedMobSpawnGroup>();
    public List<WeightedMobSpawnGroup> spawnCreatures = new ArrayList<WeightedMobSpawnGroup>();
    public List<WeightedMobSpawnGroup> spawnWaterCreatures = new ArrayList<WeightedMobSpawnGroup>();
    public List<WeightedMobSpawnGroup> spawnAmbientCreatures = new ArrayList<WeightedMobSpawnGroup>();

    public List<WeightedMobSpawnGroup> spawnMonstersMerged = new ArrayList<WeightedMobSpawnGroup>();
    public List<WeightedMobSpawnGroup> spawnCreaturesMerged = new ArrayList<WeightedMobSpawnGroup>();
    public List<WeightedMobSpawnGroup> spawnWaterCreaturesMerged = new ArrayList<WeightedMobSpawnGroup>();
    public List<WeightedMobSpawnGroup> spawnAmbientCreaturesMerged = new ArrayList<WeightedMobSpawnGroup>();
    
    public enum VillageType
    {
        disabled,
        wood,
        sandstone,
        taiga,
        savanna
    }

    public VillageType villageType;

    public enum MineshaftType
    {
        disabled,
        normal,
        mesa
    }

    public MineshaftType mineshaftType = MineshaftType.normal;
    public double mineshaftsRarity;

    public enum RareBuildingType
    {
        disabled,
        desertPyramid,
        jungleTemple,
        swampHut,
        igloo
    }

    public RareBuildingType rareBuildingType;

    public BiomeConfig(BiomeLoadInstruction loadInstruction, BiomeConfigStub biomeConfigStub, SettingsMap settings, WorldConfig worldConfig, String presetName)
    {
        super(loadInstruction.getBiomeName());

        this.registryKey = new BiomeResourceLocation(presetName, this.getName());

        // Mob inheritance
        // Mob spawning data was already loaded seperately before the rest of the biomeconfig to make inheritance work properly
        // Forge: If this is a vanilla biome then mob spawning settings have been inherited from vanilla MC biomes
        // This includes any mobs added to vanilla biomes by other mods when MC started.

        if(biomeConfigStub != null)
        {
	        spawnMonsters.addAll(biomeConfigStub.spawnMonsters);
	        spawnCreatures.addAll(biomeConfigStub.spawnCreatures);
	        spawnWaterCreatures.addAll(biomeConfigStub.spawnWaterCreatures);
	        spawnAmbientCreatures.addAll(biomeConfigStub.spawnAmbientCreatures);

	        spawnMonstersMerged.addAll(biomeConfigStub.spawnMonstersMerged);
	        spawnCreaturesMerged.addAll(biomeConfigStub.spawnCreaturesMerged);
	        spawnWaterCreaturesMerged.addAll(biomeConfigStub.spawnWaterCreaturesMerged);
	        spawnAmbientCreaturesMerged.addAll(biomeConfigStub.spawnAmbientCreaturesMerged);
        }

        this.worldConfig = worldConfig;
        this.defaultSettings = loadInstruction.getBiomeTemplate();

        this.renameOldSettings(settings);
        this.readConfigSettings(settings);

        this.correctSettings(true);

        // Add default resources when needed
        if (settings.isNewConfig())
        {
            this.resourceSequence.addAll(defaultSettings.createDefaultResources(this));
            for (ConfigFunction<BiomeConfig> res : this.resourceSequence)
            {
            	if(res instanceof CustomStructureGen)
            	{
            		this.customStructures.add((CustomStructureGen)res);
            	}
            }
        }

        // Set water level
        if (this.useWorldWaterLevel)
        {
            this.waterLevelMax = worldConfig.waterLevelMax;
            this.waterLevelMin = worldConfig.waterLevelMin;
            this.waterBlock = worldConfig.waterBlock;
            this.iceBlock = worldConfig.iceBlock;
            this.cooledLavaBlock = worldConfig.cooledLavaBlock;
            this.riverWaterLevel = worldConfig.waterLevelMax;
        } else {
            this.waterLevelMax = this.configWaterLevelMax;
            this.waterLevelMin = this.configWaterLevelMin;
            this.waterBlock = this.configWaterBlock;
            this.iceBlock = this.configIceBlock;
            this.cooledLavaBlock = this.configCooledLavaBlock;
            this.riverWaterLevel = this.configRiverWaterLevel;
        }
    }
    
	public BiomeResourceLocation getRegistryKey()
	{
		return this.registryKey;
	}

    public List<CustomStructureGen> getCustomStructures()
    {
    	return this.customStructures;
    }
    
    /**
     * This is a pretty weak map from -0.5 to ~-0.8 (min vanilla temperature)
     *
     * TODO: We should probably make this more configurable in the future?
     *
     * @param temp The temp to get snow height for
     * @return A value from 0 to 7 to be used for snow height
     */
    public int getSnowHeight(float temp)
    {
    	// OTG biome temperature is between 0.0 and 2.0.
    	// Judging by WorldStandardValues.SNOW_AND_ICE_MAX_TEMP, snow should appear below 0.15.
    	// According to the configs, snow and ice should appear between 0.2 (at y > 90) and 0.1 (entire biome covered in ice).
    	// Let's make sure that at 0.2, snow layers start with thickness 0 at y 90 and thickness 7 around y 255.
    	// In a 0.2 temp biome, y90 temp is 0.156, y255 temp is -0.12
    	   	
    	float snowTemp = WorldStandardValues.SNOW_AND_ICE_TEMP;
    	if(temp <= snowTemp)
    	{
        	float maxColdTemp = WorldStandardValues.SNOW_AND_ICE_MAX_TEMP;
        	float maxThickness = 7.0f;
        	if(temp < maxColdTemp)
        	{
        		return (int)maxThickness;
        	}
        	float range = Math.abs(maxColdTemp - snowTemp);
        	float fraction = Math.abs(maxColdTemp - temp);
    		return (int)Math.floor((1.0f - (fraction / range)) * maxThickness);
    	}

    	return  0;
    }

    public SaplingGen getSaplingGen(SaplingType type)
    {
        SaplingGen gen = saplingGrowers.get(type);
        if (gen == null && type.growsTree())
        {
            gen = saplingGrowers.get(SaplingType.All);
        }
        return gen;
    }

    public SaplingGen getCustomSaplingGen(LocalMaterialData materialData, boolean wideTrunk)
    {
    	// TODO: Re-implement this when block data works
        if (wideTrunk)
        {
        	return customBigSaplingGrowers.get(materialData);
            //return customBigSaplingGrowers.get(materialData.withBlockData(materialData.getBlockData() % 8));
        }
        return customSaplingGrowers.get(materialData);
        //return customSaplingGrowers.get(materialData.withBlockData(materialData.getBlockData() % 8));
    }

    @Override
    protected void readConfigSettings(SettingsMap settings)
    {
        this.biomeExtends = settings.getSetting(BiomeStandardValues.BIOME_EXTENDS);

        this.doResourceInheritance = settings.getSetting(BiomeStandardValues.RESOURCE_INHERITANCE);
        this.biomeSize = settings.getSetting(BiomeStandardValues.BIOME_SIZE, defaultSettings.defaultSize);
        this.biomeRarity = settings.getSetting(BiomeStandardValues.BIOME_RARITY, defaultSettings.defaultRarity);
        this.biomeRarityWhenIsle = settings.getSetting(BiomeStandardValues.BIOME_RARITY_WHEN_ISLE, defaultSettings.defaultRarityWhenIsle);

        this.biomeColor = settings.getSetting(BiomeStandardValues.BIOME_COLOR, defaultSettings.defaultColor);

        this.riverBiome = settings.getSetting(BiomeStandardValues.RIVER_BIOME, defaultSettings.defaultRiverBiome);

        this.isleInBiome = settings.getSetting(BiomeStandardValues.ISLE_IN_BIOME, defaultSettings.defaultIsle);
        this.biomeSizeWhenIsle = settings.getSetting(BiomeStandardValues.BIOME_SIZE_WHEN_ISLE, defaultSettings.defaultSizeWhenIsle);
        this.biomeIsBorder = settings.getSetting(BiomeStandardValues.BIOME_IS_BORDER, defaultSettings.defaultBorder);
        this.notBorderNear = settings.getSetting(BiomeStandardValues.NOT_BORDER_NEAR, defaultSettings.defaultNotBorderNear);
        this.biomeSizeWhenBorder = settings.getSetting(BiomeStandardValues.BIOME_SIZE_WHEN_BORDER, defaultSettings.defaultSizeWhenBorder);

        this.biomeTemperature = settings.getSetting(BiomeStandardValues.BIOME_TEMPERATURE, defaultSettings.defaultBiomeTemperature);
        this.biomeWetness = settings.getSetting(BiomeStandardValues.BIOME_WETNESS, defaultSettings.defaultBiomeWetness);

        this.replaceToBiomeName = settings.getSetting(BiomeStandardValues.REPLACE_TO_BIOME_NAME, defaultSettings.defaultReplaceToBiomeName);

        this.biomeHeight = settings.getSetting(BiomeStandardValues.BIOME_HEIGHT, defaultSettings.defaultBiomeSurface);
        this.biomeVolatility = settings.getSetting(BiomeStandardValues.BIOME_VOLATILITY, defaultSettings.defaultBiomeVolatility);
        this.smoothRadius = settings.getSetting(BiomeStandardValues.SMOOTH_RADIUS);
        this.CHCSmoothRadius = settings.getSetting(BiomeStandardValues.CUSTOM_HEIGHT_CONTROL_SMOOTH_RADIUS);

        this.stoneBlock = settings.getSetting(BiomeStandardValues.STONE_BLOCK);
        this.surfaceBlock = settings.getSetting(BiomeStandardValues.SURFACE_BLOCK, defaultSettings.defaultSurfaceBlock);
        this.groundBlock = settings.getSetting(BiomeStandardValues.GROUND_BLOCK, defaultSettings.defaultGroundBlock);
        this.replacedBlocks = settings.getSetting(BiomeStandardValues.REPLACED_BLOCKS);
        this.surfaceAndGroundControl = readSurfaceAndGroundControlSettings(settings);

        this.useWorldWaterLevel = settings.getSetting(BiomeStandardValues.USE_WORLD_WATER_LEVEL);
        this.configWaterLevelMax = settings.getSetting(BiomeStandardValues.WATER_LEVEL_MAX);
        this.configWaterLevelMin = settings.getSetting(BiomeStandardValues.WATER_LEVEL_MIN);
        this.configWaterBlock = settings.getSetting(BiomeStandardValues.WATER_BLOCK);
        this.configIceBlock = settings.getSetting(BiomeStandardValues.ICE_BLOCK);
        this.configCooledLavaBlock = settings.getSetting(BiomeStandardValues.COOLED_LAVA_BLOCK);

        this.skyColor = settings.getSetting(BiomeStandardValues.SKY_COLOR);
        this.waterColor = settings.getSetting(BiomeStandardValues.WATER_COLOR, defaultSettings.defaultWaterColorMultiplier);
        this.grassColor = settings.getSetting(BiomeStandardValues.GRASS_COLOR, defaultSettings.defaultGrassColor);
        this.grassColor2 = settings.getSetting(BiomeStandardValues.GRASS_COLOR_2, defaultSettings.defaultGrassColor);
        this.grassColorIsMultiplier = settings.getSetting(BiomeStandardValues.GRASS_COLOR_IS_MULTIPLIER);
        this.foliageColor = settings.getSetting(BiomeStandardValues.FOLIAGE_COLOR, defaultSettings.defaultFoliageColor);
        this.foliageColor2 = settings.getSetting(BiomeStandardValues.FOLIAGE_COLOR_2, defaultSettings.defaultFoliageColor);
        this.foliageColorIsMultiplier = settings.getSetting(BiomeStandardValues.FOLIAGE_COLOR_IS_MULTIPLIER);
        this.fogColor = settings.getSetting(BiomeStandardValues.FOG_COLOR);
        this.fogDensity = settings.getSetting(BiomeStandardValues.FOG_DENSITY);

        this.fogTimeWeight = settings.getSetting(BiomeStandardValues.FOG_TIME_WEIGHT);
        this.fogRainWeight = settings.getSetting(BiomeStandardValues.FOG_RAIN_WEIGHT);
        this.fogThunderWeight = settings.getSetting(BiomeStandardValues.FOG_THUNDER_WEIGHT);
        
        this.volatilityRaw1 = settings.getSetting(BiomeStandardValues.VOLATILITY_1);
        this.volatilityRaw2 = settings.getSetting(BiomeStandardValues.VOLATILITY_2);
        this.volatilityWeightRaw1 = settings.getSetting(BiomeStandardValues.VOLATILITY_WEIGHT_1);
        this.volatilityWeightRaw2 = settings.getSetting(BiomeStandardValues.VOLATILITY_WEIGHT_2);
        this.disableNotchHeightControl = settings.getSetting(BiomeStandardValues.DISABLE_BIOME_HEIGHT, defaultSettings.defaultDisableBiomeHeight);
        this.maxAverageHeight = settings.getSetting(BiomeStandardValues.MAX_AVERAGE_HEIGHT);
        this.maxAverageDepth = settings.getSetting(BiomeStandardValues.MAX_AVERAGE_DEPTH);

        this.riverHeight = settings.getSetting(BiomeStandardValues.RIVER_HEIGHT);
        this.riverVolatility = settings.getSetting(BiomeStandardValues.RIVER_VOLATILITY);
        this.configRiverWaterLevel = settings.getSetting(BiomeStandardValues.RIVER_WATER_LEVEL);

        this.strongholdsEnabled = settings.getSetting(BiomeStandardValues.STRONGHOLDS_ENABLED, defaultSettings.defaultStrongholds);
        this.oceanMonumentsEnabled = settings.getSetting(BiomeStandardValues.OCEAN_MONUMENTS_ENABLED, defaultSettings.defaultOceanMonuments);
        this.woodLandMansionsEnabled = settings.getSetting(BiomeStandardValues.WOODLAND_MANSIONS_ENABLED, defaultSettings.defaultWoodlandMansions);
        this.netherFortressesEnabled = settings.getSetting(BiomeStandardValues.NETHER_FORTRESSES_ENABLED, defaultSettings.defaultNetherFortressEnabled);
        this.villageType = settings.getSetting(BiomeStandardValues.VILLAGE_TYPE, defaultSettings.defaultVillageType);
        this.mineshaftsRarity = settings.getSetting(BiomeStandardValues.MINESHAFT_RARITY);
        this.mineshaftType = settings.getSetting(BiomeStandardValues.MINESHAFT_TYPE, defaultSettings.defaultMineshaftType);
        this.rareBuildingType = settings.getSetting(BiomeStandardValues.RARE_BUILDING_TYPE, defaultSettings.defaultRareBuildingType);

        this.biomeDictId = settings.getSetting(BiomeStandardValues.BIOME_DICT_ID, defaultSettings.defaultBiomeDictId);
    	this.inheritMobsBiomeName = settings.getSetting(BiomeStandardValues.INHERIT_MOBS_BIOME_NAME, defaultSettings.defaultInheritMobsBiomeName);

        this.readCustomObjectSettings(settings);        
        this.readResourceSettings(settings);
        this.inheritSaplingResource = settings.getSetting(BiomeStandardValues.INHERIT_SAPLING_RESOURCE, defaultSettings.inheritSaplingResource);
        this.heightMatrix = new double[this.worldConfig.worldHeightCap / TerrainShapeBase.PIECE_Y_SIZE + 1];
        this.readHeightSettings(settings, this.heightMatrix, BiomeStandardValues.CUSTOM_HEIGHT_CONTROL, defaultSettings.defaultCustomHeightControl);
        this.riverHeightMatrix = new double[this.worldConfig.worldHeightCap / TerrainShapeBase.PIECE_Y_SIZE + 1];
        this.readHeightSettings(settings, this.riverHeightMatrix, BiomeStandardValues.RIVER_CUSTOM_HEIGHT_CONTROL, defaultSettings.defaultCustomHeightControl);
    }

    private void readHeightSettings(SettingsMap settings, double[] heightMatrix, Setting<double[]> setting, double[] defaultValue)
    {
        double[] keys = settings.getSetting(setting, defaultValue);
        for (int i = 0; i < heightMatrix.length && i < keys.length; i++)
            heightMatrix[i] = keys[i];
    }

    private SurfaceGenerator readSurfaceAndGroundControlSettings(SettingsMap settings)
    {
        // Get default value
        SurfaceGenerator defaultSetting;
        if (settings.isNewConfig())
        {
            String defaultString = StringHelper.join(defaultSettings.defaultSurfaceSurfaceAndGroundControl, ",");
            try
            {
                defaultSetting = BiomeStandardValues.SURFACE_AND_GROUND_CONTROL.read(defaultString);
            } catch (InvalidConfigException e) {
                throw new AssertionError(e);
            }
        } else {
            defaultSetting = new SimpleSurfaceGenerator();
        }

        return settings.getSetting(BiomeStandardValues.SURFACE_AND_GROUND_CONTROL, defaultSetting);
    }

    private void readResourceSettings(SettingsMap settings)
    {
    	// Disable resourceinheritance for saplings
        for (ConfigFunction<BiomeConfig> res : settings.getConfigFunctions(this, false))
        {
            if (res != null)
            {
                if (res instanceof SaplingGen)
                {
                    SaplingGen sapling = (SaplingGen) res;
                    if (sapling.saplingType == SaplingType.Custom)
                    {
                        try
                        {// Puts big custom saplings in the big list and small in the small list
                            if (sapling.wideTrunk)
                                customBigSaplingGrowers.put(sapling.saplingMaterial, sapling);
                            else
                                customSaplingGrowers.put(sapling.saplingMaterial, sapling);
                        }
                        catch (NullPointerException e)
                        {
                            OTG.log(LogMarker.WARN, "Unrecognized sapling type in biome "+this.getName());
                        }
                    }
                    else
                    {
                        this.saplingGrowers.put(sapling.saplingType, sapling);
                    }
                }
            }
        }

        for (ConfigFunction<BiomeConfig> res : settings.getConfigFunctions(this, this.doResourceInheritance))
        {
            if (res != null)
            {
                if (!(res instanceof SaplingGen))
                {
                	this.resourceSequence.add(res);
                }
                if(res instanceof CustomStructureGen)
                {
            		this.customStructures.add((CustomStructureGen)res);
                }
            }
        }
    }

    private void readCustomObjectSettings(SettingsMap settings)
    {
        biomeObjectStrings = new ArrayList<String>();

        // Read from BiomeObjects setting
        List<String> customObjectStrings = settings.getSetting(BiomeStandardValues.BIOME_OBJECTS);
        for (String customObjectString : customObjectStrings)
        {
            biomeObjectStrings.add(customObjectString);
        }
    }

    @Override
    protected void writeConfigSettings(SettingsMap writer)
    {
        writer.bigTitle("Biome Inheritance");

        writer.putSetting(BiomeStandardValues.BIOME_EXTENDS, this.biomeExtends,
            "This should be the value of the biomeConfig you wish to extend.",
            "The extended config will be loaded, at which point the configs included below",
            "will overwrite any configs loaded from the extended config.");

        writer.putSetting(BiomeStandardValues.RESOURCE_INHERITANCE, this.doResourceInheritance,
            "When set to true, all resources of the parent biome (if any) will be copied",
            "to the resources queue of this biome, except for saplings. If a resource in",
            "the parent biome looks very similar to that of a child biome (for example, ",
            "two ores of the same type it won't be copied.");

        // Biome placement
        writer.bigTitle("Biome placement");

        writer.putSetting(BiomeStandardValues.BIOME_SIZE, this.biomeSize,
            "Biome size from 0 to GenerationDepth. Defines in which biome layer this biome will be generated (see GenerationDepth).",
            "Higher numbers give a smaller biome, lower numbers a larger biome.",
            "How this setting is used depends on the value of BiomeMode in the WorldConfig.",
            "It will be used for:",
            "- normal biomes, ice biomes, isle biomes and border biomes when BiomeMode is set to BeforeGroups",
            "- biomes spawned as part of a BiomeGroup when BiomeMode is set to Normal.",
            "  For biomes spawned as isles, borders or rivers other settings are available.",
            "  Isle biomes:   " + BiomeStandardValues.BIOME_SIZE_WHEN_ISLE + " (see below)",
            "  Border biomes: " + BiomeStandardValues.BIOME_SIZE_WHEN_BORDER + " (see below)",
            "  River biomes:  " + WorldStandardValues.RIVER_SIZE + " (see WorldConfig)");

        writer.putSetting(BiomeStandardValues.BIOME_RARITY, this.biomeRarity,
            "Biome rarity from 100 to 1. If this is normal or ice biome - chance for spawn this biome then others.",
            "Example for normal biome :",
            "  100 rarity mean 1/6 chance than other ( with 6 default normal biomes).",
            "  50 rarity mean 1/11 chance than other",
            "For isle biomes see the " + BiomeStandardValues.BIOME_RARITY_WHEN_ISLE + " setting below.",
            "Doesn`t work on Ocean and River (frozen versions too) biomes when not added as normal biome.");

        writer.putSetting(BiomeStandardValues.BIOME_COLOR, this.biomeColor,
            "The hexadecimal color value of this biome. Used in the output of the /otg map command,",
            "and used in the input of BiomeMode: FromImage.");

        //if (this.defaultSettings.isCustomBiome)
        //{

        writer.putSetting(BiomeStandardValues.REPLACE_TO_BIOME_NAME, this.replaceToBiomeName,
            "Replace this biome to specified after the terrain is generated.",
            "This will make the world files contain the id of the specified biome, instead of the id of this biome.",
            "This will cause saplings, colors and mob spawning work as in specified biome." +
            "To replace to minecraft biomes use resourcelocation notation, for instance: minecraft:plains." + "");
        //}

        writer.smallTitle("Isle biomes only",
            "To spawn a biome as an isle, you need to add it first to the",
            WorldStandardValues.ISLE_BIOMES + " list in the WorldConfig.",
            "");

        writer.putSetting(BiomeStandardValues.ISLE_IN_BIOME, this.isleInBiome,
            "List of biomes in which this biome will spawn as an isle.",
            "For example, Mushroom Isles spawn inside the Ocean biome.");

        writer.putSetting(BiomeStandardValues.BIOME_SIZE_WHEN_ISLE, this.biomeSizeWhenIsle,
            "Size of this biome when spawned as an isle biome in BiomeMode: Normal.",
            "Valid values range from 0 to GenerationDepth.",
            "Larger numbers give *smaller* islands. The biome must be smaller than the biome it's going",
            "to spawn in, so the " + BiomeStandardValues.BIOME_SIZE_WHEN_ISLE
                    + " number must be larger than the " + BiomeStandardValues.BIOME_SIZE + " of the other biome.");

        writer.putSetting(BiomeStandardValues.BIOME_RARITY_WHEN_ISLE, this.biomeRarityWhenIsle,
            "Rarity of this biome when spawned as an isle biome in BiomeMode: Normal.");

        writer.smallTitle("Border biomes only",
            "To spawn a biome as a border, you need to add it first to the",
            WorldStandardValues.BORDER_BIOMES + " list in the WorldConfig.",
            "");

        writer.putSetting(BiomeStandardValues.BIOME_IS_BORDER, this.biomeIsBorder,
            "List of biomes this biome can be a border of.",
            "For example, the Beach biome is a border on the Ocean biome, so",
            "it can spawn anywhere on the border of an ocean.");

        writer.putSetting(BiomeStandardValues.NOT_BORDER_NEAR, this.notBorderNear,
            "List of biomes that cancel spawning of this biome.",
            "For example, the Beach biome will never spawn next to an Extreme Hills biome.");

        writer.putSetting(BiomeStandardValues.BIOME_SIZE_WHEN_BORDER, this.biomeSizeWhenBorder,
            "Size of this biome when spawned as a border biome in BiomeMode: Normal.",
            "Valid values range from 0 to GenerationDepth.",
            "Larger numbers give *smaller* borders. The biome must be smaller than the biome it's going",
            "to spawn in, so the " + BiomeStandardValues.BIOME_SIZE_WHEN_BORDER
            + " number must be larger than the " + BiomeStandardValues.BIOME_SIZE + " of the other biome.");

        // Terrain height and volatility
        writer.bigTitle("Terrain height and volatility");

        writer.putSetting(BiomeStandardValues.BIOME_HEIGHT, this.biomeHeight,
            "BiomeHeight mean how much height will be added in terrain generation",
            "It is double value from -10.0 to 10.0",
            "Value 0.0 equivalent half of map height with all other default settings");

        writer.putSetting(BiomeStandardValues.BIOME_VOLATILITY, this.biomeVolatility,
            "Biome volatility.");

        writer.putSetting(BiomeStandardValues.SMOOTH_RADIUS, this.smoothRadius,
            "Smooth radius between biomes. Must be between 0 and 32, inclusive. The resulting",
            "smooth radius seems to be  (thisSmoothRadius + 1 + smoothRadiusOfBiomeOnOtherSide) * 4 .",
            "So if two biomes next to each other have both a smooth radius of 2, the",
            "resulting smooth area will be (2 + 1 + 2) * 4 = 20 blocks wide.");

        writer.putSetting(BiomeStandardValues.CUSTOM_HEIGHT_CONTROL_SMOOTH_RADIUS, this.CHCSmoothRadius,
            "Works the same way as SmoothRadius but only works on CustomHeightControl. Must be between 0 and 32, inclusive.",
            "Does nothing if Custom Height Control smoothing is not enabled in the world config.");

        writer.putSetting(BiomeStandardValues.MAX_AVERAGE_HEIGHT, this.maxAverageHeight,
            "If this value is greater than 0, then it will affect how much, on average, the terrain will rise before leveling off when it begins to increase in elevation.",
            "If the value is less than 0, then it will cause the terrain to either increase to a lower height before leveling out or decrease in height if the value is a large enough negative.");

        writer.putSetting(BiomeStandardValues.MAX_AVERAGE_DEPTH, this.maxAverageDepth,
            "If this value is greater than 0, then it will affect how much, on average, the terrain (usually at the ottom of the ocean) will fall before leveling off when it begins to decrease in elevation. ",
            "If the value is less than 0, then it will cause the terrain to either fall to a lesser depth before leveling out or increase in height if the value is a large enough negative.");

        writer.putSetting(BiomeStandardValues.VOLATILITY_1, this.volatilityRaw1,
            "Another type of noise. This noise is independent from biomes. The larger the values the more chaotic/volatile landscape generation becomes.",
            "Setting the values to negative will have the opposite effect and make landscape generation calmer/gentler.");
        writer.putSetting(BiomeStandardValues.VOLATILITY_2, this.volatilityRaw2);

        writer.putSetting(BiomeStandardValues.VOLATILITY_WEIGHT_1, this.volatilityWeightRaw1,
            "Adjust the weight of the corresponding volatility settings. This allows you to change how prevalent you want either of the volatility settings to be in the terrain.");
        writer.putSetting(BiomeStandardValues.VOLATILITY_WEIGHT_2, this.volatilityWeightRaw2);

        writer.putSetting(BiomeStandardValues.DISABLE_BIOME_HEIGHT, this.disableNotchHeightControl,
            "Disable all noises except Volatility1 and Volatility2. Also disable default block chance from height.");

        writer.putSetting(BiomeStandardValues.CUSTOM_HEIGHT_CONTROL, this.heightMatrix,
            "List of custom height factors, 17 double entries, each controls about 7",
            "blocks height, starting at the bottom of the world. Positive entry - larger chance of spawn blocks, negative - smaller",
            "Values which affect your configuration may be found only experimentally. Values may be very big, like ~3000.0 depends from height",
            "Example:",
            "  CustomHeightControl:0.0,-2500.0,0.0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0",
            "Makes empty layer above bedrock layer. ");

        writer.bigTitle("Rivers",
            "There are two different river systems - the standard one and the improved one.",
            "See the ImprovedRivers settting in the WorldConfig. Both modes have different",
            "river settings, so carefully read the headers to know which settings you can use.",
            "");

        writer.smallTitle("ImprovedRivers:false",
            "Only available when ImprovedRivers is set to false in the WorldConfig.");

        writer.putSetting(BiomeStandardValues.RIVER_BIOME, this.riverBiome,
            "Sets which biome is used as the river biome.");

        writer.smallTitle("ImprovedRivers:true",
            "Only available when ImprovedRivers is set to true in the WorldConfig.",
            "");

        writer.putSetting(BiomeStandardValues.RIVER_HEIGHT, this.riverHeight,
            "Works the same as BiomeHeight (scroll up), but is used where a river is generated in this biome");

        writer.putSetting(BiomeStandardValues.RIVER_VOLATILITY, this.riverVolatility,
            "Works the same as BiomeVolatility (scroll up), but is used where a river is generated in this biome");

        writer.putSetting(BiomeStandardValues.RIVER_WATER_LEVEL, this.configRiverWaterLevel,
            "Works the same as WaterLevelMax (scroll down), but is used where a river is generated in this biome",
            "Can be used to create elevated rivers");

        writer.putSetting(BiomeStandardValues.RIVER_CUSTOM_HEIGHT_CONTROL, this.riverHeightMatrix,
            "Works the same as CustomHeightControl (scroll up), but is used where a river is generated in this biome");

        writer.bigTitle("Blocks");

        writer.putSetting(BiomeStandardValues.STONE_BLOCK, this.stoneBlock,
            "Change this to generate something else than stone in the biome.");

        writer.putSetting(BiomeStandardValues.SURFACE_BLOCK, this.surfaceBlock,
            "Surface block, usually GRASS.");

        writer.putSetting(BiomeStandardValues.GROUND_BLOCK, this.groundBlock,
            "Block from stone to surface, like dirt in most biomes.");

        writer.putSetting(BiomeStandardValues.SURFACE_AND_GROUND_CONTROL, this.surfaceAndGroundControl,
            "Setting for biomes with more complex surface and ground blocks.",
            "Each column in the world has a noise value from what appears to be -7 to 7.",
            "Values near 0 are more common than values near -7 and 7. This setting is",
            "used to change the surface block based on the noise value for the column.",
            "Syntax: SurfaceBlockName,GroundBlockName,MaxNoise,[AnotherSurfaceBlockName,[AnotherGroundBlockName,MaxNoise[,...]]",
            "Example: " + BiomeStandardValues.SURFACE_AND_GROUND_CONTROL + ": STONE,STONE,-0.8,GRAVEL,STONE,0.0,DIRT,DIRT,10.0",
            "  When the noise is below -0.8, stone is the surface and ground block, between -0.8 and 0",
            "  gravel with stone just below and between 0.0 and 10.0 there's only dirt.",
            "  Because 10.0 is higher than the noise can ever get, the normal " + BiomeStandardValues.SURFACE_BLOCK,
            "  and " + BiomeStandardValues.GROUND_BLOCK + " will never appear in this biome.",
            "",
            "Alternatively, you can use Mesa, MesaForest or MesaBryce to get blocks",
            "like the blocks found in the Mesa biomes.");

        writer.putSetting(BiomeStandardValues.REPLACED_BLOCKS, replacedBlocks,
            "Replace Variable: (blockFrom,blockTo[:blockDataTo][,minHeight,maxHeight])",
            "Example :",
            "  ReplacedBlocks: (GRASS,DIRT,100,127),(GRAVEL,GLASS)",
            "Replace grass block to dirt from 100 to 127 height and replace gravel to glass on all height ");

        writer.smallTitle("Water / Lava & Frozen States");

        writer.putSetting(BiomeStandardValues.USE_WORLD_WATER_LEVEL, this.useWorldWaterLevel,
            "Set this to false to use the \"Water / Lava & Frozen States\" settings of this biome.");

        writer.putSetting(BiomeStandardValues.WATER_LEVEL_MAX, this.configWaterLevelMax,
            "Set water level. Every empty between this levels will be fill water or another block from WaterBlock.");
        writer.putSetting(BiomeStandardValues.WATER_LEVEL_MIN, this.configWaterLevelMin);

        writer.putSetting(BiomeStandardValues.WATER_BLOCK, this.configWaterBlock,
            "Block used as water in WaterLevelMax");

        writer.putSetting(BiomeStandardValues.ICE_BLOCK, this.configIceBlock,
            "Block used as ice. Ice only spawns if the BiomeTemperture is low enough.");

        writer.putSetting(WorldStandardValues.COOLED_LAVA_BLOCK, this.cooledLavaBlock,
            "Block used as cooled or frozen lava.",
            "Set this to OBSIDIAN for \"frozen\" lava lakes in cold biomes");

        writer.bigTitle("Visuals and weather",
            "Most of the settings here only have an effect on players with the client version of Open Terrain Generator installed.");

        writer.putSetting(BiomeStandardValues.BIOME_TEMPERATURE, this.biomeTemperature,
            "Biome temperature. Float value from 0.0 to 2.0.",
            "When this value is around 0.2, snow will fall on mountain peaks above y=90.",
            "When this value is around 0.1, the whole biome will be covered in snow and ice.",
            "However, on default biomes, this won't do anything except changing the grass and leaves colors slightly.");

        writer.putSetting(BiomeStandardValues.BIOME_WETNESS, this.biomeWetness,
            "Biome wetness. Float value from 0.0 to 1.0.",
            "If this biome is a custom biome, and this value is set to 0, no rain will fall.",
            "On default biomes, this won't do anything except changing the grass and leaves colors slightly.");

        writer.putSetting(BiomeStandardValues.SKY_COLOR, this.skyColor,
            "Biome sky color.");

        writer.putSetting(BiomeStandardValues.WATER_COLOR, this.waterColor,
            "Biome water color multiplier.");
        
        writer.putSetting(BiomeStandardValues.GRASS_COLOR, this.grassColor,
            "Biome grass color.");

        writer.putSetting(BiomeStandardValues.GRASS_COLOR_2, this.grassColor2,
            "Biome grass color 2, used to create a gradient like vanilla swamps," +
            "only works when " + BiomeStandardValues.GRASS_COLOR_IS_MULTIPLIER.getName() + " is set to false." +
            "Forge only atm.");
        
        writer.putSetting(BiomeStandardValues.GRASS_COLOR_IS_MULTIPLIER, this.grassColorIsMultiplier,
            "Whether the grass color is a multiplier.",
            "If you set it to true, the color will be based on this value, the BiomeTemperature and the BiomeWetness.",
            "If you set it to false, the grass color will be just this color.");

        writer.putSetting(BiomeStandardValues.FOLIAGE_COLOR, this.foliageColor,
            "Biome foliage color.");
        
        writer.putSetting(BiomeStandardValues.FOLIAGE_COLOR_2, this.foliageColor2,
            "Biome foliage color 2, used to create a gradient like vanilla swamp grass," +
            "only works when " + BiomeStandardValues.FOLIAGE_COLOR_IS_MULTIPLIER.getName() + " is set to false." +
            "Forge only atm.");
             
        writer.putSetting(BiomeStandardValues.FOLIAGE_COLOR_IS_MULTIPLIER, this.foliageColorIsMultiplier,
            "Whether the foliage color is a multiplier. See GrassColorIsMultiplier for details.");
        
        // Fog
        writer.putSetting(BiomeStandardValues.FOG_COLOR, this.fogColor, "Biome fog color.");

        writer.putSetting(BiomeStandardValues.FOG_DENSITY, this.fogDensity,
            "How dense the fog is this biome is, Float value from 0.0 to 1.0.",
            "A value of 0 produces almost no fog while a value of 1 will cover the entire screen with fog.");

        writer.putSetting(BiomeStandardValues.FOG_TIME_WEIGHT, this.fogTimeWeight,
    		"How much the world time should affect the fog color, Float value from 0.0 to 1.0.",
            "A value of 0.0 means the fog will stay the same color all day.",
            "A value of 1.0 will make the fog turn completely black at midnight.");

        writer.putSetting(BiomeStandardValues.FOG_RAIN_WEIGHT, this.fogRainWeight,
            "How much rain should affect the fog color, Float value from 0.0 to 1.0.",
            "A value of 0.0 means the fog will stay the same color in the rain.",
            "A value of 1.0 will make the fog turn completely black during rain.");

        writer.putSetting(BiomeStandardValues.FOG_THUNDER_WEIGHT, this.fogThunderWeight,
            "How much thunderstorms should affect the fog color, Float value from 0.0 to 1.0.",
            "A value of 0.0 means the fog will stay the same color during thunderstorms.",
            "A value of 1.0 will make the fog turn completely black during thunderstorms.");

        writer.bigTitle("Resource queue",
            "This section control all resources spawning after terrain generation.",
            "The resources will be placed in this order.",
            "",
            "Keep in mind that a high size, frequency or rarity might slow down terrain generation.",
            "",
            "Possible resources:",
            "DoResourceInheritance(true|false)",
            "SmallLake(BlockName,Frequency,Rarity,MinAltitude,MaxAltitude)",
            "Dungeon(Frequency,Rarity,MinAltitude,MaxAltitude)",
            "UnderGroundLake(MinSize,MaxSize,Frequency,Rarity,MinAltitude,MaxAltitude)",
            "Ore(BlockName,Size,Frequency,Rarity,MinAltitude,MaxAltitude,BlockSource[,BlockSource2,BlockSource3.....])",
            "UnderWaterOre(BlockName,Size,Frequency,Rarity,BlockSource[,BlockSource2,BlockSource3.....])",
            "CustomObject(Object[,AnotherObject[,...]])",
            "CustomStructure([Object,Object_Chance[,AnotherObject,Object_Chance[,...]]])",
            "SurfacePatch(BlockName,DecorationBlockName,MinAltitude,MaxAltitude,BlockSource[,BlockSource2,BlockSource3.....]",
            "Tree(Frequency,TreeType,TreeTypeChance[,AdditionalTreeType,AdditionalTreeTypeChance.....])",
            "Plant(PlantType,Frequency,Rarity,MinAltitude,MaxAltitude,BlockSource[,BlockSource2,BlockSource3.....])",
            "UnderWaterPlant(PlantType,Frequency,Rarity,MinAltitude,MaxAltitude,BlockSource[,BlockSource2,BlockSource3.....])",
            "Grass(PlantType,Grouped/NotGrouped,Frequency,Rarity,BlockSource[,BlockSource2,BlockSource3.....])",
            "Reed(BlockName,Frequency,Rarity,MinAltitude,MaxAltitude,BlockSource[,BlockSource2,BlockSource3.....])",
            "Cactus(BlockName,Frequency,Rarity,MinAltitude,MaxAltitude,BlockSource[,BlockSource2,BlockSource3.....])",
            "Liquid(BlockName,Frequency,Rarity,MinAltitude,MaxAltitude,BlockSource[,BlockSource2,BlockSource3.....])",
            "AboveWaterRes(BlockName,Frequency,Rarity)",
            "Vines(Frequency,Rarity,MinAltitude,MaxAltitude)",
            "Vein(BlockName,MinRadius,MaxRadius,Rarity,OreSize,OreFrequency,OreRarity,MinAltitude,MaxAltitude,BlockSource[,BlockSource2,..])",
            "Well(BaseBlockName,HalfSlabBlockName,WaterBlockName,Frequency,Rarity,MinAltitude,MaxAltitude,BlockSource[,BlockSource2,..])",
            "Boulder(BlockName,Frequency,Rarity,MinAltitude,MaxAltitude,BlockSource[,BlockSource2,..]",
            "IceSpike(BlockName,IceSpikeType,Frequency,Rarity,MinAltitude,MaxAltitude,Blocksource[,BlockSource2,...])",
            "",
            "BlockName:      must be the name of a block. May include block data, like \"WOOL:1\".",
            "BlockSource:    list of blocks the resource can spawn on/in. You can also use \"Solid\" or \"All\".",
            "Frequency:      number of attempts to place this resource in each chunk.",
            "Rarity:         chance for each attempt, Rarity:100 - mean 100% to pass, Rarity:1 - mean 1% to pass.",
            "MinAltitude and MaxAltitude: height limits.",
            "BlockSource:    mean where or whereupon resource will be placed ",
            "TreeType:       Tree (original oak tree) - BigTree - Birch - TallBirch - SwampTree -",
            "                HugeMushroom (randomly red or brown) - HugeRedMushroom - HugeBrownMushroom -",
            "                Taiga1 - Taiga2 - HugeTaiga1 - HugeTaiga2 -",
            "                JungleTree (the huge jungle tree) - GroundBush - CocoaTree (smaller jungle tree)",
            "                DarkOak (from the roofed forest biome) - Acacia",
            "                You can also use your own custom objects, as long as they have set Tree to true in their settings.",
            "TreeTypeChance: similar to Rarity. Example:",
            "                Tree(10,Taiga1,35,Taiga2,100) - plugin tries 10 times, for each attempt it tries to place Taiga1 (35% chance),",
            "                if that fails, it attempts to place Taiga2 (100% chance).",
            "PlantType:      one of the plant types: " + StringHelper.join(PlantType.values(), ", "),
            "                or simply a BlockName",
            "IceSpikeType:   one of the ice spike types: " + StringHelper.join(IceSpikeGen.SpikeType.values(), ","),
            "Object:         can be a any kind of custom object (bo2 or bo3) but without the file extension. You can",
            "                also use UseWorld to spawn one of the object in the WorldObjects folder and UseBiome to spawn",
            "                one of the objects in the BiomeObjects setting. When using BO2s for UseWorld, the BO2 must have",
            "                this biome in their spawnInBiome setting.",
            "",
            "Plant and Grass resource: both a resource of one block. Plant can place blocks underground, Grass cannot.",
            "UnderWaterPlant resource: a resource of one block, places blocks only in water.",
            "Liquid resource: a one-block water or lava source",
            "SmallLake and UnderGroundLake resources: small lakes of about 8x8 blocks",
            "Vein resource: not in vanilla. Starts an area where ores will spawn. Can be slow, so use a low Rarity (smaller than 1).",
            "CustomStructure resource: starts a BO3 structure in the chunk.",
            "");
        writer.addConfigFunctions(this.resourceSequence);

        writer.bigTitle("Sapling resource",
            PluginStandardValues.PLUGIN_NAME + " allows you to grow your custom objects from saplings, instead",
            "of the vanilla trees. Add one or more Sapling functions here to override vanilla",
            "spawning for that sapling.",
            "",
            "The syntax is: Sapling(SaplingType,TreeType,TreeType_Chance[,Additional_TreeType,Additional_TreeType_Chance.....])",
            "Works like Tree resource instead first parameter.",
            "For custom saplings (Forge only); Sapling(Custom,SaplingMaterial,WideTrunk,TreeType,TreeType_Chance.....)",
            "SaplingMaterial is a material name from a mod.",
            "WideTrunk is 'true' or 'false', whether or not it requires 4 saplings.",
            "",
            "Sapling types: " + StringHelper.join(SaplingType.values(), ", "),
            "All - will make the tree spawn from all saplings, but not from mushrooms.",
            "BigJungle - for when 4 jungle saplings grow at once.",
            "RedMushroom/BrownMushroom - will only grow when bonemeal is used.",
            "",
            "",
            "");        
        
        writer.addConfigFunctions(this.saplingGrowers.values());
        writer.addConfigFunctions(this.customSaplingGrowers.values());
        writer.addConfigFunctions(this.customBigSaplingGrowers.values());
        
        writer.putSetting(BiomeStandardValues.INHERIT_SAPLING_RESOURCE, this.inheritSaplingResource,
            "For virtual (replaceToBiomeName) biomes: Inherit all Sapling() resources from the",
            "replaceToBiomeName biome. If a Sapling() with the same SaplingType is defined ",
            "in this config and the parent config, the one from this config is used.");

        writer.bigTitle("Custom objects");

        this.writeCustomObjects(writer);

        writer.bigTitle("Structures",
            "Here you can change, enable or disable the stuctures.",
            "If you have disabled the structure in the WorldConfig, it won't spawn,",
            "regardless of these settings.");

        writer.putSetting(BiomeStandardValues.STRONGHOLDS_ENABLED, strongholdsEnabled,
            "Disables strongholds for this biome. If there is no suitable biome nearby,",
            "Minecraft will ignore this setting.");

        writer.putSetting(BiomeStandardValues.WOODLAND_MANSIONS_ENABLED, woodLandMansionsEnabled,
    		"Whether a Woodland Mansion can be placed in this biome.");

        writer.putSetting(BiomeStandardValues.OCEAN_MONUMENTS_ENABLED, oceanMonumentsEnabled,
            "Whether an Ocean Monument can be placed in this biome.");

        writer.putSetting(BiomeStandardValues.NETHER_FORTRESSES_ENABLED, netherFortressesEnabled,
            "Whether a Nether Fortress can start in this biome. Might extend to neighbor biomes.");

        writer.putSetting(BiomeStandardValues.VILLAGE_TYPE, villageType,
            "The village type in this biome. Can be wood, sandstone, taiga, savanna or disabled.");

        writer.putSetting(BiomeStandardValues.MINESHAFT_TYPE, mineshaftType,
            "The mineshaft type in this biome. Can be normal, mesa or disabled.");

        writer.putSetting(BiomeStandardValues.MINESHAFT_RARITY, mineshaftsRarity,
            "The mineshaft rarity from 0 to 100. 0 = no mineshafts, 1 = default rarity, 100 = a wooden chaos.",
            "Note that mineshafts will never spawn, regardless of this setting, if ",
            BiomeStandardValues.MINESHAFT_TYPE + " was set to " + MineshaftType.disabled);

        writer.putSetting(BiomeStandardValues.RARE_BUILDING_TYPE, rareBuildingType,
            "The type of the aboveground rare building in this biome. Can be desertPyramid, jungleTemple, swampHut, igloo or disabled.");

        writer.bigTitle("Mob spawning",
            "This is where you configure mob spawning. Mobs spawn in groups,",
            "see http://minecraft.gamepedia.com/Spawn#Mob_spawning",
            "",
            "A mobgroups is made of four parts. They are mob, weight, min and max.",
            "The mob is one of the Minecraft internal mob names.",
            "See http://minecraft.gamepedia.com/Chunk_format#Mobs",
            "The weight is used for a random selection. This is a positive integer.",
            "The min is the minimum amount of mobs spawning as a group. This is a positive integer.",
            "The max is the maximum amount of mobs spawning as a group. This is a positive integer.",
            "",
            "Mob groups are written to the config files in Json.",
            "Json is a tree document format: http://en.wikipedia.org/wiki/JSON",
            "Write a mobgroup like this: {\"mob\": \"mobname\", \"weight\": integer, \"min\": integer, \"max\": integer}",
            "For example: {\"mob\": \"Ocelot\", \"weight\": 10, \"min\": 2, \"max\": 6}",
            "For example: {\"mob\": \"MushroomCow\", \"weight\": 5, \"min\": 2, \"max\": 2}",
            "A json list of mobgroups looks like this: [mobgroup, mobgroup, mobgroup...]",
            "This would be an ampty list: []",
            "You can validate your json here: http://jsonlint.com/",
            "",
            "There are four categories of mobs: monsters, creatures, water creatures and ambient creatures.",
            "You can add your own mobgroups in the lists below." + 
            "For Spigot virtual biomes (that use ReplaceToBiomeName, f.e. the default biomes), only the creatures list works."
        );

        writer.putSetting(BiomeStandardValues.SPAWN_MONSTERS, this.spawnMonsters,
            "The monsters (skeletons, zombies, etc.) that spawn in this biome",
            "For instance [{\"mob\": \"Spider\", \"weight\": 100, \"min\": 4, \"max\": 4}, {\"mob\": \"Zombie\", \"weight\": 100, \"min\": 4, \"max\": 4}]",
            "Use the \"/otg entities\" console command to get a list of possible mobs and mob types.",
            "Use the \"/otg biome -m\" console command to get the list of registered mobs for a biome.",
            "For Spigot virtual biomes (that use ReplaceToBiomeName, f.e. the default biomes) this list doesn't work."
        );

        writer.putSetting(BiomeStandardValues.SPAWN_CREATURES, this.spawnCreatures,
            "The friendly creatures (cows, pigs, etc.) that spawn in this biome",
            "For instance [{\"mob\": \"Sheep\", \"weight\": 12, \"min\": 4, \"max\": 4}, {\"mob\": \"Pig\", \"weight\": 10, \"min\": 4, \"max\": 4}]",
            "Use the \"/otg entities\" console command to get a list of possible mobs and mob types.",
            "Use the \"/otg biome -m\" console command to get the list of registered mobs for a biome."
        );

        writer.putSetting(BiomeStandardValues.SPAWN_WATER_CREATURES, this.spawnWaterCreatures,
            "The water creatures (only squids in vanilla) that spawn in this biome",
            "For instance [{\"mob\": \"Squid\", \"weight\": 10, \"min\": 4, \"max\": 4}]",
            "Use the \"/otg entities\" console command to get a list of possible mobs and mob types.",
            "Use the \"/otg biome -m\" console command to get the list of registered mobs for a biome.",
            "For Spigot virtual biomes (that use ReplaceToBiomeName, f.e. the default biomes) this list doesn't work."
        );

        writer.putSetting(BiomeStandardValues.SPAWN_AMBIENT_CREATURES, this.spawnAmbientCreatures,
            "The ambient creatures (only bats in vanila) that spawn in this biome",
            "For instance [{\"mob\": \"Bat\", \"weight\": 10, \"min\": 8, \"max\": 8}]",
            "Use the \"/otg entities\" console command to get a list of possible mobs and mob types.",
            "Use the \"/otg biome -m\" console command to get the list of registered mobs for a biome.",
            "*Some mob spawning lists may only work for custom biomes (that don't use replacetobiomename).",
            "For Spigot virtual biomes (that use ReplaceToBiomeName, f.e. the default biomes) this list doesn't work."
        );

        // PG Settings

        writer.putSetting(BiomeStandardValues.BIOME_DICT_ID, this.biomeDictId,
	        "Forge Biome Dictionary ID used by other mods to identify a biome and place",
	        "modded blocks, items and mobs in it.",
	        "This will only work for modded items/blocks/mobs that are placed in biomes",
	        "while chunks are being generated. Most mods that add mods add their mobs to",
	        "biomes' internal mob list when MC starts and let MC's mob spawning mechanics",
	        "handle the actual spawning. This means that when TC creates new biomes",
	        "when it generates a world other mods do not add their mobs to those biomes.",
	        "This can be solved by using the InheritMobsBiomeName setting to inherit a",
	        "a mob list from a vanilla biome.",
	        "NOTE: Only works for biomes with id's under < 255 (non-virtual biomes).",
	        "For virtual biomes the BiomeDictId is inherited via ReplaceToBiomeName."
		);

        writer.putSetting(BiomeStandardValues.INHERIT_MOBS_BIOME_NAME, inheritMobsBiomeName,
		    "Inherit the internal mobs list of another biome. Inherited mobs can be ",
		    "overridden using the SpawnMonsters, SpawnCreatures, SpawnWaterCreatures",
		    "and SpawnAmbientCreatures settings. Any mob type defined using those settings",
		    "will override inherited mob settings for the same mob.",
		    "Use this setting to inherit modded mobs from vanilla biomes (see also: BiomeDictId)"
		);

        // /PG
    }

    private void writeCustomObjects(SettingsMap writer)
    {
        List<String> objectStrings = new ArrayList<String>(biomeObjectStrings.size());
        for (String objectString : biomeObjectStrings)
        {
            objectStrings.add(objectString);
        }
        writer.putSetting(BiomeStandardValues.BIOME_OBJECTS, objectStrings,
                "These objects will spawn when using the UseBiome keyword.");
    }

    @Override
    protected void correctSettings(boolean logWarnings)
    {
        this.biomeExtends = (this.biomeExtends == null || this.biomeExtends.equals("null")) ? "" : this.biomeExtends;
        this.biomeSize = lowerThanOrEqualTo(biomeSize, worldConfig.generationDepth);
        this.biomeSizeWhenIsle = lowerThanOrEqualTo(biomeSizeWhenIsle, worldConfig.generationDepth);
        this.biomeSizeWhenBorder = lowerThanOrEqualTo(biomeSizeWhenBorder, worldConfig.generationDepth);
        this.biomeRarity = lowerThanOrEqualTo(biomeRarity, worldConfig.biomeRarityScale);
        this.biomeRarityWhenIsle = lowerThanOrEqualTo(biomeRarityWhenIsle, worldConfig.biomeRarityScale);

        this.isleInBiome = filterBiomes(this.isleInBiome, this.worldConfig.worldBiomes);
        this.biomeIsBorder = filterBiomes(this.biomeIsBorder, this.worldConfig.worldBiomes);
        this.notBorderNear = filterBiomes(this.notBorderNear, this.worldConfig.worldBiomes);

        this.volatility1 = this.volatilityRaw1 < 0.0D ? 1.0D / (Math.abs(this.volatilityRaw1) + 1.0D) : this.volatilityRaw1 + 1.0D;
        this.volatility2 = this.volatilityRaw2 < 0.0D ? 1.0D / (Math.abs(this.volatilityRaw2) + 1.0D) : this.volatilityRaw2 + 1.0D;

        this.volatilityWeight1 = (this.volatilityWeightRaw1 - 0.5D) * 24.0D;
        this.volatilityWeight2 = (0.5D - this.volatilityWeightRaw2) * 24.0D;

        this.waterLevelMax = higherThanOrEqualTo(waterLevelMax, this.waterLevelMin);

        // Update configs for worlds with no saved biome id data (OTG 1.12.2 v7, dynamic biome ids update)
    	// Update biomes for legacy worlds, default biomes should be referred to as minecraft:<biomename>
    	if(
			this.replaceToBiomeName != null && 
			this.replaceToBiomeName.trim().length() > 0	        			
		)
    	{
    		String defaultBiomeResourceLocation = BiomeRegistryNames.getRegistryNameForDefaultBiome(this.replaceToBiomeName);
    		if(defaultBiomeResourceLocation != null)
    		{
    			this.replaceToBiomeName = defaultBiomeResourceLocation;
    		}
    	} else {
    		// Default biomes must replacetobiomename themselves
    		String defaultBiomeResourceLocation = BiomeRegistryNames.getRegistryNameForDefaultBiome(this.getName());
    		if(defaultBiomeResourceLocation != null)
    		{
    			this.replaceToBiomeName = defaultBiomeResourceLocation;
    		}
    	}
    }

    @Override
    protected void renameOldSettings(SettingsMap settings)
    {
        // disableNotchPonds
        if (settings.hasSetting(BiomeStandardValues.DISABLE_NOTCH_PONDS))
        {
            // Found disableNotchPonds, so add SmallLake resource if it wasn't
            // set to true
            if (!settings.getSetting(BiomeStandardValues.DISABLE_NOTCH_PONDS, false))
            {
                settings.addConfigFunctions(Arrays.<ConfigFunction<?>> asList(
                        Resource.createResource(this, SmallLakeGen.class,
                                LocalMaterials.WATER, 4, 7, 8,
                                worldConfig.worldHeightCap),
                        Resource.createResource(this, SmallLakeGen.class,
                        		LocalMaterials.LAVA, 2, 3, 8,
                                worldConfig.worldHeightCap - 8)));
            }
        }

        // FrozenRivers
        if (!settings.getSetting(WorldStandardValues.FROZEN_RIVERS))
        {
            // User had disabled frozen rivers in the old WorldConfig
            // So ignore the default value of RiverBiome
            settings.putSetting(BiomeStandardValues.RIVER_BIOME, "River");
        }

        // BiomeRivers
        if (!settings.getSetting(BiomeStandardValues.BIOME_RIVERS))
        {
            // If the rivers were disabled using the old setting, disable them
            // also using the new setting
            // (Overrides FrozenRivers: false)
            settings.putSetting(BiomeStandardValues.RIVER_BIOME, "");
        }

        // ReplacedBlocks in format fromId=toId.data(minHeight-maxHeight)
        String replacedBlocksValue = settings.getSetting(BiomeStandardValues.REPLACED_BLOCKS_OLD);

        if (replacedBlocksValue.contains("="))
        {
            String[] values = replacedBlocksValue.split(",");
            List<ReplacedBlocksInstruction> output = new ArrayList<ReplacedBlocksInstruction>();

            for (String replacedBlock : values)
            {
                try
                {
                    LocalMaterialData fromId = OTG.getEngine().readMaterial(replacedBlock.split("=")[0]);
                    String rest = replacedBlock.split("=")[1];
                    LocalMaterialData to;
                    int minHeight = 0;
                    int maxHeight = worldConfig.worldHeightCap;

                    int start = rest.indexOf('(');
                    int end = rest.indexOf(')');
                    if (start != -1 && end != -1)
                    {   // Found height settings
                        String[] ranges = rest.substring(start + 1, end).split("-");
                        to = OTG.getEngine().readMaterial(rest.substring(0, start));
                        minHeight = StringHelper.readInt(ranges[0], minHeight, maxHeight);
                        maxHeight = StringHelper.readInt(ranges[1], minHeight, maxHeight);
                    } else {
                    	// No height settings
                        to = OTG.getEngine().readMaterial(rest);
                    }

                    output.add(new ReplacedBlocksInstruction(fromId, to, minHeight, maxHeight));
                } catch (InvalidConfigException ignored)
                {
                }
            }

            ReplacedBlocksMatrix replacedBlocks = ReplacedBlocksMatrix.createEmptyMatrix(worldConfig.worldHeightCap);
            replacedBlocks.setInstructions(output);
            settings.putSetting(BiomeStandardValues.REPLACED_BLOCKS, replacedBlocks);
        }

        // SpawnMobsAddDefaults: add default values to list if old boolean was
        // set to true
        if (settings.getSetting(BiomeStandardValues.SPAWN_MONSTERS_ADD_DEFAULTS, false))
        {
            addDefaultMobGroups(settings, BiomeStandardValues.SPAWN_MONSTERS, defaultSettings.defaultMonsters);
        }
        if (settings.getSetting(BiomeStandardValues.SPAWN_CREATURES_ADD_DEFAULTS, false))
        {
            addDefaultMobGroups(settings, BiomeStandardValues.SPAWN_CREATURES, defaultSettings.defaultCreatures);
        }
        if (settings.getSetting(BiomeStandardValues.SPAWN_WATER_CREATURES_ADD_DEFAULTS, false))
        {
            addDefaultMobGroups(settings, BiomeStandardValues.SPAWN_WATER_CREATURES, defaultSettings.defaultWaterCreatures);
        }
        if (settings.getSetting(BiomeStandardValues.SPAWN_AMBIENT_CREATURES_ADD_DEFAULTS, false))
        {
            addDefaultMobGroups(settings, BiomeStandardValues.SPAWN_AMBIENT_CREATURES, defaultSettings.defaultAmbientCreatures);
        }

        // *WhenBorder, *WhenIsle
        // Used to be shared with the base setting
        if (!settings.isNewConfig())
        {
            if (!settings.hasSetting(BiomeStandardValues.BIOME_SIZE_WHEN_ISLE))
            {
                settings.putSetting(BiomeStandardValues.BIOME_SIZE_WHEN_ISLE,
                        settings.getSetting(BiomeStandardValues.BIOME_SIZE));
            }
            if (!settings.hasSetting(BiomeStandardValues.BIOME_SIZE_WHEN_BORDER))
            {
                settings.putSetting(BiomeStandardValues.BIOME_SIZE_WHEN_BORDER,
                        settings.getSetting(BiomeStandardValues.BIOME_SIZE));
            }
            if (!settings.hasSetting(BiomeStandardValues.BIOME_RARITY_WHEN_ISLE))
            {
                settings.putSetting(BiomeStandardValues.BIOME_RARITY_WHEN_ISLE,
                        settings.getSetting(BiomeStandardValues.BIOME_RARITY));
            }
        }
    }

    private void addDefaultMobGroups(SettingsMap settings, Setting<List<WeightedMobSpawnGroup>> mobSetting, List<WeightedMobSpawnGroup> defaultGroups)
    {
        List<WeightedMobSpawnGroup> emptyList = Collections.emptyList();
        List<WeightedMobSpawnGroup> groups = new ArrayList<WeightedMobSpawnGroup>();
        groups.addAll(defaultGroups);
        groups.addAll(settings.getSetting(mobSetting, emptyList));
        settings.putSetting(mobSetting, groups);
    }

    // See ClientConfigProvider for reading
    public void writeToStream(DataOutput stream, boolean isSinglePlayer) throws IOException
    {
        StreamHelper.writeStringToStream(stream, getName());

        stream.writeFloat(this.biomeTemperature);
        stream.writeFloat(this.biomeWetness);
        stream.writeInt(this.fogColor);
        stream.writeFloat(this.fogDensity);
        stream.writeFloat(this.fogRainWeight);
        stream.writeFloat(this.fogThunderWeight);
        stream.writeFloat(this.fogTimeWeight);        
        stream.writeInt(this.skyColor);
        stream.writeInt(this.waterColor);
        stream.writeInt(this.grassColor);
        stream.writeInt(this.grassColor2);
        stream.writeBoolean(this.grassColorIsMultiplier);
        stream.writeInt(this.foliageColor);
        stream.writeInt(this.foliageColor2);
        stream.writeBoolean(this.foliageColorIsMultiplier);

        StreamHelper.writeStringToStream(stream, this.replaceToBiomeName);        
        StreamHelper.writeStringToStream(stream, this.biomeDictId);
    }
}
