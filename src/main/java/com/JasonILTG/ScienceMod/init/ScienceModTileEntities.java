package com.JasonILTG.ScienceMod.init;

import com.JasonILTG.ScienceMod.reference.Names;
import com.JasonILTG.ScienceMod.tileentity.TEAirExtractor;
import com.JasonILTG.ScienceMod.tileentity.TEElectrolyzer;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ScienceModTileEntities
{
	
	public static void init()
	{
		// Register the blocks with the game registry
		GameRegistry.registerTileEntity(TEElectrolyzer.class, Names.Tiles.ELECTROLYZER);
		GameRegistry.registerTileEntity(TEAirExtractor.class, Names.Tiles.AIR_EXTRACTOR);
	}
	
}
