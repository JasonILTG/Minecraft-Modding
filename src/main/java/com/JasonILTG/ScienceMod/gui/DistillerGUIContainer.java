package com.JasonILTG.ScienceMod.gui;

import com.JasonILTG.ScienceMod.gui.general.InventoryGUIContainer;
import com.JasonILTG.ScienceMod.gui.general.ScienceSlot;
import com.JasonILTG.ScienceMod.reference.Textures;
import com.JasonILTG.ScienceMod.tileentity.general.TEInventory;

import net.minecraft.inventory.IInventory;

public class DistillerGUIContainer extends InventoryGUIContainer
{
	protected static final int JAR_INPUT_SLOT_ID = 0;
	protected static final int INPUT_SLOT_ID = 1;
	protected static final int OUTPUT_SLOT_ID = 2;
	
	protected static final int INPUT_SLOT_X = 79;
	protected static final int INPUT_SLOT_Y = 18;
	protected static final int JAR_INPUT_SLOT_X = 105;
	protected static final int JAR_INPUT_SLOT_Y = 18;
	protected static final int OUTPUT_SLOT_X = 66;
	protected static final int OUTPUT_SLOT_Y = 58;
	
	protected static final int PLAYER_INV_Y = Textures.GUI.FILTER_GUI_HEIGHT + 22;
	
	public DistillerGUIContainer(IInventory playerInv, TEInventory te)
	{
		super(te, 3, PLAYER_INV_Y);
		addSlots();
		super.addPlayerInventorySlots(playerInv);
	}
	
	public void addSlots()
	{
		// Input, ID 0
		this.addSlotToContainer(new ScienceSlot(te, INPUT_SLOT_ID, INPUT_SLOT_X, INPUT_SLOT_Y));
		
		// Jar Input, ID 1
		this.addSlotToContainer(new JarSlot(te, JAR_INPUT_SLOT_ID, JAR_INPUT_SLOT_X, JAR_INPUT_SLOT_Y));
		
		// Output, ID 2
		this.addSlotToContainer(new ScienceSlot(te, OUTPUT_SLOT_ID, OUTPUT_SLOT_X, OUTPUT_SLOT_Y));
	}
}