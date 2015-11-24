package com.JasonILTG.ScienceMod.item.elements;

import java.util.ArrayList;
import java.util.List;

import com.JasonILTG.ScienceMod.creativetabs.ScienceCreativeTabs;
import com.JasonILTG.ScienceMod.init.ScienceModItems;
import com.JasonILTG.ScienceMod.item.general.ItemScience;
import com.JasonILTG.ScienceMod.reference.Names;
import com.JasonILTG.ScienceMod.reference.Reference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElementItem extends ItemScience
{
	public ElementItem()
	{
		super();
		setMaxStackSize(64);
		setHasSubtypes(true);
		setUnlocalizedName("element");
		setCreativeTab(ScienceCreativeTabs.tabElements);
		setContainerItem(ScienceModItems.jar);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{
		return String.format("item.%s%s.%s", Reference.RESOURCE_PREFIX, Names.Items.ELEMENT,
				Names.Items.ELEMENT_SUBTYPES[MathHelper.clamp_int(itemStack.getItemDamage(), 0,
						Names.Items.ELEMENT_SUBTYPES.length - 1)]);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List list)
	{
		for (int meta = 0; meta < Names.Items.ELEMENT_SUBTYPES.length; meta++)
		{
			list.add(new ItemStack(this, 1, meta));
		}
	}
	
	@Override
	public int getNumSubtypes()
	{
		return Names.Items.ELEMENT_SUBTYPES.length;
	}
	
	public List<Item> getElements()
	{
		List<Item> itemList = new ArrayList<Item>();
		
		for (int meta = 0; meta < Names.Items.ELEMENT_SUBTYPES.length; meta++)
		{
			itemList.add(new ItemStack(this, 1, meta).getItem());
		}
		
		return itemList;
	}
}
