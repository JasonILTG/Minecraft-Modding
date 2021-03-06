package com.JasonILTG.ScienceMod.manager;

import java.util.Random;

import com.JasonILTG.ScienceMod.IScienceNBT;
import com.JasonILTG.ScienceMod.handler.manager.ManagerRegistry;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Nothing right now, may be adding things later.
 * 
 * @author JasonILTG and syy1125
 */
public abstract class Manager
		implements IScienceNBT
{
	/** Whether the manager is valid */
	protected boolean valid;
	
	public static final Random RANDOMIZER = new Random();
	
	/**
	 * Constructor.
	 */
	protected Manager()
	{
		valid = true;
		
		ManagerRegistry.registerManager(this);
	}
	
	/**
	 * Marks the manager for removal.
	 * 
	 * @return Whether the manager was previously valid
	 */
	public boolean markForRemoval()
	{
		boolean wasValid = valid;
		valid = false;
		return wasValid;
	}
	
	/**
	 * @return Whether the manager is valid
	 */
	public boolean isValid()
	{
		return valid;
	}
	
	/**
	 * Recalculates effective parameters using base and multiplier values.
	 */
	public abstract void refreshFields();
	
	/**
	 * The action executed at the start of a tick.
	 */
	public abstract void onTickStart();
	
	/**
	 * The action executed at the end of a tick.
	 */
	public abstract void onTickEnd();
	
	/**
	 * Loads the manager from an NBTTag.
	 * 
	 * @param tag The NBTTag to load from
	 */
	public void readFromNBT(NBTTagCompound tag)
	{
		readFromDataTag(getDataTagFrom(tag));
		
		ManagerRegistry.registerManager(this);
	}
	
	/**
	 * Gets the data tag from the source tag.
	 * 
	 * @param source The source tag
	 * @return the NBT data tag
	 */
	protected NBTTagCompound getDataTagFrom(NBTTagCompound source)
	{
		return (NBTTagCompound) source.getCompoundTag(this.getClass().getSimpleName());
	}
	
	/**
	 * Writes the manager to an NBTTag.
	 * 
	 * @param tag The NBTTag to write to
	 */
	public void writeToNBT(NBTTagCompound tag)
	{
		writeDataTag(tag, makeDataTag());
	}
	
	/**
	 * Writes the data of the manager into a tag.
	 * 
	 * @param source The source tag containing all information
	 * @param dataTag The tag containing information about the manager
	 */
	protected void writeDataTag(NBTTagCompound source, NBTTagCompound dataTag)
	{
		source.setTag(this.getClass().getSimpleName(), dataTag);
	}
}
