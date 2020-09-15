package com.pg85.otg.generator.resource;

import com.pg85.otg.common.LocalWorld;
import com.pg85.otg.configuration.biome.BiomeConfig;
import com.pg85.otg.configuration.standard.PluginStandardValues;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.helpers.RandomHelper;
import com.pg85.otg.util.materials.MaterialSet;

import java.util.List;
import java.util.Random;

public class VeinGen extends Resource
{
    int maxAltitude; // Maximum altitude of the vein
    private int maxRadius; // Maximum size of the vein in blocks (inclusive)
    int minAltitude; // Minimum altitude of the vein
    private int minRadius; // Minimum size of the vein in blocks (inclusive)
    int oreFrequency; // Frequency of the ores in the vein
    int oreRarity; // Rarity of the ores in the vein
    int oreSize; // Average size of a ore in the vein
    MaterialSet sourceBlocks; // Blocks for the ore to spawn in
    private double veinRarity; // Chance for the vein to spawn in a chunk

    public VeinGen(BiomeConfig biomeConfig, List<String> args) throws InvalidConfigException
    {
        super(biomeConfig);
        assureSize(9, args);

        material = readMaterial(args.get(0));
        minRadius = readInt(args.get(1), 10, 200);
        maxRadius = readInt(args.get(2), minRadius, 201);
        veinRarity = readDouble(args.get(3), 0.0000001, 100);
        oreSize = readInt(args.get(4), 1, 64);
        oreFrequency = readInt(args.get(5), 1, 100);
        oreRarity = readInt(args.get(6), 1, 100);
        minAltitude = readInt(args.get(7), PluginStandardValues.WORLD_DEPTH,
                PluginStandardValues.WORLD_HEIGHT - 1);
        maxAltitude = readInt(args.get(8), minAltitude,
                PluginStandardValues.WORLD_HEIGHT - 1);
        sourceBlocks = readMaterials(args, 9);
    }

    @Override
    public boolean equals(Object other)
    {
        if (!super.equals(other))
            return false;
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (getClass() != other.getClass())
            return false;
        final VeinGen compare = (VeinGen) other;
        return this.veinRarity == compare.veinRarity
               && this.minRadius == compare.minRadius
               && this.maxRadius == compare.maxRadius
               && this.oreSize == compare.oreSize
               && this.oreFrequency == compare.oreFrequency
               && this.oreRarity == compare.oreRarity
               && this.minAltitude == compare.minAltitude
               && this.maxAltitude == compare.maxAltitude
               && (this.sourceBlocks == null ? this.sourceBlocks == compare.sourceBlocks
                   : this.sourceBlocks.equals(compare.sourceBlocks));
    }

    @Override
    public int getPriority()
    {
        return 9;
    }

    /**
     * Returns the vein that starts in the chunk.
     * @param world  The world to spawn in.
     * @param chunkX The x of the chunk.
     * @param chunkZ The z of the chunk.
     * @return The vein that starts in the chunk, or null if there is no
     *         starting vein.
     */
    private Vein getVeinStartInChunk(LocalWorld world, int chunkX, int chunkZ)
    {
        // Create a random generator that is constant for this chunk and vein
        Random random = RandomHelper.getRandomForCoords(chunkX, chunkZ, material.hashCode() * (minRadius + maxRadius + 100) + world.getSeed());

        if (random.nextDouble() * 100.0 < veinRarity)
        {
            int veinX = chunkX * 16 + random.nextInt(16) + 8;
            int veinY = RandomHelper.numberInRange(random, minAltitude, maxAltitude);
            int veinZ = chunkZ * 16 + random.nextInt(16) + 8;
            int veinSize = RandomHelper.numberInRange(random, minRadius, maxRadius);
            return new Vein(veinX, veinY, veinZ, veinSize);
        }

        return null;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 29 * hash + super.hashCode();
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.veinRarity) ^ (Double.doubleToLongBits(this.veinRarity) >>> 32));
        hash = 29 * hash + this.minRadius;
        hash = 29 * hash + this.maxRadius;
        hash = 29 * hash + this.oreSize;
        hash = 29 * hash + this.oreFrequency;
        hash = 29 * hash + this.oreRarity;
        hash = 29 * hash + this.minAltitude;
        hash = 29 * hash + this.maxAltitude;
        hash = 29 * hash + (this.sourceBlocks != null ? this.sourceBlocks.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
        String result = "Vein(" + material + "," + minRadius + "," + maxRadius + "," + veinRarity + ",";
        result += oreSize + "," + oreFrequency + "," + oreRarity + "," + minAltitude + "," + maxAltitude + makeMaterials(sourceBlocks) + ")";
        return result;
    }

    @Override
    public void spawn(LocalWorld world, Random random, boolean villageInChunk, int x, int z, ChunkCoordinate chunkBeingPopulated)
    {
        // Left blank, as spawnInChunk already handles this.
    }

    @Override
    protected void spawnInChunk(LocalWorld world, Random random, boolean villageInChunk, ChunkCoordinate chunkBeingPopulated)
    {
    	// Make sure we stay within population bounds, anything outside won't be spawned (unless it's in an existing chunk).
    	
        // Find all veins that reach this chunk, and spawn them
        int searchRadius = (this.maxRadius + 15) / 16;
        
        parseMaterials(world, this.material, this.sourceBlocks);

        if(world.getConfigs().getWorldConfig().disableOreGen)
        {
        	if(this.material.isOre())
        	{
        		return;
        	}
        }
        
        int currentChunkX = chunkBeingPopulated.getChunkX();
        int currentChunkZ = chunkBeingPopulated.getChunkZ();
        for (int searchChunkX = currentChunkX - searchRadius; searchChunkX < currentChunkX + searchRadius; searchChunkX++)
        {
            for (int searchChunkZ = currentChunkZ - searchRadius; searchChunkZ < currentChunkZ + searchRadius; searchChunkZ++)
            {
                Vein vein = getVeinStartInChunk(world, searchChunkX, searchChunkZ);
                if (vein != null && vein.reachesChunk(currentChunkX, currentChunkZ))
                {
                    vein.spawn(world, random, chunkBeingPopulated, this);
                }
            }
        }
    }

}
