package com.JasonILTG.ScienceMod.item;

import java.util.List;

import com.JasonILTG.ScienceMod.creativetabs.ScienceCreativeTabs;
import com.JasonILTG.ScienceMod.init.ScienceModItems;
import com.JasonILTG.ScienceMod.item.general.ItemJarred;
import com.JasonILTG.ScienceMod.reference.ChemElement;
import com.JasonILTG.ScienceMod.reference.NBTKeys;
import com.JasonILTG.ScienceMod.reference.NBTKeys.Chemical;
import com.JasonILTG.ScienceMod.reference.NBTTypes;
import com.JasonILTG.ScienceMod.util.NBTHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class Mixture extends ItemJarred
{
	public Mixture()
	{
		setUnlocalizedName("mixture");
		setCreativeTab(ScienceCreativeTabs.tabCompounds);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		//NBTHelper.checkDoubleFrac(stack.getTagCompound().getTagList(NBTKeys.PRECIPITATES, NBTTypes.COMPOUND), NBTKeys.MOLS);
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn)
	{
		NBTHelper.checkDoubleZero(stack.getTagCompound().getTagList(NBTKeys.PRECIPITATES, NBTTypes.COMPOUND), NBTKeys.MOLS);
	}
	
	public static ItemStack parseItemStackMixture(ItemStack stack)
	{
		//Null check
		if( stack == null ) return null;
		
		//Mixtures
		if( stack.isItemEqual(new ItemStack(ScienceModItems.mixture)) ) return stack.copy();
		
		//Elements
		if( (new ItemStack(stack.getItem())).isItemEqual(new ItemStack(ScienceModItems.element)) )
		{
			int meta = stack.getMetadata();
			
			ItemStack mixtureStack = new ItemStack(ScienceModItems.mixture, stack.stackSize);
			NBTTagCompound mixtureTag = new NBTTagCompound();
			NBTTagList precipitateList = new NBTTagList();
			
			NBTTagCompound elementTag = new NBTTagCompound();
			elementTag.setString(NBTKeys.PRECIPITATE, ChemElement.values()[meta].getElementCompound());
			elementTag.setDouble(NBTKeys.MOLS, 1.0);
			elementTag.setString(NBTKeys.STATE, ChemElement.values()[meta].getElementState());
			precipitateList.appendTag(elementTag);
			
			mixtureTag.setTag(NBTKeys.PRECIPITATES, precipitateList);
			mixtureStack.setTagCompound(mixtureTag);
			return mixtureStack;
		}
		
		//Everything else
		return null;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
	{
		if (stack.getTagCompound() != null)
		{
			NBTTagList tagList = stack.getTagCompound().getTagList(Chemical.PRECIPITATES, NBTTypes.COMPOUND);
			for (int i = 0; i < tagList.tagCount(); i ++)
			{
				NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
				byte mols = tagCompound.getByte(Chemical.MOLS);
				String precipitate = tagCompound.getString(Chemical.PRECIPITATE);
				String state = tagCompound.getString(Chemical.STATE);
				
				tooltip.add(String.format("%s%3f mol %s (%s)", EnumChatFormatting.DARK_GRAY, mols, precipitate, state));
			}
		}
	}
	
}
