package com.JasonILTG.ScienceMod.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraftforge.fluids.FluidTank;

public class TEElectrolyzer extends TEMachine implements ISidedInventory
{
	public static final int INVENTORY_SIZE = 4;
	public static final int ITEM_INPUT_INDEX = 0;
	public static final int JAR_INPUT_INDEX = 1;
	public static final int[] OUTPUT_INDEX = { 3, 4 };
	
	private FluidTank tank = new FluidTank(10000);
	
}
