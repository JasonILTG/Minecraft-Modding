package com.JasonILTG.ScienceMod.init;

import com.JasonILTG.ScienceMod.item.ItemScience;
import com.JasonILTG.ScienceMod.item.JarItem;
import com.JasonILTG.ScienceMod.item.elements.BerylliumItem;
import com.JasonILTG.ScienceMod.item.elements.BoronItem;
import com.JasonILTG.ScienceMod.item.elements.CarbonItem;
import com.JasonILTG.ScienceMod.item.elements.FluorineItem;
import com.JasonILTG.ScienceMod.item.elements.HeliumItem;
import com.JasonILTG.ScienceMod.item.elements.HydrogenItem;
import com.JasonILTG.ScienceMod.item.elements.LithiumItem;
import com.JasonILTG.ScienceMod.item.elements.NeonItem;
import com.JasonILTG.ScienceMod.item.elements.NitrogenItem;
import com.JasonILTG.ScienceMod.item.elements.OxygenItem;
import com.JasonILTG.ScienceMod.references.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ScienceItems
{
	public static ItemScience jar = new JarItem();
	public static ItemScience hydrogen = new HydrogenItem();
	public static ItemScience helium = new HeliumItem();
	public static ItemScience lithium = new LithiumItem();
	public static ItemScience beryllium = new BerylliumItem();
	public static ItemScience boron = new BoronItem();
	public static ItemScience carbon = new CarbonItem();
	public static ItemScience nitrogen = new NitrogenItem();
	public static ItemScience oxygen = new OxygenItem();
	public static ItemScience fluorine = new FluorineItem();
	public static ItemScience neon = new NeonItem();
	
	public static void init()
	{
		register();
	}
	
	public static void register()
	{
		GameRegistry.registerItem(jar, jar.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(hydrogen, hydrogen.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(helium, helium.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(lithium, lithium.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(beryllium, beryllium.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(boron, boron.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(carbon, carbon.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(nitrogen, nitrogen.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(oxygen, oxygen.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(fluorine, fluorine.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(neon, neon.getUnlocalizedName().substring(5));
	}
	
	public static void registerRenders()
	{
		registerRender(jar);
		registerRender(hydrogen);
		registerRender(helium);
		registerRender(lithium);
		registerRender(beryllium);
		registerRender(boron);
		registerRender(carbon);
		registerRender(nitrogen);
		registerRender(oxygen);
		registerRender(fluorine);
		registerRender(neon);
	}
	
	public static void registerRender(Item item)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, 
				new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
}