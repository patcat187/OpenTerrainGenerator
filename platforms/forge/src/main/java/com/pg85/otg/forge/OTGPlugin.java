package com.pg85.otg.forge;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pg85.otg.forge.biome.OTGBiomeProvider;
import com.pg85.otg.forge.commands.OTGCommand;
import com.pg85.otg.forge.generator.OTGNoiseChunkGenerator;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("openterraingenerator")
public class OTGPlugin
{
	// TODO: Use Forge API to register DimensionSettings?
	public static final RegistryKey<DimensionSettings> field_242734_c = RegistryKey.func_240903_a_(Registry.field_243549_ar, new ResourceLocation("overworld"));

	// Register the otg worldtype for the world creation screen
	public static final BiomeGeneratorTypeScreens otgWorldType = new BiomeGeneratorTypeScreens("otg")
	{
		protected ChunkGenerator func_241869_a(Registry<Biome> p_241869_1_, Registry<DimensionSettings> p_241869_2_, long p_241869_3_)
		{
			return new OTGNoiseChunkGenerator(
				new OTGBiomeProvider(p_241869_3_, false, false, p_241869_1_), 
				p_241869_3_, 
				() -> {
					return p_241869_2_.func_243576_d(field_242734_c);
				}
			);
		}
	};

	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	public OTGPlugin()
	{		
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// Register the enqueueIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		// Register the processIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		// Register the doClientStuff method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		
		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
		
		// Register the otg worldtype for the world creation screen
		BiomeGeneratorTypeScreens.field_239068_c_.add(otgWorldType);	
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		// some preinit code
		LOGGER.info("HELLO FROM PREINIT");
		LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
	}

	private void doClientStuff(final FMLClientSetupEvent event)
	{
		// do something that can only be done on the client
		LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
	}

	private void enqueueIMC(final InterModEnqueueEvent event)
	{
		// some example code to dispatch IMC to another mod
		InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
	}

	private void processIMC(final InterModProcessEvent event)
	{
		// some example code to receive and process InterModComms from other mods
		LOGGER.info("Got IMC {}", event.getIMCStream().
		map(m->m.getMessageSupplier().get()).
		collect(Collectors.toList()));
	}

	
	@SubscribeEvent
	public void onCommandRegister(RegisterCommandsEvent event)
	{
		OTGCommand.register(event.getDispatcher());
	}
	
	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event)
	{
		// do something when the server starts
		LOGGER.info("HELLO from server starting");
	}

	// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
	// Event bus for receiving Registry Events)
	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents
	{
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
		{
			// register a new block here
			LOGGER.info("HELLO from Register Block");
		}
	}
}
