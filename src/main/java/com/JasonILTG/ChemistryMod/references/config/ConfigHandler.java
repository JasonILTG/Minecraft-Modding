package com.JasonILTG.ChemistryMod.references.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler
{
	public static void init(File configFile)
	{
		Configuration configuration = new Configuration(configFile);
	}
	
}
