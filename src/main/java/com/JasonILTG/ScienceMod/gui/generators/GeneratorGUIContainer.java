package com.JasonILTG.ScienceMod.gui.generators;

import com.JasonILTG.ScienceMod.gui.general.InventoryGUIContainer;
import com.JasonILTG.ScienceMod.tileentity.generators.TEGenerator;

/**
 * Wrapper class for all generator Containers in ScienceMod.
 * 
 * @author JasonILTG and syy1125
 */
public class GeneratorGUIContainer extends InventoryGUIContainer
{
	/**
	 * Constructor.
	 * 
	 * @param inv The inventory of this container
	 * @param playerInvStartID The starting slot ID of the player inventory
	 * @param playerInvY The y-position of the player inventory
	 */
	public GeneratorGUIContainer(TEGenerator inv, int playerInvStartID, int playerInvY)
	{
		super(inv, playerInvStartID, playerInvY);
	}
	
	/**
	 * Adds the slots for the inventory.
	 */
	public void addSlots()
	{
		
	}
}
