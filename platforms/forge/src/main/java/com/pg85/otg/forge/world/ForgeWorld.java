package com.pg85.otg.forge.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import com.pg85.otg.OTG;
import com.pg85.otg.forge.generator.OTGChunkGenerator;
import com.pg85.otg.logging.LogMarker;
import com.pg85.otg.network.ConfigProvider;
import com.pg85.otg.terraingen.ChunkBuffer;
import com.pg85.otg.terraingen.ObjectSpawner;
import com.pg85.otg.terraingen.biome.BiomeGenerator;
import com.pg85.otg.util.BiomeIds;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.bo3.NamedBinaryTag;
import com.pg85.otg.common.LocalBiome;
import com.pg85.otg.common.LocalMaterialData;
import com.pg85.otg.common.LocalWorld;
import com.pg85.otg.common.WorldSession;
import com.pg85.otg.configuration.biome.BiomeConfig;
import com.pg85.otg.configuration.standard.TreeType;
import com.pg85.otg.customobjects.SpawnableObject;
import com.pg85.otg.customobjects.bofunctions.EntityFunction;
import com.pg85.otg.customobjects.structures.CustomStructureCache;
import com.pg85.otg.exception.BiomeNotFoundException;

import net.minecraft.world.World;

public class ForgeWorld implements LocalWorld
{
    public static final int STANDARD_WORLD_HEIGHT = 128; // TODO: Why is this 128, should be 255?
	
    private OTGChunkGenerator generator;
    public World world;
    private String name;
    private long seed;

    public ForgeWorld(String _name)
    {
		OTG.log(LogMarker.INFO, "Creating world \"" + _name + "\"");
        this.name = _name;
    }
       
    public World getWorld()
    {
        return this.world;
    }
    
    public OTGChunkGenerator getChunkGenerator()
    {
        return this.generator;
    }

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWorldSettingsName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDimensionId()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getSeed()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public File getWorldSaveDir()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigProvider getConfigs()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectSpawner getObjectSpawner()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CustomStructureCache getStructureCache()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorldSession getWorldSession()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteWorldSessionData()
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public int getHeightCap()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeightScale()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BiomeGenerator getBiomeGenerator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalBiome createBiomeFor(BiomeConfig biomeConfig, BiomeIds biomeIds, ConfigProvider configProvider,
			boolean isReload)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxBiomesCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxSavedBiomesCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<LocalBiome> getAllBiomes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalBiome getBiomeByOTGIdOrNull(int id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalBiome getFirstBiomeOrNull()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalBiome getBiomeByNameOrNull(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalBiome getBiome(int x, int z) throws BiomeNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSavedBiomeName(int x, int z)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalBiome getCalculatedBiome(int x, int z)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRegisteredBiomeId(String resourceLocation)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void prepareDefaultStructures(int chunkX, int chunkZ, boolean dry)
	{
		// TODO Auto-generated method stub		
	}

	@Override
	public boolean placeDungeon(Random rand, int x, int y, int z)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean placeFossil(Random rand, ChunkCoordinate chunkCoord)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean placeTree(TreeType type, Random rand, int x, int y, int z)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean placeDefaultStructures(Random rand, ChunkCoordinate chunkCoord)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SpawnableObject getMojangStructurePart(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean chunkHasDefaultStructure(Random rand, ChunkCoordinate chunk)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void placePopulationMobs(LocalBiome biome, Random random, ChunkCoordinate chunkCoord)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void spawnEntity(EntityFunction<?> entityData, ChunkCoordinate chunkBeingPopulated)
	{
		// TODO Auto-generated method stub		
	}

	@Override
	public void startPopulation(ChunkCoordinate chunkCoord)
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public void endPopulation()
	{
		// TODO Auto-generated method stub		
	}

	@Override
	public LocalMaterialData getMaterial(int x, int y, int z, ChunkCoordinate chunkBeingPopulated)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBlockAboveLiquidHeight(int x, int z, ChunkCoordinate chunkBeingPopulated)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBlockAboveSolidHeight(int x, int z, ChunkCoordinate chunkBeingPopulated)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHighestBlockAboveYAt(int x, int z, ChunkCoordinate chunkBeingPopulated)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHighestBlockYAt(int x, int z, boolean findSolid, boolean findLiquid, boolean ignoreLiquid,
			boolean ignoreSnow, boolean ignoreLeaves, ChunkCoordinate chunkBeingPopulated)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeightMapHeight(int x, int z, ChunkCoordinate chunkBeingPopulated)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLightLevel(int x, int y, int z, ChunkCoordinate chunkBeingPopulated)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBlock(int x, int y, int z, LocalMaterialData material, NamedBinaryTag metaDataTag,
			ChunkCoordinate chunkBeingPopulated)
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public LocalMaterialData[] getBlockColumnInUnloadedChunk(int x, int z)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void replaceBlocks(ChunkCoordinate chunkCoord)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isInsidePregeneratedRegion(ChunkCoordinate chunk)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ChunkCoordinate getSpawnChunk()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean generateModdedCaveGen(int x, int z, ChunkBuffer chunkBuffer)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInsideWorldBorder(ChunkCoordinate chunkCoordinate)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOTGPlus()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
