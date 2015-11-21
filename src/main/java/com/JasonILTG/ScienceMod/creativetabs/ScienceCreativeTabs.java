package com.JasonILTG.ScienceMod.creativetabs;

import com.JasonILTG.ScienceMod.init.ScienceItems;
import com.JasonILTG.ScienceMod.init.ScienceModBlocks;
import com.JasonILTG.ScienceMod.references.Reference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ScienceCreativeTabs
{
	public static final CreativeTabs tabElements = new CreativeTabs(Reference.MOD_ID + ":elements")
	{
		@Override
		public Item getTabIconItem()
		{
			return ScienceItems.hydrogen;
		}
	};
	
	public static final CreativeTabs tabCompounds = new CreativeTabs(Reference.MOD_ID + ":compounds")
	{
		@Override
		public Item getTabIconItem()
		{
			return ScienceItems.water;
		}
	};
	
	public static final CreativeTabs tabMachines = new CreativeTabs(Reference.MOD_ID + ":machines")
	{
		@Override
		public Item getTabIconItem()
		{
			return Item.getItemFromBlock(ScienceModBlocks.electrolyzer);
		}
	};
	
	public static final CreativeTabs tabMiscScience = new CreativeTabs(Reference.MOD_ID + ":misc")
	{
		@Override
		public Item getTabIconItem()
		{
			return ScienceItems.jar;
		}
		
		@Override
		public String getTranslatedTabLabel()
		{
			return "Misc Science";
		}
	};
}
