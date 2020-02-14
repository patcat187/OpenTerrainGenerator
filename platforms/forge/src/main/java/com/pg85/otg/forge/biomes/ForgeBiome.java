package com.pg85.otg.forge.biomes;

import com.pg85.otg.common.LocalBiome;
import com.pg85.otg.configuration.biome.BiomeConfig;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class ForgeBiome implements LocalBiome
{
    public final Biome biomeBase;
    private final boolean isCustom;
    private final int otgBiomeId;
    
    private final BiomeConfig biomeConfig;

    public ForgeBiome(Biome biome, int otgBiomeId, BiomeConfig biomeConfig)
    {
        this.biomeBase = biome;
        this.biomeConfig = biomeConfig;
        this.otgBiomeId = otgBiomeId;
        if (biome instanceof OTGBiome)
        {
            this.isCustom = true;
        } else {
            this.isCustom = false;
        }
        
        if(biomeConfig == null)
        {
        	throw new RuntimeException("Was machst du!?!");
        }
    }

    @Override
    public boolean isCustom()
    {
        return this.isCustom;
    }

    @Override
    public String getName()
    {
        return this.biomeConfig.getName();
    }

    @Override
    public int getOTGBiomeId()
    {
    	return this.otgBiomeId;
    }
    
    public Biome getHandle()
    {
        return this.biomeBase;
    }

    @Override
    public float getTemperatureAt(int x, int y, int z)
    {
        return this.biomeBase.getTemperature(new BlockPos(x, y, z));
    }

    @Override
    public BiomeConfig getBiomeConfig()
    {
        return this.biomeConfig;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
