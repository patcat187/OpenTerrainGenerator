package com.pg85.otg.forge;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import com.pg85.otg.OTGEngine;
import com.pg85.otg.common.LocalMaterialData;
import com.pg85.otg.common.LocalWorld;
import com.pg85.otg.configuration.biome.BiomeConfigFinder.BiomeConfigStub;
import com.pg85.otg.configuration.biome.BiomeLoadInstruction;
import com.pg85.otg.configuration.standard.DefaultMaterial;
import com.pg85.otg.configuration.standard.PluginStandardValues;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.forge.util.ForgeLogger;
import com.pg85.otg.forge.world.WorldLoader;

public class ForgeEngine extends OTGEngine
{
	//public static LinkedHashMap<String, DimensionConfigGui> Presets = new LinkedHashMap<String, DimensionConfigGui>();
	//private ForgeBiomeRegistryManager biomeRegistryManager;
	private WorldLoader worldLoader;
	
    public ForgeEngine()
    {
        super(new ForgeLogger());
        
        this.worldLoader = new WorldLoader();      
        //this.biomeRegistryManager = new ForgeBiomeRegistryManager();
    }

    // Folders
    
	@Override
	public File getOTGRootFolder()
	{
		return this.worldLoader.getConfigsFolder();
	}

	@Override
	public File getGlobalObjectsDirectory()
	{
        return new File(this.getOTGRootFolder(), PluginStandardValues.BO_DirectoryName);
	}

	@Override
	public File getWorldsDirectory()
	{
        return new File(this.getOTGRootFolder(), PluginStandardValues.PresetsDirectoryName);
	}

	//
	
	@Override
	public LocalWorld getWorld(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalWorld getUnloadedWorld(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<LocalWorld> getAllWorlds()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalMaterialData readMaterial(String name) throws InvalidConfigException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalMaterialData toLocalMaterialData(DefaultMaterial defaultMaterial, int blockData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isModLoaded(String mod)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean areEnoughBiomeIdsAvailableForPresets(ArrayList<String> presetNames)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<BiomeLoadInstruction> getDefaultBiomes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mergeVanillaBiomeMobSpawnSettings(BiomeConfigStub biomeConfigStub, String biomeResourceLocation)
	{
		// TODO Auto-generated method stub		
	}
}
