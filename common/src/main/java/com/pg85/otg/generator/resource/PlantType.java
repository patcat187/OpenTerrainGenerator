package com.pg85.otg.generator.resource;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.pg85.otg.OTG;
import com.pg85.otg.common.LocalWorld;
import com.pg85.otg.common.materials.LocalMaterialData;
import com.pg85.otg.common.materials.LocalMaterials;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.materials.MaterialSet;

/**
 * Holds all small plants (1 or 2 blocks) of Minecraft so that users don't
 * have to use the confusing ids and data values Mojang and Bukkit gave them.
 */
public class PlantType
{
    // Builds lookup map
    private static final Map<String, PlantType> LOOKUP_MAP = new TreeMap<String, PlantType>(String.CASE_INSENSITIVE_ORDER);

    public static final PlantType Allium = register(new PlantType("Allium", LocalMaterials.RED_ROSE, 2));
    public static final PlantType AzureBluet = register(new PlantType("AzureBluet", LocalMaterials.RED_ROSE, 3));
    public static final PlantType BlueOrchid = register(new PlantType("BlueOrchid", LocalMaterials.RED_ROSE, 1));
    public static final PlantType BrownMushroom = register(new PlantType("BrownMushroom", LocalMaterials.BROWN_MUSHROOM, 0));
    public static final PlantType Dandelion = register(new PlantType("Dandelion", LocalMaterials.YELLOW_FLOWER, 0));
    public static final PlantType DeadBush = register(new PlantType("DeadBush", LocalMaterials.DEAD_BUSH, 0));
    public static final PlantType DoubleTallgrass = register(new PlantType("DoubleTallgrass", LocalMaterials.DOUBLE_PLANT, 2, 8));
    public static final PlantType Fern = register(new PlantType("Fern", LocalMaterials.LONG_GRASS, 2));
    public static final PlantType LargeFern = register(new PlantType("LargeFern", LocalMaterials.DOUBLE_PLANT, 3, 8));
    public static final PlantType Lilac = register(new PlantType("Lilac", LocalMaterials.DOUBLE_PLANT, 1, 8));
    public static final PlantType OrangeTulip = register(new PlantType("OrangeTulip", LocalMaterials.RED_ROSE, 5));
    public static final PlantType OxeyeDaisy = register(new PlantType("OxeyeDaisy", LocalMaterials.RED_ROSE, 8));
    public static final PlantType Peony = register(new PlantType("Peony", LocalMaterials.DOUBLE_PLANT, 5, 8));
    public static final PlantType PinkTulip = register(new PlantType("PinkTulip", LocalMaterials.RED_ROSE, 7));
    public static final PlantType Poppy = register(new PlantType("Poppy", LocalMaterials.RED_ROSE, 0));
    public static final PlantType RedMushroom = register(new PlantType("RedMushroom", LocalMaterials.RED_MUSHROOM, 0));
    public static final PlantType RedTulip = register(new PlantType("RedTulip", LocalMaterials.RED_ROSE, 4));
    public static final PlantType RoseBush = register(new PlantType("RoseBush", LocalMaterials.DOUBLE_PLANT, 4, 8));
    public static final PlantType Sunflower = register(new PlantType("Sunflower", LocalMaterials.DOUBLE_PLANT, 0, 8));
    public static final PlantType Tallgrass = register(new PlantType("Tallgrass", LocalMaterials.LONG_GRASS, 1));
    public static final PlantType WhiteTulip = register(new PlantType("WhiteTulip", LocalMaterials.RED_ROSE, 6));

    /**
     * Gets the plant with the given name. The name can be one of the premade
     * plant types or a blockName:data combination.
     * 
     * @param name Name of the plant type, case insensitive.
     * @return The plant type.
     * @throws InvalidConfigException If the name is invalid.
     */
    static PlantType getPlant(String name) throws InvalidConfigException
    {
        PlantType plantType = LOOKUP_MAP.get(name);
        if (plantType == null)
        {
        	LocalMaterialData material = OTG.getEngine().readMaterial(name);
            // Fall back on block name + data
            plantType = new PlantType(material);
        }
        return plantType;
    }

    /**
     * Gets all registered plant types.
     * 
     * @return All registered plant types.
     */
    public static Collection<PlantType> values()
    {
        return LOOKUP_MAP.values();
    }

    /**
     * Registers the plant type so that it can be retrieved using
     * {@link #getPlant(String)}.
     * 
     * @param plantType The plant type.
     * @return The plant type provided.
     */
    private static PlantType register(PlantType plantType)
    {
        LOOKUP_MAP.put(plantType.toString(), plantType);
        return plantType;
    }

    private final String name;
    private LocalMaterialData topBlock;
    private LocalMaterialData bottomBlock;

    /**
     * Creates a single-block plant with the given name.
     * 
     * @param name Custom name for this plant.
     * @param material The material of the block.
     * @param data The data value of the block.
     */
    private PlantType(String name, LocalMaterialData material, int data)
    {
        this.name = name;
        this.topBlock = null;
        // TODO: Reimplement this when block data works
        //this.bottomBlock = LocalMaterialManager.toLocalMaterialData(material, data);
        this.bottomBlock = material;
    }

    /**
     * Creates a single-block plant.
     * 
     * @param material Material of the plant.
     */
    private PlantType(LocalMaterialData material)
    {
        this.name = material.toString();
        this.topBlock = null;
        this.bottomBlock = material;
    }

    /**
     * Creates a two-block-high plant with the given name.
     * 
     * @param name Name of the plant.
     * @param material The material of the plant.
     * @param bottomData Data value for the bottom.
     * @param topData Data value for the top.
     */
    private PlantType(String name, LocalMaterialData material, int bottomData, int topData)
    {
        this.name = name;
        // TODO: Reimplement this when block data works        
        //this.topBlock = LocalMaterialManager.toLocalMaterialData(material, topData);
        //this.bottomBlock = LocalMaterialManager.toLocalMaterialData(material, bottomData);
        this.topBlock = material;
        this.bottomBlock = material;
    }

    /**
     * Gets the name of this plant type. You can get an equivalent plant back
     * type using {@link #getPlant(String)}.
     * 
     * @return The name of this plant type.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Spawns this plant in the world.
     * 
     * @param world The world to spawn in.
     * @param x X position of the plant.
     * @param y Y position of the lowest block of the plant.
     * @param z Z position of the plant.
     */
    void spawn(LocalWorld world, int x, int y, int z, ChunkCoordinate chunkBeingPopulated)
    {      
        parseMaterials(world, bottomBlock, null);
        
        world.setBlock(x, y, z, bottomBlock, null, chunkBeingPopulated);
        if (topBlock != null)
        {
            parseMaterials(world, topBlock, null);
            world.setBlock(x, y + 1, z, topBlock, null, chunkBeingPopulated);
        }
    }
    
    private void parseMaterials(LocalWorld world, LocalMaterialData material, MaterialSet sourceBlocks)
    {
    	material.parseForWorld(world);

        if (sourceBlocks != null)
        {
            sourceBlocks.parseForWorld(world);
        }
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bottomBlock == null) ? 0 : bottomBlock.hashCode());
        result = prime * result + ((topBlock == null) ? 0 : topBlock.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof PlantType))
        {
            return false;
        }
        PlantType other = (PlantType) obj;
        if (bottomBlock == null)
        {
            if (other.bottomBlock != null)
            {
                return false;
            }
        } else if (!bottomBlock.equals(other.bottomBlock))
        {
            return false;
        }
        if (topBlock == null)
        {
            if (other.topBlock != null)
            {
                return false;
            }
        } else if (!topBlock.equals(other.topBlock))
        {
            return false;
        }
        return true;
    }

    /**
     * Gets the bottom block of this plant.
     * 
     * @return The bottom block.
     */
    public LocalMaterialData getBottomMaterial()
    {
        return bottomBlock;
    }

    /**
     * Gets the top block of this plant. May be null.
     * 
     * @return The top block, or null if this plant only has one block.
     */
    public LocalMaterialData getTopMaterial()
    {
        return topBlock;
    }

}
