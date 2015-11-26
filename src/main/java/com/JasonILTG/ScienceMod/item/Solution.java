package com.JasonILTG.ScienceMod.item;

import java.util.List;

import com.JasonILTG.ScienceMod.creativetabs.ScienceCreativeTabs;
import com.JasonILTG.ScienceMod.init.ScienceModItems;
import com.JasonILTG.ScienceMod.item.general.ItemScience;
import com.JasonILTG.ScienceMod.reference.NBTKeys;
import com.JasonILTG.ScienceMod.reference.NBTTypes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;

public class Solution extends ItemScience
{
	public Solution()
	{
		setUnlocalizedName("solution");
		setCreativeTab(ScienceCreativeTabs.tabCompounds);
		setContainerItem(ScienceModItems.jar);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
	{
		if(stack.getTagCompound() != null)
		{
			NBTTagList ionTagList = stack.getTagCompound().getTagList(NBTKeys.IONS, NBTTypes.COMPOUND);
			for( int i = 0; i < ionTagList.tagCount(); i++ )
			{
				NBTTagCompound ionTagCompound = ionTagList.getCompoundTagAt(i);
				double mols = ionTagCompound.getDouble(NBTKeys.MOLS);
				String component = ionTagCompound.getString(NBTKeys.ION);
				String state = ionTagCompound.getString(NBTKeys.STATE);
				
				tooltip.add(EnumChatFormatting.DARK_GRAY + String.valueOf(mols) + " mol " + component
						+ "(" + state + ")");
			}
			
			NBTTagList precipitateTagList = stack.getTagCompound().getTagList(NBTKeys.PRECIPITATES, NBTTypes.COMPOUND);
			for( int i = 0; i < precipitateTagList.tagCount(); i++ )
			{
				NBTTagCompound precipitateTagCompound = precipitateTagList.getCompoundTagAt(i);
				double mols = precipitateTagCompound.getDouble(NBTKeys.MOLS);
				String precipitate = precipitateTagCompound.getString(NBTKeys.PRECIPITATE);
				String state = precipitateTagCompound.getString(NBTKeys.STATE);
				
				tooltip.add(String.format("%s%3f mol %s (%s)", EnumChatFormatting.DARK_GRAY, mols, precipitate, state));
			}
		}
	}
}