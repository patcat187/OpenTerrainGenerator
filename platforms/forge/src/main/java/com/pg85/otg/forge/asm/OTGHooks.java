package com.pg85.otg.forge.asm;

import com.pg85.otg.configuration.standard.WorldStandardValues;
import com.pg85.otg.forge.dimensions.WorldProviderOTG;

import net.minecraft.entity.Entity;

public class OTGHooks
{
	public static double getGravityFactor(Entity entity)
	{
		if(entity.world.provider instanceof WorldProviderOTG)
		{
			return ((WorldProviderOTG)entity.world.provider).getGravityFactor();
		} else {
			return 0.08D;
		}
	}

	public static double getGravityFactorMineCart(Entity entity)
	{
		if(entity.world.provider instanceof WorldProviderOTG)
		{
	    	double baseGravityFactor = WorldStandardValues.gravityFactor.getDefaultValue();
	    	double gravityFactor = ((WorldProviderOTG)entity.world.provider).getGravityFactor();
	    	return 0.03999999910593033D * (double)(gravityFactor / baseGravityFactor);
		} else {
			return 0.03999999910593033D;
		}
	}

	public static double getGravityFactorArrow(Entity entity)
	{
		if(entity.world.provider instanceof WorldProviderOTG)
		{
	    	double baseGravityFactor = WorldStandardValues.gravityFactor.getDefaultValue();
	    	double gravityFactor = ((WorldProviderOTG)entity.world.provider).getGravityFactor();
	    	return 0.05000000074505806D * (double)(gravityFactor / baseGravityFactor);
		} else {
			return 0.05000000074505806D;
		}
	}

	public static double getGravityFactorBoat(Entity entity)
	{
		if(entity.world.provider instanceof WorldProviderOTG)
		{
	    	double baseGravityFactor = WorldStandardValues.gravityFactor.getDefaultValue();
	    	double gravityFactor = ((WorldProviderOTG)entity.world.provider).getGravityFactor();
	    	return -0.03999999910593033D * (double)(gravityFactor / baseGravityFactor);
		} else {
			return -0.03999999910593033D;
		}
	}

	public static double getGravityFactorFallingBlock(Entity entity)
	{
		if(entity.world.provider instanceof WorldProviderOTG)
		{
	    	double baseGravityFactor = WorldStandardValues.gravityFactor.getDefaultValue();
	    	double gravityFactor = ((WorldProviderOTG)entity.world.provider).getGravityFactor();
	    	return 0.03999999910593033D * (double)(gravityFactor / baseGravityFactor);
		} else {
			return 0.03999999910593033D;
		}
	}

	public static double getGravityFactorItem(Entity entity)
	{
		if(entity.world.provider instanceof WorldProviderOTG)
		{
	    	double baseGravityFactor = WorldStandardValues.gravityFactor.getDefaultValue();
	    	double gravityFactor = ((WorldProviderOTG)entity.world.provider).getGravityFactor();
	    	return 0.03999999910593033D * (double)(gravityFactor / baseGravityFactor);
		} else {
			return 0.03999999910593033D;
		}
	}

	public static double getGravityFactorLlamaSpit(Entity entity)
	{
		if(entity.world.provider instanceof WorldProviderOTG)
		{
	    	double baseGravityFactor = WorldStandardValues.gravityFactor.getDefaultValue();
	    	double gravityFactor = ((WorldProviderOTG)entity.world.provider).getGravityFactor();
	    	return 0.05999999865889549D * (double)(gravityFactor / baseGravityFactor);
		} else {
			return 0.05999999865889549D;
		}
	}

	public static double getGravityFactorShulkerBullet(Entity entity)
	{
		if(entity.world.provider instanceof WorldProviderOTG)
		{
	    	double baseGravityFactor = WorldStandardValues.gravityFactor.getDefaultValue();
	    	double gravityFactor = ((WorldProviderOTG)entity.world.provider).getGravityFactor();
	    	return 0.04D * (double)(gravityFactor / baseGravityFactor);
		} else {
			return 0.04D;
		}
	}

	public static float getGravityFactorThrowable(Entity entity)
	{
		if(entity.world.provider instanceof WorldProviderOTG)
		{
	    	double baseGravityFactor = WorldStandardValues.gravityFactor.getDefaultValue();
	    	double gravityFactor = ((WorldProviderOTG)entity.world.provider).getGravityFactor();
	    	return (float)(0.03F * (double)(gravityFactor / baseGravityFactor));
		} else {
			return 0.03F;
		}
	}

	public static double getGravityFactorTNTPrimed(Entity entity)
	{
		if(entity.world.provider instanceof WorldProviderOTG)
		{
	    	double baseGravityFactor = WorldStandardValues.gravityFactor.getDefaultValue();
	    	double gravityFactor = ((WorldProviderOTG)entity.world.provider).getGravityFactor();
	    	return 0.03999999910593033D * (double)(gravityFactor / baseGravityFactor);
		} else {
			return 0.03999999910593033D;
		}
	}

	public static double getGravityFactorXPOrb(Entity entity)
	{
		if(entity.world.provider instanceof WorldProviderOTG)
		{
	    	double baseGravityFactor = WorldStandardValues.gravityFactor.getDefaultValue();
	    	double gravityFactor = ((WorldProviderOTG)entity.world.provider).getGravityFactor();
	    	return 0.029999999329447746D * (double)(gravityFactor / baseGravityFactor);
		} else {
			return 0.029999999329447746D;
		}
	}

	public static double getFallDamageFactor(double y, Entity entity)
	{
		if(entity.world.provider instanceof WorldProviderOTG)
		{
			return ((WorldProviderOTG)entity.world.provider).getFallDamageFactor(y);
		} else {
			return y;
		}
	}
}
