package com.JasonILTG.ScienceMod.tileentity.general;

import net.minecraft.world.IWorldNameable;

import com.JasonILTG.ScienceMod.manager.power.IPowered;

/**
 * Interface for all tile entities that involve power.
 * 
 * @author JasonILTG and syy1125
 */
public interface ITileEntityPowered extends IPowered, ITileEntityScienceMod, IWorldNameable
{
	/**
	 * @return Whether the tile entity has sufficient power to continue operation.
	 */
	public boolean hasPower();
	
	/**
	 * @return The tile entity's power capacity
	 */
	public float getPowerCapacity();
	
	/**
	 * @return The tile entity's current power
	 */
	public float getCurrentPower();
	
	/**
	 * Sets the current power of the tile entity. Only used on the client side.
	 * 
	 * @param amount The current power
	 */
	public void setCurrentPower(float amount);
}
