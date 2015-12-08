package com.JasonILTG.ScienceMod.tileentity.machines;

import com.JasonILTG.ScienceMod.ScienceMod;
import com.JasonILTG.ScienceMod.crafting.MachineHeatedRecipe;
import com.JasonILTG.ScienceMod.crafting.MachinePoweredRecipe;
import com.JasonILTG.ScienceMod.crafting.MachineRecipe;
import com.JasonILTG.ScienceMod.init.ScienceModItems;
import com.JasonILTG.ScienceMod.manager.HeatManager;
import com.JasonILTG.ScienceMod.manager.PowerManager;
import com.JasonILTG.ScienceMod.messages.TEDoProgressMessage;
import com.JasonILTG.ScienceMod.messages.TEMaxProgressMessage;
import com.JasonILTG.ScienceMod.messages.TEPowerMessage;
import com.JasonILTG.ScienceMod.messages.TEProgressMessage;
import com.JasonILTG.ScienceMod.messages.TEResetProgressMessage;
import com.JasonILTG.ScienceMod.messages.TETempMessage;
import com.JasonILTG.ScienceMod.reference.NBTKeys;
import com.JasonILTG.ScienceMod.tileentity.general.ITileEntityHeated;
import com.JasonILTG.ScienceMod.tileentity.general.ITileEntityPowered;
import com.JasonILTG.ScienceMod.tileentity.general.TEInventory;
import com.JasonILTG.ScienceMod.util.InventoryHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.EnumFacing;

/**
 * A wrapper class for all machines that have an inventory and a progress bar in the mod.
 */
public abstract class TEMachine extends TEInventory implements IUpdatePlayerListBox, ITileEntityPowered, ITileEntityHeated
{
	/** The current machine recipe */
	protected MachineRecipe currentRecipe;
	/** The current progress */
	protected int currentProgress;
	/** The max progress of the current recipe */
	protected int maxProgress;
	public static final int DEFAULT_MAX_PROGRESS = 200;
	
	protected static final int UPGRADE_INV_INDEX = 0;
	protected static final int JAR_INV_INDEX = 1;
	protected static final int INPUT_INV_INDEX = 2;
	protected static final int OUTPUT_INV_INDEX = 3;
	protected static final int BATTERY_INV_INDEX = 4;
	
	// TODO implement ISidedInventory
	protected int[][] sidedAccess;
	
	protected int topAccessIndex = 0;
	protected int bottomAccessIndex = 1;
	protected int leftAccessIndex = 2;
	protected int rightAccessIndex = 3;
	protected int backAccessIndex = 4;
	
	protected EnumFacing frontFacingSide;
	protected EnumFacing topFacingSide;
	
	/** The HeatManager of the machine */
	protected HeatManager machineHeat;
	/** The PowerManager of the machine */
	protected PowerManager machinePower;
	
	public static final int DEFAULT_POWER_CAPACITY = 20000;
	public static final int DEFAULT_MAX_IN_RATE = 100;
	public static final int DEFAULT_MAX_OUT_RATE = 100;
	
	protected static final int DEFAULT_INV_COUNT = 5;
	
	private static final int NO_RECIPE_TAG_VALUE = -1;
	
	/** Whether or not to increment progress on the client side */
	protected boolean doProgress;
	
	/**
	 * Constructor.
	 * 
	 * @param name The machine's name
	 * @param inventorySizes The sizes of the inventories
	 * @param hasTank Whether or not the machine has a tank
	 */
	public TEMachine(String name, int[] inventorySizes, int numTanks)
	{
		super(name, inventorySizes, numTanks);
		
		// Recipe and processing
		currentRecipe = null;
		maxProgress = DEFAULT_MAX_PROGRESS;
		currentProgress = 0;
		doProgress = false;
		
		machineHeat = new HeatManager(HeatManager.DEFAULT_MAX_TEMP, HeatManager.DEFAULT_SPECIFIC_HEAT);
		machinePower = new PowerManager(DEFAULT_POWER_CAPACITY, DEFAULT_MAX_IN_RATE, DEFAULT_MAX_OUT_RATE);
	}
	
	/**
	 * Constructor that defaults numTanks to 0.
	 * 
	 * @param name The machine's name
	 * @param inventorySizes The sizes of the inventories
	 */
	public TEMachine(String name, int[] inventorySizes)
	{
		this(name, inventorySizes, 0);
	}
	
	@Override
	public void update()
	{
		// Only update progress on client side (for GUIs)
		if (this.worldObj.isRemote)
		{
			if (doProgress && currentProgress < maxProgress) currentProgress++;
			return;
		}
		
		// Server actions
		craft();
		
		// Update heat and power
		this.heatAction();
		this.powerAction();
		
		super.update();
	}
	
	/**
	 * @return The current progress
	 */
	public int getCurrentProgress()
	{
		return currentProgress;
	}
	
	/**
	 * Resets the current progress to 0.
	 */
	public void resetProgress()
	{
		currentProgress = 0;
	}
	
	/**
	 * Sets whether to do progress on the client side.
	 * 
	 * @param doProgress Whether to do progress on the client side
	 */
	public void setDoProgress(boolean doProgress)
	{
		this.doProgress = doProgress;
	}
	
	/**
	 * @return Whether to do progress on the client side
	 */
	public boolean getDoProgress()
	{
		return doProgress;
	}
	
	/**
	 * Sets the current progress.
	 * 
	 * @param progress The current progress
	 */
	public void setProgress(int progress)
	{
		currentProgress = progress;
	}
	
	/**
	 * @return The max progress
	 */
	public int getMaxProgress()
	{
		return maxProgress;
	}
	
	/**
	 * Sets the max progress.
	 * 
	 * @param maxProgress The max progress
	 */
	public void setMaxProgress(int maxProgress)
	{
		this.maxProgress = maxProgress;
	}
	
	/**
	 * @return The valid recipes for the machine
	 */
	public abstract MachineRecipe[] getRecipes();
	
	/**
	 * Consumes the required input for when the machine finishes processing.
	 * 
	 * @param recipe The recipe to follow when finishing crafting
	 */
	protected abstract void consumeInputs(MachineRecipe recipe);
	
	/**
	 * Adds the outputs to the inventory.
	 * 
	 * @param recipe The recipe to follow
	 */
	protected void doOutput(MachineRecipe recipe)
	{
		// null check for when a recipe doesn't have item outputs
		if (recipe.getItemOutputs() == null) return;
		
		// Give output
		ItemStack[] currentOutputInventorySlots = allInventories[OUTPUT_INV_INDEX];
		allInventories[OUTPUT_INV_INDEX] = InventoryHelper.mergeStackArrays(currentOutputInventorySlots,
				InventoryHelper.findInsertPattern(recipe.getItemOutputs(), currentOutputInventorySlots));
		
	}
	
	/**
	 * Determines whether the current recipe has the ingredients necessary.
	 * 
	 * @param recipeToUse The recipe to try crafting with
	 * @return Whether or not the given recipe can be crafted
	 */
	protected abstract boolean hasIngredients(MachineRecipe recipeToUse);
	
	/**
	 * Resets the recipe and all relevant variables.
	 */
	protected void resetRecipe()
	{
		currentRecipe = null;
		currentProgress = 0;
		maxProgress = DEFAULT_MAX_PROGRESS;
		
		doProgress = false;
		ScienceMod.snw.sendToAll(new TEResetProgressMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
		ScienceMod.snw.sendToAll(new TEDoProgressMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), false));
	}
	
	/**
	 * Tries to advance the progress of the current recipe if possible, and switches recipes otherwise.
	 */
	public void craft()
	{
		if (currentRecipe != null && hasIngredients(currentRecipe))
		{
			// We have a current recipe and it still works.
			
			// If there is not enough power, skip the cycle.
			if (this instanceof ITileEntityPowered && !((ITileEntityPowered) this).hasPower()) return;
			// If there is not enough heat, skip the cycle.
			if (this instanceof ITileEntityHeated && !((ITileEntityHeated) this).hasHeat()) return;
			
			currentProgress ++;
			if (currentRecipe instanceof MachinePoweredRecipe)
			{
				machinePower.consumePower(((MachinePoweredRecipe) currentRecipe).getPowerRequired());
			}
			if (currentRecipe instanceof MachineHeatedRecipe)
			{
				machineHeat.transferHeat(((MachineHeatedRecipe) currentRecipe).getHeatReleased());
			}
			
			if (currentProgress >= maxProgress)
			{
				// Time to output items and reset progress.
				currentProgress = 0;
				ScienceMod.snw.sendToAll(new TEResetProgressMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
				consumeInputs(currentRecipe);
				doOutput(currentRecipe);
			}
		}
		else {
			
			// The current recipe is no longer valid. We will reset the current progress and try to find a new recipe.
			if (doProgress) resetRecipe();
			
			for (MachineRecipe newRecipe : getRecipes())
			{
				if (hasIngredients(newRecipe))
				{
					// Found a new recipe. Start crafting in the next tick - the progress loss should be negligible.
					currentRecipe = newRecipe;
					maxProgress = currentRecipe.getTimeRequired();
					ScienceMod.snw.sendToAll(new TEMaxProgressMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), maxProgress));
					
					doProgress = true;
					ScienceMod.snw.sendToAll(new TEDoProgressMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), true));
					return;
				}
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		// Machine progress
		currentProgress = tag.getInteger(NBTKeys.RecipeData.CURRENT_PROGRESS);
		maxProgress = tag.getInteger(NBTKeys.RecipeData.MAX_PROGRESS);
		doProgress = tag.getBoolean(NBTKeys.RecipeData.DO_PROGRESS);
		
		// Load recipe
		int recipeValue = tag.getInteger(NBTKeys.RecipeData.RECIPE);
		if (recipeValue == NO_RECIPE_TAG_VALUE) {
			currentRecipe = null;
		}
		else {
			currentRecipe = getRecipes()[recipeValue];
		}
		
		// Load heat and power managers
		machineHeat.readFromNBT(tag);
		machinePower.readFromNBT(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		// Machine progress
		tag.setInteger(NBTKeys.RecipeData.CURRENT_PROGRESS, currentProgress);
		tag.setInteger(NBTKeys.RecipeData.MAX_PROGRESS, maxProgress);
		tag.setBoolean(NBTKeys.RecipeData.DO_PROGRESS, doProgress);
		
		// Save recipe
		if (currentRecipe == null) {
			tag.setInteger(NBTKeys.RecipeData.RECIPE, NO_RECIPE_TAG_VALUE);
		}
		else {
			tag.setInteger(NBTKeys.RecipeData.RECIPE, currentRecipe.ordinal());
		}
		
		// Save heat and power managers
		machineHeat.writeToNBT(tag);
		machinePower.writeToNBT(tag);
	}
	
	@Override
	public void sendInfo()
	{
		if (this.worldObj.isRemote) return;
		
		super.sendInfo();

		ScienceMod.snw.sendToAll(new TEDoProgressMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), doProgress));
		ScienceMod.snw.sendToAll(new TEProgressMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), currentProgress));
		ScienceMod.snw.sendToAll(new TEMaxProgressMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), maxProgress));
		ScienceMod.snw.sendToAll(new TEPowerMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), getCurrentPower()));
		ScienceMod.snw.sendToAll(new TETempMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), getCurrentTemp()));
	}
	
	// TODO implement ISidedInventory and add Javadocs
	public int getMachineSide(EnumFacing side)
	{
		return 0;
	}
	
	public void setMachineOrientation(EnumFacing front, EnumFacing top)
	{
		frontFacingSide = front;
		topFacingSide = top;
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return sidedAccess[getMachineSide(side)];
	}

	@Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
		int[] faceSlots = getSlotsForFace(direction);
		boolean hasSlot = false;
		for (int slotIndex : faceSlots)
		{
			if (slotIndex == index) hasSlot = true;
		}
		if (!hasSlot) return false;
		
		int invIndex = getInvIndexBySlotIndex(index);
		if (invIndex == OUTPUT_INV_INDEX) return false;
		if (invIndex == JAR_INV_INDEX && !itemStackIn.isItemEqual(new ItemStack(ScienceModItems.jar))) return false;
		
		ItemStack stackInSlot = getStackInSlot(index);
		if (stackInSlot == null) return true;
		if (!stackInSlot.isItemEqual(itemStackIn)) return false;
		
		return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
    	int[] faceSlots = getSlotsForFace(direction);
		boolean hasSlot = false;
		for (int slotIndex : faceSlots)
		{
			if (slotIndex == index) hasSlot = true;
		}
		if (!hasSlot) return false;
		
		int invIndex = getInvIndexBySlotIndex(index);
		if (invIndex == INPUT_INV_INDEX) return false;
		if (invIndex == JAR_INV_INDEX && !stack.isItemEqual(new ItemStack(ScienceModItems.jar))) return false;
		
		ItemStack stackInSlot = getStackInSlot(index);
		if (stackInSlot == null) return false;
		if (!stackInSlot.isItemEqual(stack)) return false;
		
		return true;
    }
    
    @Override
    public HeatManager getHeatManager()
    {
    	return machineHeat;
    }
    
    @Override
    public boolean hasHeat()
    {
    	return true;
    }
    
    @Override
    public void heatAction()
    {
    	if (machineHeat.update(this.getWorld(), this.getPos()))
			ScienceMod.snw.sendToAll(new TETempMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), getCurrentTemp()));
    }
    
    @Override
    public float getCurrentTemp()
    {
    	return machineHeat.getCurrentTemp();
    }
    
    @Override
    public void setCurrentTemp(float temp)
    {
    	// Only allowed on the client side
    	if (!this.worldObj.isRemote) return;
    	machineHeat.setCurrentTemp(temp);
    }
    
    @Override
    public PowerManager getPowerManager()
    {
    	return machinePower;
    }
    
    @Override
    public boolean hasPower()
    {
    	if (currentRecipe instanceof MachinePoweredRecipe)
    	{
    		return machinePower.getCurrentPower() > ((MachinePoweredRecipe) currentRecipe).getPowerRequired();
    	}
    	return true;
    }
    
    @Override
    public void powerAction()
    {
    	if (machinePower.update(this.getWorld(), this.getPos())) 
			ScienceMod.snw.sendToAll(new TEPowerMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), getCurrentPower()));
    }
    
    @Override
    public int getPowerCapacity()
    {
    	return machinePower.getCapacity();
    }
    
    @Override
    public int getCurrentPower()
    {
    	return machinePower.getCurrentPower();
    }
    
    @Override
    public void setCurrentPower(int amount)
    {
    	// Only allowed on the client side
    	if (!this.worldObj.isRemote) return;
    	machinePower.setCurrentPower(amount);
    }
}