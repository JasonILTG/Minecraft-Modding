package com.JasonILTG.ScienceMod.util;

import java.util.ArrayList;

import com.JasonILTG.ScienceMod.reference.NBTKeys;
import com.JasonILTG.ScienceMod.reference.NBTKeys.Inventory;
import com.JasonILTG.ScienceMod.reference.NBTTypes;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fluids.FluidTank;

/**
 * Helper class for ScienceMod NBTTags.
 * 
 * @author JasonILTG and syy1125
 */
public class NBTHelper
{
	/**
	 * Reads an inventory from a compound tag.
	 * 
	 * @param tag The tag to read from
	 * @param inventory The inventory to read items into
	 */
	public static void readInventoryFromNBT(NBTTagCompound tag, ItemStack[] inventory)
	{
		// A list of tags that contains all the items in the inventory
		NBTTagList tagList = tag.getTagList(Inventory.ITEMS, NBTTypes.COMPOUND);
		
		// For each tag
		for (int i = 0; i < tagList.tagCount(); i ++)
		{
			// Get the ItemStack and index
			NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
			byte slotIndex = tagCompound.getByte(Inventory.SLOT);
			
			// Load the ItemStack into the inventory
			if (slotIndex >= 0 && slotIndex < inventory.length)
			{
				inventory[slotIndex] = ItemStack.loadItemStackFromNBT(tagCompound);
			}
		}
	}
	
	/**
	 * Saves an inventory into an NBT tag.
	 * 
	 * @param inventory The inventory of the current tile entity
	 * @param tag The tag to write to
	 */
	public static void writeInventoryToTag(ItemStack[] inventory, NBTTagCompound tag)
	{
		// Generate a new tag list to store item tags
		NBTTagList tagList = new NBTTagList();
		
		// For each inventory ItemStack
		for (int currentIndex = 0; currentIndex < inventory.length; currentIndex ++)
		{
			// If it is not null
			if (inventory[currentIndex] != null)
			{
				// Add the ItemStack to the tag list
				NBTTagCompound tagCompound = new NBTTagCompound();
				tagCompound.setByte(Inventory.SLOT, (byte) currentIndex);
				inventory[currentIndex].writeToNBT(tagCompound);
				tagList.appendTag(tagCompound);
			}
		}
		
		// Stores the tag list
		tag.setTag(Inventory.ITEMS, tagList);
	}
	
	/**
	 * Reads an array of FluidTanks from an NBTTag.
	 * 
	 * @param tanks The array to read the tanks into
	 * @param tag The tag to read from
	 */
	public static void readTanksFromNBT(FluidTank[] tanks, NBTTagCompound tag)
	{
		// A list of tags that contains all the items in the inventory
		NBTTagList tagList = tag.getTagList(Inventory.TANKS, NBTTypes.COMPOUND);
		
		// For each tag
		for (int i = 0; i < tagList.tagCount(); i ++)
		{
			NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
			byte slotIndex = tagCompound.getByte(Inventory.SLOT);
			
			// Load the tank
			if (slotIndex >= 0 && slotIndex < tanks.length) {
				tanks[slotIndex].readFromNBT(tagCompound);
			}
		}
	}
	
	/**
	 * Writes an array of FluidTanks to an NBTTag.
	 * 
	 * @param tanks The tanks to write to the tag
	 * @param tag The tag to write to
	 */
	public static void writeTanksToNBT(FluidTank[] tanks, NBTTagCompound tag)
	{
		// Generate a new tag list to store tank tags
		NBTTagList tagList = new NBTTagList();
		
		// For each tank
		for (int currentIndex = 0; currentIndex < tanks.length; currentIndex ++)
		{
			// If not null
			if (tanks[currentIndex] != null)
			{
				// Add a tag to the tag list
				NBTTagCompound tagCompound = new NBTTagCompound();
				tagCompound.setByte(Inventory.SLOT, (byte) currentIndex);
				tanks[currentIndex].writeToNBT(tagCompound);
				tagList.appendTag(tagCompound);
			}
		}
		
		// Add the tag list to the tag
		tag.setTag(Inventory.TANKS, tagList);
	}
	
	/**
	 * Combines two NBTTagLists, adding numerical values of tags with matching identifiers.
	 * 
	 * @param tagList1 The first NBTTagList
	 * @param tagList2 The second NBTTagList
	 * @param stringCompKey The key of the String identifier
	 * @param intCompKey The key of the int identifier
	 * @param intAddKey The key of the int to be added
	 * @param doubleAddKey The key of the double to be added
	 * @param fracAddKey The key of the int array fraction to be added
	 * @return The resulting NBTTagList
	 */
	public static NBTTagList combineTagLists(NBTTagList tagList1, NBTTagList tagList2, String stringCompKey, String intCompKey, String intAddKey,
			String doubleAddKey, String fracAddKey)
	{
		NBTTagList newTagList = new NBTTagList();
		
		// Get a list of the Strings in tagList1
		ArrayList<String> stringsIn1 = new ArrayList<String>();
		for (int i = 0; i < tagList1.tagCount(); i ++)
		{
			NBTTagCompound tag1 = (NBTTagCompound) tagList1.getCompoundTagAt(i).copy();
			newTagList.appendTag(tag1);
			stringsIn1.add(tag1.getString(stringCompKey));
		}
		
		for (int i = 0; i < tagList2.tagCount(); i ++)
		{
			NBTTagCompound tag2 = (NBTTagCompound) tagList2.getCompoundTagAt(i).copy();
			int indexIn1 = stringsIn1.indexOf(tag2.getString(stringCompKey));
			if (indexIn1 > -1)
			{
				// For each tag in tagList2, find a matching tag in tagList1 based on stringCompKey
				NBTTagCompound tag1 = newTagList.getCompoundTagAt(indexIn1);
				if (intCompKey == null || (intCompKey != null && tag1.getInteger(intCompKey) == tag2.getInteger(intCompKey)))
				{
					// If the tags match by intCompKey, combine them
					
					// Null check
					if (fracAddKey != null)
					{
						newTagList.getCompoundTagAt(indexIn1).setIntArray(fracAddKey,
								MathUtil.addFrac(tag1.getIntArray(fracAddKey), tag2.getIntArray(fracAddKey)));
					}
					
					// Null check
					if (doubleAddKey != null)
					{
						newTagList.getCompoundTagAt(indexIn1).setDouble(doubleAddKey, tag1.getDouble(doubleAddKey) + tag2.getDouble(doubleAddKey));
					}
					
					// Null check
					if (intAddKey != null)
					{
						newTagList.getCompoundTagAt(indexIn1).setInteger(intAddKey, tag1.getInteger(intAddKey) + tag2.getInteger(intAddKey));
					}
				}
				
			}
			else
			{
				// Otherwise just add a new tag
				newTagList.appendTag(tag2);
			}
		}
		
		return newTagList;
	}

	/**
	 * Reads a BlockPos from an NBTTag.
	 * 
	 * @param tag The tag to read from
	 * @return The BlockPos read from the tag
	 */
	public static BlockPos readBlockPosFromNBT(NBTTagCompound tag)
	{
		NBTTagCompound posTag = (NBTTagCompound) tag.getTag(NBTKeys.Block.POS);
		
		return new BlockPos(posTag.getInteger(NBTKeys.Block.X), posTag.getInteger(NBTKeys.Block.Y), posTag.getInteger(NBTKeys.Block.Z));
	}
	
	/**
	 * Writes a BlockPos to an NBTTag.
	 * 
	 * @param pos The BlockPos to write to the tag
	 * @param tag The tag to write to
	 */
	public static void writeBlockPosToNBT(BlockPos pos, NBTTagCompound tag)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		NBTTagCompound posTag = new NBTTagCompound();
		posTag.setInteger(NBTKeys.Block.X, x);
		posTag.setInteger(NBTKeys.Block.Y, y);
		posTag.setInteger(NBTKeys.Block.Z, z);
		tag.setTag(NBTKeys.Block.POS, posTag);
	}
}
