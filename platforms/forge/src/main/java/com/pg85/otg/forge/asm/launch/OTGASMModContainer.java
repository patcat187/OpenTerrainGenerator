package com.pg85.otg.forge.asm.launch;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.ModMetadata;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.LoadController;

public class OTGASMModContainer extends DummyModContainer
{
	public OTGASMModContainer()
	{
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "otgcore";
		meta.name = "OTG Core";
		meta.description = "Allows gravity settings per world/dimension. Optional for 1.11.2, required for 1.12.2.";
		meta.version = "1.11.2 - v1";
		List<String> authorList = new ArrayList<String>();
		authorList.add("PG85");
		meta.authorList = authorList;
	}

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
    	bus.register(this);
        return true;
    }
}