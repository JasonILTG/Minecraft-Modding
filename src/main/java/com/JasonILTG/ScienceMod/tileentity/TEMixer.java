package com.JasonILTG.ScienceMod.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import com.JasonILTG.ScienceMod.crafting.MachineRecipe;
import com.JasonILTG.ScienceMod.init.ScienceModItems;
import com.JasonILTG.ScienceMod.item.Solution;
import com.JasonILTG.ScienceMod.reference.NBTKeys;
import com.JasonILTG.ScienceMod.reference.NBTKeys.Chemical;
import com.JasonILTG.ScienceMod.reference.NBTTypes;
import com.JasonILTG.ScienceMod.tileentity.general.TEMachine;
import com.JasonILTG.ScienceMod.util.ItemStackHelper;
import com.JasonILTG.ScienceMod.util.LogHelper;
import com.JasonILTG.ScienceMod.util.NBTHelper;

public class TEMixer extends TEMachine
{
	public static final String NAME = "Mixer";
	
	public static final int INVENTORY_SIZE = 5;
	public static final int ITEM_INPUT_INDEX = 0;
	public static final int JAR_OUTPUT_INDEX = 1;
	public static final int JAR_INPUT_INDEX = 2;
	public static final int[] OUTPUT_INDEX = { 3 };
	public static final int DISPLAY_INDEX = 4;
	
	public static final int DEFAULT_MAX_PROGRESS = 100;
	public static final int DEFAULT_TANK_CAPACITY = 10000;
	
	public static final int DEFAULT_ENERGY_CAPACITY = 0;
	
	private FluidTank mixTank;
	private ItemStack solution;
	
	private boolean toUpdate;
	
	public TEMixer()
	{
		// Initialize everything
		super(NAME, DEFAULT_MAX_PROGRESS, INVENTORY_SIZE, OUTPUT_INDEX);
		mixTank = new FluidTank(DEFAULT_TANK_CAPACITY);
		
		solution = new ItemStack(ScienceModItems.solution);
		NBTTagList ionList = new NBTTagList();
		NBTTagList precipitateList = new NBTTagList();
		NBTTagCompound solutionTag = new NBTTagCompound();
		solutionTag.setTag(Chemical.ION, ionList);
		solutionTag.setTag(Chemical.PRECIPITATES, precipitateList);
		solutionTag.setBoolean(Chemical.STABLE, true);
		solution.setTagCompound(solutionTag);
		
		toUpdate = true;
	}
	
	@Override
	public void update()
	{
		// Prevent double updates due to slowness
		if (toUpdate)
		{
			toUpdate = false;
			addMixtures();
			addSolutions();
			Solution.checkPrecipitates(solution);
			Solution.checkSolubility(solution);
			ItemStackHelper.checkEmptyStacks(inventory);
			toUpdate = true;
		}
		inventory[DISPLAY_INDEX] = solution.copy();
		super.update();
	}
	
	private void addMixtures()
	{
		// Null check
		ItemStack stack = inventory[ITEM_INPUT_INDEX];
		if (stack == null) return;
		
		if (stack.isItemEqual(new ItemStack(ScienceModItems.mixture)))
		{
			// Check that the item stack is a mixture
			
			// Find the number of available jar spaces
			int jarSpace = 0;
			if (inventory[JAR_OUTPUT_INDEX] == null)
			{
				jarSpace = this.getInventoryStackLimit();
			}
			else if (inventory[JAR_OUTPUT_INDEX].isItemEqual(new ItemStack(ScienceModItems.jar)))
			{
				jarSpace = this.getInventoryStackLimit() - inventory[JAR_OUTPUT_INDEX].stackSize;
			}
			
			int numToAdd = Math.min(jarSpace, stack.stackSize);
			NBTTagList precipitatesToAdd = stack.getTagCompound().getTagList(Chemical.PRECIPITATES, NBTTypes.COMPOUND);
			NBTTagList precipitateList = solution.getTagCompound().getTagList(Chemical.PRECIPITATES, NBTTypes.COMPOUND);
			for (int i = 0; i < numToAdd; i ++)
			{
				precipitateList = NBTHelper.combineTagLists(precipitateList, precipitatesToAdd, Chemical.PRECIPITATE, null, null, Chemical.MOLS);
			}
			solution.getTagCompound().setTag(Chemical.PRECIPITATES, precipitateList);
			
			inventory[JAR_OUTPUT_INDEX].stackSize += numToAdd;
			inventory[ITEM_INPUT_INDEX].splitStack(numToAdd);
		}
	}
	
	private void addSolutions()
	{
		// Null check
		ItemStack stack = inventory[ITEM_INPUT_INDEX];
		if (stack == null) return;
		
		if (stack.isItemEqual(new ItemStack(ScienceModItems.solution)))
		{
			// Check that the item stack is a solution
			
			// Find the number of available jar spaces
			int jarSpace = 0;
			if (inventory[JAR_OUTPUT_INDEX] == null)
			{
				jarSpace = this.getInventoryStackLimit();
			}
			else if (inventory[JAR_OUTPUT_INDEX].isItemEqual(new ItemStack(ScienceModItems.jar)))
			{
				jarSpace = this.getInventoryStackLimit() - inventory[JAR_OUTPUT_INDEX].stackSize;
			}
			if (jarSpace == 0) return;
			
			// Find the amount of available tank space
			int tankSpace = mixTank.getCapacity() - mixTank.getFluidAmount();
			
			int numToAdd = Math.min(Math.min(jarSpace, stack.stackSize), tankSpace / 250);
			NBTTagList precipitatesToAdd = stack.getTagCompound().getTagList(Chemical.PRECIPITATES, NBTTypes.COMPOUND);
			NBTTagList precipitateList = solution.getTagCompound().getTagList(Chemical.PRECIPITATES, NBTTypes.COMPOUND);
			NBTTagList ionsToAdd = stack.getTagCompound().getTagList(Chemical.IONS, NBTTypes.COMPOUND);
			NBTTagList ionList = solution.getTagCompound().getTagList(Chemical.IONS, NBTTypes.COMPOUND);
			for (int i = 0; i < numToAdd; i ++)
			{
				precipitateList = NBTHelper.combineTagLists(precipitateList, precipitatesToAdd, Chemical.PRECIPITATE, null, null, Chemical.MOLS);
				ionList = NBTHelper.combineTagLists(ionList, ionsToAdd, Chemical.ION, null, null, Chemical.MOLS);
			}
			solution.getTagCompound().setTag(Chemical.PRECIPITATES, precipitateList);
			solution.getTagCompound().setTag(Chemical.IONS, ionList);
			
			if (inventory[JAR_OUTPUT_INDEX] == null)
			{
				inventory[JAR_OUTPUT_INDEX] = new ItemStack(ScienceModItems.jar, numToAdd);
			}
			else
			{
				inventory[JAR_OUTPUT_INDEX].stackSize += numToAdd;
			}
			inventory[ITEM_INPUT_INDEX].splitStack(numToAdd);
			this.fillAll(new FluidStack(FluidRegistry.WATER, 250 * numToAdd));
		}
	}
	
	@Override
	protected boolean hasIngredients(MachineRecipe recipeToUse)
	{
		// null check
		if (recipeToUse == null) return false;
		
		// If the recipe cannot use the input, the attempt fails.
		if (!recipeToUse.canProcess(inventory[JAR_INPUT_INDEX], mixTank.getFluid()))
			return false;
		
		// Try to match output items with output slots.
		ItemStack[] storedOutput = new ItemStack[OUTPUT_INDEX.length];
		for (int i = 0; i < OUTPUT_INDEX.length; i ++)
			storedOutput[i] = inventory[OUTPUT_INDEX[i]];
		ItemStack[] newOutput = recipeToUse.getItemOutputs();
		
		if (ItemStackHelper.findInsertPattern(newOutput, storedOutput) == null) return false;
		
		return true;
	}
	
	@Override
	protected void consumeInputs(MachineRecipe recipe)
	{
		if (!(recipe instanceof MixerRecipe)) return;
		MixerRecipe validRecipe = (MixerRecipe) recipe;
		
		// Consume input
		if (inventory[JAR_INPUT_INDEX] == null) LogHelper.fatal("Jar Stack is null!");
		inventory[JAR_INPUT_INDEX].splitStack(validRecipe.reqJarCount);
		
		ItemStackHelper.checkEmptyStacks(inventory);
	}
	
	@Override
	protected void doOutput(MachineRecipe recipe)
	{
		// Calculate the output
		double multiplier = 250. / mixTank.getFluidAmount();
		NBTTagList ionList = solution.getTagCompound().getTagList(NBTKeys.IONS, NBTTypes.COMPOUND);
		NBTTagList outputIons = (NBTTagList) ionList.copy();
		NBTTagList outputPrecipitates = new NBTTagList();
		for (int i = 0; i < ionList.tagCount(); i ++)
		{
			double prevMols = ionList.getCompoundTagAt(i).getDouble(NBTKeys.MOLS);
			outputIons.getCompoundTagAt(i).setDouble(NBTKeys.MOLS, prevMols * multiplier);
			ionList.getCompoundTagAt(i).setDouble(NBTKeys.MOLS, prevMols * (1.0 - multiplier));
		}
		NBTTagCompound outputTag = new NBTTagCompound();
		outputTag.setTag(NBTKeys.IONS, outputIons);
		outputTag.setTag(NBTKeys.PRECIPITATES, outputPrecipitates);
		
		if (inventory[OUTPUT_INDEX[0]] == null)
		{
			inventory[OUTPUT_INDEX[0]] = new ItemStack(ScienceModItems.solution);
			inventory[OUTPUT_INDEX[0]].setTagCompound(outputTag);
			
		}
		else if (inventory[OUTPUT_INDEX[0]].isItemEqual(new ItemStack(ScienceModItems.solution)))
		{
			inventory[OUTPUT_INDEX[0]].stackSize += 1;
		}
		
		mixTank.drain(250, true);
	}
	
	@Override
	public MachineRecipe[] getRecipes()
	{
		return MixerRecipe.values();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		NBTHelper.readTanksFromNBT(new FluidTank[] { mixTank }, tag);
		// null check
		if (mixTank == null) mixTank = new FluidTank(DEFAULT_TANK_CAPACITY);
		
		// Read solution from tag
		solution = new ItemStack(ScienceModItems.solution);
		NBTTagCompound solutionTag = new NBTTagCompound();
		NBTTagList ionList = tag.getTagList(Chemical.IONS, NBTTypes.COMPOUND);
		NBTTagList precipitateList = tag.getTagList(Chemical.PRECIPITATES, NBTTypes.COMPOUND);
		solutionTag.setTag(Chemical.IONS, ionList);
		solutionTag.setTag(Chemical.PRECIPITATES, precipitateList);
		solutionTag.setBoolean(Chemical.STABLE, false);
		solution.setTagCompound(solutionTag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		NBTHelper.writeTanksToNBT(new FluidTank[] { mixTank }, tag);
		
		// Write solution to tag
		tag.setTag(Chemical.IONS, solution.getTagCompound().getTagList(Chemical.IONS, NBTTypes.COMPOUND));
		tag.setTag(Chemical.PRECIPITATES, solution.getTagCompound().getTagList(Chemical.PRECIPITATES, NBTTypes.COMPOUND));
		tag.setBoolean(Chemical.STABLE, solution.getTagCompound().getBoolean(Chemical.STABLE));
	}
	
	@Override
	public void checkFields()
	{
		super.checkFields();
		if (mixTank == null) mixTank = new FluidTank(DEFAULT_TANK_CAPACITY);
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		if (index == JAR_INPUT_INDEX && !stack.getIsItemStackEqual(new ItemStack(ScienceModItems.jar, 1))) return false;
		return true;
	}
	
	public boolean fillAll(FluidStack fluid)
	{
		if (mixTank.getCapacity() - mixTank.getFluidAmount() < fluid.amount) return false;
		
		mixTank.fill(fluid, true);
		return true;
	}
	
	public enum MixerRecipe implements MachineRecipe
	{
		FillJar(20, 1, new FluidStack(FluidRegistry.WATER, 250), new ItemStack[] { new ItemStack(ScienceModItems.solution) });
		
		public final int timeReq;
		public final int reqJarCount;
		public final FluidStack reqFluidStack;
		public final ItemStack[] outItemStack;
		
		private MixerRecipe(int timeRequired, int requiredJarCount, FluidStack requiredFluidStack,
				ItemStack[] outputItemStacks)
		{
			timeReq = timeRequired;
			reqJarCount = requiredJarCount;
			reqFluidStack = requiredFluidStack;
			outItemStack = outputItemStacks;
		}
		
		private boolean hasJars(ItemStack inputJarStack)
		{
			if (reqJarCount == 0) return true;
			if (inputJarStack == null) return false;
			return inputJarStack.stackSize >= reqJarCount;
		}
		
		private boolean hasFluid(FluidStack inputFluidStack)
		{
			if (reqFluidStack != null)
			{
				if (inputFluidStack == null) return false;
				
				if (!inputFluidStack.containsFluid(reqFluidStack)) return false;
			}
			return true;
		}
		
		/**
		 * @param params input format: jar input stack, item input stack, fluid input stack
		 */
		public boolean canProcess(Object... params)
		{
			ItemStack inputJarStack = (ItemStack) params[0];
			FluidStack inputFluidStack = (FluidStack) params[1];
			return hasJars(inputJarStack) && hasFluid(inputFluidStack);
		}
		
		public ItemStack[] getItemOutputs()
		{
			return outItemStack;
		}
		
		public int getTimeRequired()
		{
			return timeReq;
		}
	}
}
