package com.pg85.otg.forge.world;

import net.minecraft.client.Minecraft;
import java.io.File;

import com.pg85.otg.configuration.standard.PluginStandardValues;

/**
 * Responsible for loading and unloading the world.
 *
 */
public final class WorldLoader
{	
	private final File configsDir;

    public WorldLoader()
    {
        File dataFolder = new File(Minecraft.getInstance().gameDir, "mods" + File.separator + PluginStandardValues.PLUGIN_NAME);
        this.configsDir = dataFolder;
    }
    
    public File getConfigsFolder()
    {
        return this.configsDir;
    }
    
    public void createDefaultOTGWorld(String worldName)
    {
    	/*
		// Create default OTG world

    	File worldDir = new File(OTG.getEngine().getWorldsDirectory() + File.separator + worldName);
        List<File> worldDirs = new ArrayList<File>(2);
        worldDirs.add(new File(worldDir, WorldStandardValues.WORLD_BIOMES_DIRECTORY_NAME));
        worldDirs.add(new File(worldDir, WorldStandardValues.WORLD_OBJECTS_DIRECTORY_NAME));
        FileHelper.makeFolders(worldDirs);		
		
		// World config
		DefaulWorldData defaultWorldData = WorldConfig.createDefaultOTGWorldConfig(worldDir, worldName);
		SettingsMap settingsMap = defaultWorldData.settingsMap;
		WorldConfig defaultWorldConfig = defaultWorldData.worldConfig;			
    	FileSettingsWriter.writeToFile(settingsMap, new File(worldDir, WorldStandardValues.WORLD_CONFIG_FILE_NAME), WorldConfig.ConfigMode.WriteAll);
    	
    	// Biome configs
        // Build a set of all biomes to load
        Collection<BiomeLoadInstruction> biomesToLoad = new HashSet<BiomeLoadInstruction>();

        // Loop through all default biomes and create the default
        // settings for them
        List<BiomeLoadInstruction> defaultBiomes = new ArrayList<BiomeLoadInstruction>();
        for (DefaultBiome defaultBiome : DefaultBiome.values())
        {
            int id = defaultBiome.Id;
            BiomeLoadInstruction instruction = defaultBiome.getLoadInstructions(ForgeMojangSettings.fromId(id), ForgeWorld.STANDARD_WORLD_HEIGHT);
            defaultBiomes.add(instruction);
        }
        
        // If we're creating a new world with new configs then add the default biomes
        for (BiomeLoadInstruction defaultBiome : defaultBiomes)
        {
    		biomesToLoad.add(new BiomeLoadInstruction(defaultBiome.getBiomeName(), defaultBiome.getBiomeTemplate()));
        }
        
        List<File> biomeDirs = new ArrayList<File>(1);
        biomeDirs.add(new File(worldDir, WorldStandardValues.WORLD_BIOMES_DIRECTORY_NAME));
        
        // Load all files
        BiomeConfigFinder biomeConfigFinder = new BiomeConfigFinder(OTG.getPluginConfig().biomeConfigExtension);
        Map<String, BiomeConfigStub> biomeConfigStubs = biomeConfigFinder.findBiomes(defaultWorldConfig, defaultWorldConfig.worldHeightScale, biomeDirs, biomesToLoad);
        
        // Write all biomes

        for (BiomeConfigStub biomeConfigStub : biomeConfigStubs.values())
        {
            // Settings reading
            BiomeConfig biomeConfig = new BiomeConfig(biomeConfigStub.getLoadInstructions(), biomeConfigStub, biomeConfigStub.getSettings(), defaultWorldConfig);

            // Settings writing
            File writeFile = biomeConfigStub.getFile();
            if (!biomeConfig.biomeExtends.isEmpty())
            {
                writeFile = new File(writeFile.getAbsolutePath() + ".inherited");
            }
            FileSettingsWriter.writeToFile(biomeConfig.getSettingsAsMap(), writeFile, defaultWorldConfig.settingsMode);
        }
        */
    }
}
