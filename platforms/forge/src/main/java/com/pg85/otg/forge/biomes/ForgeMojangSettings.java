package com.pg85.otg.forge.biomes;

import com.pg85.otg.common.LocalMaterialData;
import com.pg85.otg.configuration.biome.settings.WeightedMobSpawnGroup;
import com.pg85.otg.configuration.standard.MojangSettings;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * Gets some default settings from the BiomeBase instance. The settings in the
 * BiomeBase instance are provided by Mojang.
 *
 * @see MojangSettings
 */
public final class ForgeMojangSettings implements MojangSettings
{
    private final Biome biomeBase;

    /**
     * Creates an instance that provides access to the default settings of the
     * vanilla biome with the given id.
     *
     * @param biomeId The id of the biome.
     * @return The settings.
     */
    public static MojangSettings fromResourceLocation(ResourceLocation location)
    {
    	Biome baseBiome = ForgeRegistries.BIOMES.getValue(location);
    	if(baseBiome != null)
    	{
    		return fromBiomeBase(baseBiome);
    	}
    	throw new RuntimeException("This should not happen."); // TODO: Remove after testing
    }

    /**
     * Creates an instance that provides access to the default settings of the
     * vanilla biome.
     *
     * @param biomeBase The biome.
     * @return The settings.
     */
    private static MojangSettings fromBiomeBase(Biome biomeBase)
    {
        return new ForgeMojangSettings(biomeBase);
    }

    private ForgeMojangSettings(Biome biomeBase)
    {
        this.biomeBase = biomeBase;
    }

    @Override
    public float getTemperature()
    {
        return this.biomeBase.getDefaultTemperature();
    }

    @Override
    public float getWetness()
    {
        return this.biomeBase.getDownfall();
    }

    @Override
    public float getSurfaceHeight()
    {
    	return this.biomeBase.getDepth();
    }

    @Override
    public float getSurfaceVolatility()
    {
        return this.biomeBase.getScale();
    }

    @Override
    public LocalMaterialData getSurfaceBlock()
    {
    	// TODO: Implement this
    	return null;
        //return ForgeMaterialData.ofMinecraftBlockState(this.biomeBase.topBlock);
    }

    @Override
    public LocalMaterialData getGroundBlock()
    {
    	// TODO: Implement this
    	return null;
        //return ForgeMaterialData.ofMinecraftBlockState(this.biomeBase.fillerBlock);
    }

    @Override
    public List<WeightedMobSpawnGroup> getMobSpawnGroup(EntityCategory entityCategory)
    {
    	// TODO: Implement this
    	return null;
        //return MobSpawnGroupHelper.getListFromMinecraftBiome(this.biomeBase, entityCategory);
    }

}
