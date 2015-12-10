package com.JasonILTG.ScienceMod.gui.machines;

import com.JasonILTG.ScienceMod.gui.JarSlot;
import com.JasonILTG.ScienceMod.gui.general.ScienceSlot;
import com.JasonILTG.ScienceMod.reference.Textures;
import com.JasonILTG.ScienceMod.tileentity.machines.TEMachine;

import net.minecraft.inventory.IInventory;

/**
 * Container class for air extractors.
 * 
 * @author JasonILTG and syy1125
 */
public class AirExtractorGUIContainer extends MachineGUIContainer
{
	protected static final int[] JAR_INPUT_SLOTS_ID = { 0, 1, 2 };
	protected static final int[] OUTPUT_SLOTS_ID = new int[27];
	{
		for (int i = 0; i < OUTPUT_SLOTS_ID.length; i ++)
			OUTPUT_SLOTS_ID[i] = i + 3;
	}
	
	protected static final int[] JAR_INPUT_SLOTS_X = { 169, 169, 169 };
	protected static final int[] JAR_INPUT_SLOTS_Y = { 18, 36, 54 };
	protected static final int[] OUTPUT_SLOTS_X = new int[27];
	{
		for (int i = 0; i < OUTPUT_SLOTS_X.length; i ++)
			OUTPUT_SLOTS_X[i] = 3 + 18 * (i % 9);
	}
	protected static final int[] OUTPUT_SLOTS_Y = new int[27];
	{
		for (int i = 0; i < OUTPUT_SLOTS_Y.length; i ++)
			OUTPUT_SLOTS_Y[i] = 18 + (i / 9) * 18;
	}
	
	protected static final int PLAYER_INV_Y = Textures.GUI.Machine.AIR_EXTRACTOR_GUI_HEIGHT + 22;
	
	/**
	 * Constructor.
	 * 
	 * @param playerInv The player inventory
	 * @param te The tile entity for this container
	 */
	public AirExtractorGUIContainer(IInventory playerInv, TEMachine te)
	{
		super(te, 30, PLAYER_INV_Y);
		addSlots();
		super.addPlayerInventorySlots(playerInv);
	}
	
	@Override
	public void addSlots()
	{
		// Jar Inputs, IDs 0-2
		for (int i = 0; i < JAR_INPUT_SLOTS_ID.length; i ++)
			this.addSlotToContainer(new JarSlot(inventory, JAR_INPUT_SLOTS_ID[i], JAR_INPUT_SLOTS_X[i], JAR_INPUT_SLOTS_Y[i]));
		
		// Outputs, IDs 3-29
		for (int i = 0; i < OUTPUT_SLOTS_ID.length; i ++)
			this.addSlotToContainer(new ScienceSlot(inventory, OUTPUT_SLOTS_ID[i], OUTPUT_SLOTS_X[i], OUTPUT_SLOTS_Y[i]));
	}
}
