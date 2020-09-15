package com.pg85.otg.configuration.settingType;

import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.util.helpers.StringHelper;
import com.pg85.otg.util.materials.MaterialSet;

/**
 * Reads and writes a set of materials, used for matching.
 *
 * <p>Materials are separated using a comma and, optionally, whitespace. Each
 * material is stripped from its whitespace and read using
 * {@link MaterialSet#parseAndAdd(String)}.
 *
 */
class MaterialSetSetting extends Setting<MaterialSet>
{
    private final String[] defaultValues;

    public MaterialSetSetting(String name, String... defaultValues)
    {
        super(name);
        this.defaultValues = defaultValues;
    }

    @Override
    public MaterialSet getDefaultValue()
    {
    	try
    	{
	        MaterialSet blocks = new MaterialSet();
	        for (String blockName : defaultValues)
	        {
	            blocks.parseAndAdd(blockName);
	        }
	        return blocks;
        } catch (InvalidConfigException e)
        {
            throw new AssertionError(e);
        }        
    }

    @Override
    public MaterialSet read(String string) throws InvalidConfigException
    {
        MaterialSet blocks = new MaterialSet();

        for (String blockName : StringHelper.readCommaSeperatedString(string))
        {
            blocks.parseAndAdd(blockName);
        }

        return blocks;
    }

}
