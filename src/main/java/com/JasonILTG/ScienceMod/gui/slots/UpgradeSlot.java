package com.JasonILTG.ScienceMod.gui.slots;

import com.JasonILTG.ScienceMod.item.upgrades.ScienceUpgrade;
import com.JasonILTG.ScienceMod.tileentity.general.TEInventory;

import net.minecraft.item.ItemStack;

/**
 * Slot class for upgrade slots.
 * 
 * @author JasonILTG and syy1125
 */
public class UpgradeSlot extends ScienceSlot
{
	/** The previous <code>ItemStack</code> in this slot */
	private ItemStack prevStack;
	/** The <code>TEInventory</code> this slot is part of */
	private TEInventory te;
	
	/**
	 * Constructor.
	 * 
	 * @param inventory The inventory
	 * @param index The index of this slot
	 * @param xPosition The x-position in the GUI
	 * @param yPosition The y-position in the GUI
	 */
	public UpgradeSlot(TEInventory inventory, int index, int xPosition, int yPosition)
	{
        super(inventory, index, xPosition, yPosition);
        prevStack = null;
        te = inventory;
    }
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return stack.getItem() != null && stack.getItem() instanceof ScienceUpgrade;
	}
	
	@Override
	public void onSlotChanged()
	{
		int numBefore = prevStack == null ? 0 : prevStack.stackSize;
		ItemStack newStack = this.getStack();
		ScienceUpgrade newUpgrade = newStack != null && newStack.getItem() instanceof ScienceUpgrade ? (ScienceUpgrade) newStack.getItem() : null;
		int numAfter = newStack == null ? 0 : newStack.stackSize;
		
		if (numBefore != 0) ((ScienceUpgrade) prevStack.getItem()).removeEffect(te);
		if (numAfter != 0) newUpgrade.applyEffect(te, numAfter);
		
		prevStack = newUpgrade == null ? null : newStack.copy();
		
		super.onSlotChanged();
	}
}
