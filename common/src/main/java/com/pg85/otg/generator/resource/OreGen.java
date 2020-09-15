package com.pg85.otg.generator.resource;

import com.pg85.otg.common.LocalWorld;
import com.pg85.otg.common.materials.LocalMaterialData;
import com.pg85.otg.configuration.biome.BiomeConfig;
import com.pg85.otg.configuration.standard.PluginStandardValues;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.helpers.MathHelper;
import com.pg85.otg.util.helpers.RandomHelper;
import com.pg85.otg.util.materials.MaterialSet;
import com.pg85.otg.util.materials.MaterialSetEntry;
import java.util.List;
import java.util.Random;

public class OreGen extends Resource
{
    private final int maxAltitude;
    private final int maxSize;
    private final int minAltitude;
    private final MaterialSet sourceBlocks;

    public OreGen(BiomeConfig biomeConfig, List<String> args) throws InvalidConfigException
    {
        super(biomeConfig);
        assureSize(7, args);

        material = readMaterial(args.get(0));
        maxSize = readInt(args.get(1), 1, 128);
        frequency = readInt(args.get(2), 1, 100);
        rarity = readRarity(args.get(3));
        minAltitude = readInt(args.get(4), PluginStandardValues.WORLD_DEPTH, PluginStandardValues.WORLD_HEIGHT - 1);
        maxAltitude = readInt(args.get(5), minAltitude, PluginStandardValues.WORLD_HEIGHT - 1);
        sourceBlocks = readMaterials(args, 6);
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
        final OreGen compare = (OreGen) other;
        return this.maxSize == compare.maxSize
               && this.minAltitude == compare.minAltitude
               && this.maxAltitude == compare.maxAltitude
               && (this.sourceBlocks == null ? this.sourceBlocks == compare.sourceBlocks
                   : this.sourceBlocks.equals(compare.sourceBlocks));
    }

    @Override
    public int getPriority()
    {
        return 10;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 11 * hash + super.hashCode();
        hash = 11 * hash + this.minAltitude;
        hash = 11 * hash + this.maxAltitude;
        hash = 11 * hash + this.maxSize;
        hash = 11 * hash + (this.sourceBlocks != null ? this.sourceBlocks.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
        return "Ore(" + material + "," + maxSize + "," + frequency + "," + rarity + "," + minAltitude + "," + maxAltitude + makeMaterials(sourceBlocks) + ")";
    }
        
    // use a byte since y is always between 0-255
    byte[][] highestBlocksCache;
    protected void createCache()
    {
   		this.highestBlocksCache = new byte[32][32];
    }
    
    protected void clearCache()
    {
    	this.highestBlocksCache = null;
    }
    
    @Override
    public void spawn(LocalWorld world, Random rand, boolean villageInChunk, int x, int z, ChunkCoordinate chunkBeingPopulated)
    {
    	// Make sure we stay within population bounds, anything outside won't be spawned (unless it's in an existing chunk).
    	
        parseMaterials(world, this.material, this.sourceBlocks);
        
        if(world.getConfigs().getWorldConfig().disableOreGen)
        {
        	if(this.material.isOre())
        	{
        		return;
        	}
        }
        
        int y = RandomHelper.numberInRange(rand, this.minAltitude, this.maxAltitude);
       
        float f = rand.nextFloat() * (float)Math.PI;
        double d0 = (double)((float)(x + 8) + MathHelper.sin(f) * (float)this.maxSize / 8.0F);
        double d1 = (double)((float)(x + 8) - MathHelper.sin(f) * (float)this.maxSize / 8.0F);
        double d2 = (double)((float)(z + 8) + MathHelper.cos(f) * (float)this.maxSize / 8.0F);
        double d3 = (double)((float)(z + 8) - MathHelper.cos(f) * (float)this.maxSize / 8.0F);
        double d4 = (double)(y + rand.nextInt(3) - 2);
        double d5 = (double)(y + rand.nextInt(3) - 2);
        
        float iFactor;
        double d6;
        double d7;
        double d8;

        double d9;
        double d10;
        double d11;

        int j;
        int k;
        int l;

        int i1;
        int j1;
        int k1; 
        
        double d13;
        double d14;
        double d15;
        
        LocalMaterialData material;
        int highestSolidBlock;            
        
        int areaBeingPoulatedSize = 32;
        boolean bFound = false;
        
        // TODO: This seems to be really poorly optimised.
        // Tries to spawn blocks in the same position a lot.
        // Redesign this.
        for (int i = 0; i < this.maxSize; i++)
        {
            iFactor = (float) i / (float) this.maxSize;
            d6 = d0 + (d1 - d0) * (double)iFactor;
            d7 = d4 + (d5 - d4) * (double)iFactor;
            d8 = d2 + (d3 - d2) * (double)iFactor;

            d9 = rand.nextDouble() * (double)this.maxSize / 16.0D;
            d10 = (double)(MathHelper.sin((float)Math.PI * iFactor) + 1.0F) * d9 + 1.0D;
            d11 = (double)(MathHelper.sin((float)Math.PI * iFactor) + 1.0F) * d9 + 1.0D;
            
            j = MathHelper.floor(d6 - d10 / 2.0D);
            k = MathHelper.floor(d7 - d11 / 2.0D);
            l = MathHelper.floor(d8 - d10 / 2.0D);

            i1 = MathHelper.floor(d6 + d10 / 2.0D);
            j1 = MathHelper.floor(d7 + d11 / 2.0D);
            k1 = MathHelper.floor(d8 + d10 / 2.0D);            
            
            if(j < chunkBeingPopulated.getBlockX())
            {
            	j = chunkBeingPopulated.getBlockX();
            }
            if(j > chunkBeingPopulated.getBlockX() + areaBeingPoulatedSize - 1)
            {
            	j = chunkBeingPopulated.getBlockX() + areaBeingPoulatedSize - 1;
            }
            if(i1 < chunkBeingPopulated.getBlockX())
            {
            	i1 = chunkBeingPopulated.getBlockX();
            }
            if(i1 > chunkBeingPopulated.getBlockX() + areaBeingPoulatedSize - 1)
            {
            	i1 = chunkBeingPopulated.getBlockX() + areaBeingPoulatedSize - 1;
            }
            
            if(l < chunkBeingPopulated.getBlockZ())
            {
            	l = chunkBeingPopulated.getBlockZ();
            }
            if(l > chunkBeingPopulated.getBlockZ() + areaBeingPoulatedSize - 1)
            {
            	l = chunkBeingPopulated.getBlockZ() + areaBeingPoulatedSize - 1;
            }
            if(k1 < chunkBeingPopulated.getBlockZ())
            {
            	k1 = chunkBeingPopulated.getBlockZ();
            }
            if(k1 > chunkBeingPopulated.getBlockZ() + areaBeingPoulatedSize - 1)
            {
            	k1 = chunkBeingPopulated.getBlockZ() + areaBeingPoulatedSize - 1;
            }
            
    		if(k < PluginStandardValues.WORLD_DEPTH)
    		{
    			k = PluginStandardValues.WORLD_DEPTH;
    		}
    		if(k > PluginStandardValues.WORLD_HEIGHT - 1)
    		{
    			k = PluginStandardValues.WORLD_HEIGHT - 1;
    		}
            
            for (int i3 = j; i3 <= i1; i3++)
            {
                d13 = ((double)i3 + 0.5D - d6) / (d10 / 2.0D);
                if (d13 * d13 < 1.0D)
                {                	
                    for (int i5 = l; i5 <= k1; i5++)
                    {
                    	if(j1 > 63) // Optimisation, don't look for highestblock if we're already looking below 63, default worlds have base terrain height at 63.
                    	{
	                		highestSolidBlock = this.highestBlocksCache[i3 - chunkBeingPopulated.getBlockX()][i5 - chunkBeingPopulated.getBlockZ()] & 0xFF; // byte to int conversion
	                		if(highestSolidBlock == 0)  // 0 is default / unset.
	                		{
	                			highestSolidBlock = world.getHeightMapHeight(i3, i5, chunkBeingPopulated);
	                			// TODO: This causes getHeightMapHeight to be called every time on a 0 height column, 
	                			// can't use -1 tho since we're using byte arrays. At least we're aborting the column 
	                			// immediately, since OreGen shouldn't be used to spawn things in empty columns. If
	                			// that's what you want, make a cloud generator or something, optimised for spawning in 
	                			// air/void.
	                			if(highestSolidBlock == -1)
	                			{
	                				highestSolidBlock = (byte)0; // Reset
	                				break;
	                			}
	                			this.highestBlocksCache[i3 - chunkBeingPopulated.getBlockX()][i5 - chunkBeingPopulated.getBlockZ()] = (byte)highestSolidBlock;
	                		}
                    		if(j1 > highestSolidBlock)
                    		{
                    			j1 = highestSolidBlock;
                    		}
                    	}
	                    for (int i4 = k; i4 <= j1; i4++)
	                    {
	                        d14 = ((double)i4 + 0.5D - d7) / (d11 / 2.0D);
	                        if (d13 * d13 + d14 * d14 < 1.0D)
	                        {
                                d15 = ((double)i5 + 0.5D - d8) / (d10 / 2.0D);
                                if((d13 * d13 + d14 * d14 + d15 * d15 < 1.0D))
                                {
	                                material = world.getMaterial(i3, i4, i5, chunkBeingPopulated);
	                                bFound = false;
	                                for(MaterialSetEntry sourceBlockEntry : this.sourceBlocks.materials)
	                                {
	                                	if(sourceBlockEntry.material.equals(material))
	                                	{
	                                		bFound = true;
	                                		break;
	                                	}
	                                }
	                                if(bFound)
	                                {
	                                    world.setBlock(i3, i4, i5, this.material, null, chunkBeingPopulated);
	                                }
                                }
                            }
                    	}
                	}
                }
            }
        }
    }
}
