package com.JasonILTG.ScienceMod.init.crafting;

import com.JasonILTG.ScienceMod.init.ScienceModItems;
import com.JasonILTG.ScienceMod.reference.ChemElements;
import com.JasonILTG.ScienceMod.reference.NBTKeys.Chemical;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Init class for recipes for elements
 * 
 * @author JasonILTG and syy1125
 */
public class ElementCrafting
{
	/**
	 * Initializes all recipes for elements
	 */
	public static void init()
	{
		for (int meta = 0; meta < ChemElements.ELEMENT_COUNT; meta++)
		{
			if (!ChemElements.values()[meta].getElementState().equals("s")) continue;
			ItemStack element = new ItemStack(ScienceModItems.element, 1, meta);
			ItemStack dust = new ItemStack(ScienceModItems.dust);
			NBTTagCompound dustTag = new NBTTagCompound();
			NBTTagList precipitates = new NBTTagList();
			NBTTagCompound elementTag = new NBTTagCompound();
			elementTag.setString(Chemical.PRECIPITATE, ChemElements.values()[meta].getElementSymbol());
			elementTag.setIntArray(Chemical.MOLS, new int[]{ 1, 1 });
			elementTag.setString(Chemical.STATE, "s");
			precipitates.appendTag(elementTag);
			dustTag.setTag(Chemical.PRECIPITATES, precipitates);
			dust.setTagCompound(dustTag);
			GameRegistry.addShapelessRecipe(dust, element);
		}
	}
}
