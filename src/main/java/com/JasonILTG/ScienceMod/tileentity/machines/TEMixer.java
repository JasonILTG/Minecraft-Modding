package com.JasonILTG.ScienceMod.tileentity.machines;

import java.util.ArrayList;
import java.util.List;

import com.JasonILTG.ScienceMod.ScienceMod;
import com.JasonILTG.ScienceMod.crafting.te.MachineRecipe;
import com.JasonILTG.ScienceMod.init.ScienceModItems;
import com.JasonILTG.ScienceMod.item.chemistry.Mixture;
import com.JasonILTG.ScienceMod.item.chemistry.Solution;
import com.JasonILTG.ScienceMod.messages.MixerSolutionMessage;
import com.JasonILTG.ScienceMod.messages.TETankMessage;
import com.JasonILTG.ScienceMod.reference.Constants;
import com.JasonILTG.ScienceMod.reference.NBTKeys;
import com.JasonILTG.ScienceMod.reference.NBTKeys.Chemical;
import com.JasonILTG.ScienceMod.reference.NBTTypes;
import com.JasonILTG.ScienceMod.reference.chemistry.compounds.CommonCompounds;
import com.JasonILTG.ScienceMod.util.InventoryHelper;
import com.JasonILTG.ScienceMod.util.LogHelper;
import com.JasonILTG.ScienceMod.util.MathUtil;
import com.JasonILTG.ScienceMod.util.NBTHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Tile entity class for mixers.
 * 
 * @author JasonILTG and syy1125
 */
public class TEMixer extends TEMachine
{
	public static final String NAME = "Mixer";
	
	public static final int INVENTORY_SIZE = 6;
	
	public static final int UPGRADE_INV_SIZE = 2;
	
	public static final int ITEM_INPUT_INDEX = 2;
	public static final int INPUT_INV_SIZE = 1;
	
	public static final int JAR_INV_SIZE = 2;
	
	public static final int JAR_OUTPUT_INDEX = 0;
	public static final int JAR_OUTPUT_SIZE = 1;
	
	public static final int JAR_INPUT_INDEX = 1;
	public static final int JAR_INPUT_SIZE = 1;
	
	public static final int OUTPUT_INDEX = 3;
	public static final int OUTPUT_INV_SIZE = 1;
	
	public static final int NUM_TANKS = 1;
	public static final int MIX_TANK_INDEX = 0;
	
	public static final int DEFAULT_ENERGY_CAPACITY = 0;
	
	/** An ItemStack representing the contents of the mixer */
	private ItemStack solution;
	/** Prevents double updates due to lag */
	private boolean toUpdate;
	
	/** List of ion Strings for the tooltip */
	private List<String> ionList;
	/** List of precipitate Strings for the tooltip */
	private List<String> precipitateList;
	
	/**
	 * Default constructor.
	 */
	public TEMixer()
	{
		// Initialize everything
		super(NAME, new int[] { UPGRADE_INV_SIZE, JAR_INV_SIZE, INPUT_INV_SIZE, OUTPUT_INV_SIZE, NO_INV_SIZE }, NUM_TANKS);
		
		solution = new ItemStack(ScienceModItems.solution);
		NBTTagList ionList = new NBTTagList();
		NBTTagList precipitateList = new NBTTagList();
		NBTTagCompound solutionTag = new NBTTagCompound();
		solutionTag.setTag(NBTKeys.Chemical.ION, ionList);
		solutionTag.setTag(NBTKeys.Chemical.PRECIPITATES, precipitateList);
		solutionTag.setBoolean(NBTKeys.Chemical.STABLE, true);
		solution.setTagCompound(solutionTag);
		
		toUpdate = true;
		
		this.ionList = new ArrayList<String>();
		this.precipitateList = new ArrayList<String>();
	}
	
	@Override
	public void update()
	{
		super.update();
		
		// Prevent double updates due to slowness
		if (!worldObj.isRemote && toUpdate)
		{
			toUpdate = false;
			checkBoil();

			addSolutions();
			addMixtures();
			
			InventoryHelper.checkEmptyStacks(allInventories);
			toUpdate = true;
		}
	}
	
	/**
	 * Checks the contents of the mixer for reactions and 0 values.
	 */
	private void check()
	{
		if (tanks[MIX_TANK_INDEX].getFluidAmount() == 0)
		{
			if (solution.isItemEqual(new ItemStack(ScienceModItems.solution)))
			{
				ItemStack newMixture = new ItemStack(ScienceModItems.mixture);
				newMixture.setTagCompound(solution.getTagCompound());
				solution = newMixture;
			}
			Mixture.check(solution);
		}
		else
		{
			if (solution.isItemEqual(new ItemStack(ScienceModItems.mixture)))
			{
				ItemStack newSolution = new ItemStack(ScienceModItems.solution);
				newSolution.setTagCompound(solution.getTagCompound());
				solution = newSolution;
			}
			Solution.check(solution);
		}
		ScienceMod.snw.sendToAll(new MixerSolutionMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), solution.getTagCompound()));
		tanksUpdated[MIX_TANK_INDEX] = false;
	}

	/**
	 * Checks for boiling.
	 */
	private void checkBoil()
	{
		int boilAmount = Math.min((int) (Constants.BOIL_RATE * (machineHeat.getCurrentTemp() - Constants.BOIL_THRESHOLD + 1)), 
									tanks[MIX_TANK_INDEX].getFluidAmount());
		if (boilAmount > 0)
		{
			drainTank(new FluidStack(FluidRegistry.WATER, boilAmount), MIX_TANK_INDEX);
			machineHeat.transferHeat(-boilAmount * Constants.BOIL_HEAT_LOSS);
			ScienceMod.snw.sendToAll(new TETankMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), tanks[MIX_TANK_INDEX].getFluidAmount(), MIX_TANK_INDEX));
		}
	}
	
	/**
	 * Tries to add a mixture ItemStack to the mixer. Used for right-clicking on mixers with mixtures.
	 * 
	 * @param toAdd The mixture to add
	 * @return Whether the mixture was successfully added
	 */
	public boolean addMixture(ItemStack toAdd)
	{
		// Parse the item into a mixture, and check that it is one
		ItemStack stack = Mixture.parseItemStackMixture(toAdd);
		if (stack == null) return false;
		
		// Calculate how much can be added and add it
		int numToAdd = stack.stackSize;
		NBTTagList precipitatesToAdd = stack.getTagCompound().getTagList(NBTKeys.Chemical.PRECIPITATES, NBTTypes.COMPOUND);
		NBTTagList precipitateList = solution.getTagCompound().getTagList(NBTKeys.Chemical.PRECIPITATES, NBTTypes.COMPOUND);
		for (int i = 0; i < numToAdd; i ++)
		{
			precipitateList = NBTHelper.combineTagLists(precipitateList, precipitatesToAdd, NBTKeys.Chemical.PRECIPITATE, null, null, null,
					NBTKeys.Chemical.MOLS);
		}
		solution.getTagCompound().setTag(NBTKeys.Chemical.PRECIPITATES, precipitateList);
		
		// Check the resulting solution
		solution.getTagCompound().setBoolean(NBTKeys.Chemical.STABLE, false);
		check();
		return true;
	}
	
	/**
	 * Adds any mixtures in the input slot.
	 */
	private void addMixtures()
	{
		// Parse the item into a mixture, and check that it is one
		ItemStack stack = Mixture.parseItemStackMixture(allInventories[ITEM_INPUT_INDEX][0]);
		if (stack == null) return;
		
		// Find the number of available jar spaces
		int jarSpace = 0;
		if (allInventories[JAR_INV_INDEX][JAR_OUTPUT_INDEX] == null)
		{
			jarSpace = this.getInventoryStackLimit();
		}
		else if (allInventories[JAR_INV_INDEX][JAR_OUTPUT_INDEX].isItemEqual(new ItemStack(ScienceModItems.jar)))
		{
			jarSpace = this.getInventoryStackLimit() - allInventories[JAR_INV_INDEX][JAR_OUTPUT_INDEX].stackSize;
		}
		
		// Calculate how much can be added and add it
		int numToAdd = Math.min(jarSpace, stack.stackSize);
		NBTTagList precipitatesToAdd = stack.getTagCompound().getTagList(NBTKeys.Chemical.PRECIPITATES, NBTTypes.COMPOUND);
		NBTTagList precipitateList = solution.getTagCompound().getTagList(NBTKeys.Chemical.PRECIPITATES, NBTTypes.COMPOUND);
		for (int i = 0; i < numToAdd; i ++)
		{
			precipitateList = NBTHelper.combineTagLists(precipitateList, precipitatesToAdd, NBTKeys.Chemical.PRECIPITATE, null, null, null,
					NBTKeys.Chemical.MOLS);
		}
		solution.getTagCompound().setTag(NBTKeys.Chemical.PRECIPITATES, precipitateList);
		
		// Output jars and consume input
		if (allInventories[JAR_INV_INDEX][JAR_OUTPUT_INDEX] == null)
		{
			allInventories[JAR_INV_INDEX][JAR_OUTPUT_INDEX] = new ItemStack(ScienceModItems.jar, numToAdd);
		}
		else
		{
			allInventories[JAR_INV_INDEX][JAR_OUTPUT_INDEX].stackSize += numToAdd;
		}
		allInventories[ITEM_INPUT_INDEX][0].splitStack(numToAdd);
		
		// Check the resulting solution
		solution.getTagCompound().setBoolean(NBTKeys.Chemical.STABLE, false);
		check();
	}
	
	/**
	 * Tries to add a solution ItemStack to the mixer. Used for right clicking on mixers with solutions.
	 * 
	 * @param toAdd The solution to add
	 * @return Whether the solution was successfully added
	 */
	public boolean addSolution(ItemStack toAdd)
	{
		// Parse the stack into a solution, and check if it can be
		ItemStack stack = Solution.parseItemStackSolution(toAdd);
		if (stack == null) return false;
		
		// Find the amount of available tank space
		int tankSpace = tanks[MIX_TANK_INDEX].getCapacity() - tanks[MIX_TANK_INDEX].getFluidAmount();
		
		// Calculate how much can be added and add it
		int numToAdd = Math.min(stack.stackSize, tankSpace / 250);
		NBTTagList precipitatesToAdd = stack.getTagCompound().getTagList(NBTKeys.Chemical.PRECIPITATES, NBTTypes.COMPOUND);
		NBTTagList precipitateList = solution.getTagCompound().getTagList(NBTKeys.Chemical.PRECIPITATES, NBTTypes.COMPOUND);
		NBTTagList ionsToAdd = stack.getTagCompound().getTagList(NBTKeys.Chemical.IONS, NBTTypes.COMPOUND);
		NBTTagList ionList = solution.getTagCompound().getTagList(NBTKeys.Chemical.IONS, NBTTypes.COMPOUND);
		for (int i = 0; i < numToAdd; i ++)
		{
			precipitateList = NBTHelper.combineTagLists(precipitateList, precipitatesToAdd, NBTKeys.Chemical.PRECIPITATE, null, null, null,
					NBTKeys.Chemical.MOLS);
			ionList = NBTHelper.combineTagLists(ionList, ionsToAdd, NBTKeys.Chemical.ION, null, null, null, NBTKeys.Chemical.MOLS);
		}
		solution.getTagCompound().setTag(NBTKeys.Chemical.PRECIPITATES, precipitateList);
		solution.getTagCompound().setTag(NBTKeys.Chemical.IONS, ionList);
		
		this.fillAll(new FluidStack(FluidRegistry.WATER, 250 * numToAdd), MIX_TANK_INDEX);
		
		// Check the resulting solution
		solution.getTagCompound().setBoolean(NBTKeys.Chemical.STABLE, false);
		check();
		return true;
	}
	
	/**
	 * Adds any solutions in the input slot.
	 */
	private void addSolutions()
	{
		// Parse the stack into a solution, and check if it can be
		ItemStack stack = Solution.parseItemStackSolution(allInventories[ITEM_INPUT_INDEX][0]);
		if (stack == null) return;
		
		// Find the number of available jar spaces
		int jarSpace = 0;
		if (allInventories[JAR_INV_INDEX][JAR_OUTPUT_INDEX] == null)
		{
			jarSpace = this.getInventoryStackLimit();
		}
		else if (allInventories[JAR_INV_INDEX][JAR_OUTPUT_INDEX].isItemEqual(new ItemStack(ScienceModItems.jar)))
		{
			jarSpace = this.getInventoryStackLimit() - allInventories[JAR_INV_INDEX][JAR_OUTPUT_INDEX].stackSize;
		}
		if (jarSpace == 0) return;
		
		// Find the amount of available tank space
		int tankSpace = tanks[MIX_TANK_INDEX].getCapacity() - tanks[MIX_TANK_INDEX].getFluidAmount();
		
		// Calculate how much can be added and add it
		int numToAdd = Math.min(Math.min(jarSpace, stack.stackSize), tankSpace / 250);
		NBTTagList precipitatesToAdd = stack.getTagCompound().getTagList(NBTKeys.Chemical.PRECIPITATES, NBTTypes.COMPOUND);
		NBTTagList precipitateList = solution.getTagCompound().getTagList(NBTKeys.Chemical.PRECIPITATES, NBTTypes.COMPOUND);
		NBTTagList ionsToAdd = stack.getTagCompound().getTagList(NBTKeys.Chemical.IONS, NBTTypes.COMPOUND);
		NBTTagList ionList = solution.getTagCompound().getTagList(NBTKeys.Chemical.IONS, NBTTypes.COMPOUND);
		for (int i = 0; i < numToAdd; i ++)
		{
			precipitateList = NBTHelper.combineTagLists(precipitateList, precipitatesToAdd, NBTKeys.Chemical.PRECIPITATE, null, null, null,
					NBTKeys.Chemical.MOLS);
			ionList = NBTHelper.combineTagLists(ionList, ionsToAdd, NBTKeys.Chemical.ION, null, null, null, NBTKeys.Chemical.MOLS);
		}
		solution.getTagCompound().setTag(NBTKeys.Chemical.PRECIPITATES, precipitateList);
		solution.getTagCompound().setTag(NBTKeys.Chemical.IONS, ionList);
		
		// Output jars and fluid and consume input
		if (allInventories[JAR_INV_INDEX][JAR_OUTPUT_INDEX] == null)
		{
			allInventories[JAR_INV_INDEX][JAR_OUTPUT_INDEX] = new ItemStack(ScienceModItems.jar, numToAdd);
		}
		else
		{
			allInventories[JAR_INV_INDEX][JAR_OUTPUT_INDEX].stackSize += numToAdd;
		}
		allInventories[ITEM_INPUT_INDEX][0].splitStack(numToAdd);
		this.fillAll(new FluidStack(FluidRegistry.WATER, 250 * numToAdd), MIX_TANK_INDEX);
		
		// Check the resulting solution
		solution.getTagCompound().setBoolean(NBTKeys.Chemical.STABLE, false);
		check();
	}
	
	@Override
	protected boolean hasIngredients(MachineRecipe recipeToUse)
	{
		// null check
		if (recipeToUse == null) return false;
		
		// If the recipe cannot use the input, the attempt fails.
		if (!recipeToUse.canProcess(allInventories[JAR_INV_INDEX][JAR_INPUT_INDEX], tanks[MIX_TANK_INDEX].getFluid()) || isEmpty())
			return false;
		
		return true;
	}
	
	/**
	 * Determines whether the mixer's tank is empty.
	 * 
	 * @return Whether the tank is empty
	 */
	protected boolean isEmpty()
	{
		if (tanks[MIX_TANK_INDEX].getFluidAmount() > 0) return false;
		
		NBTTagList precipitateList = solution.getTagCompound().getTagList(NBTKeys.Chemical.PRECIPITATES, NBTTypes.COMPOUND);
		
		if (precipitateList.hasNoTags()) return true;
		return false;
	}
	
	/**
	 * Input consumption is done in doOutput for mixers.
	 */
	@Override
	protected void consumeInputs(MachineRecipe recipe)
	{	
		
	}
	
	/**
	 * Do output and consume the input.
	 */
	@Override
	protected void doOutput(MachineRecipe recipe)
	{
		// Null check
		if (allInventories[JAR_INPUT_INDEX] == null || allInventories[JAR_INV_INDEX][JAR_INPUT_INDEX] == null || !allInventories[JAR_INV_INDEX][JAR_INPUT_INDEX].isItemEqual(new ItemStack(ScienceModItems.jar))) return;
		
		NBTTagList ionList = (NBTTagList) solution.getTagCompound().getTagList(NBTKeys.Chemical.IONS, NBTTypes.COMPOUND).copy();
		NBTTagList precipitateList = (NBTTagList) solution.getTagCompound().getTagList(NBTKeys.Chemical.PRECIPITATES, NBTTypes.COMPOUND).copy();
		if (ionList.hasNoTags() && tanks[MIX_TANK_INDEX].getFluidAmount() >= 250)
		{
			// If there are no ions and some fluid, output water
			if (allInventories[OUTPUT_INDEX][0] == null)
			{
				drainTank(new FluidStack(FluidRegistry.WATER, 250), MIX_TANK_INDEX);
				allInventories[JAR_INV_INDEX][JAR_INPUT_INDEX].splitStack(1);
				allInventories[OUTPUT_INDEX][0] = CommonCompounds.getWater(1);
			}
			else if (allInventories[OUTPUT_INDEX][0].isItemEqual(CommonCompounds.water))
			{
				drainTank(new FluidStack(FluidRegistry.WATER, 250), MIX_TANK_INDEX);
				allInventories[JAR_INV_INDEX][JAR_INPUT_INDEX].splitStack(1);
				allInventories[OUTPUT_INDEX][0].stackSize ++;
			}
		}
		else if (tanks[MIX_TANK_INDEX].getFluidAmount() >= 250)
		{
			// If there is both fluid and ions, output a solution
			
			// Calculate what fraction of the solution is outputted
			int[] outMultiplier = MathUtil.parseFrac(250. / tanks[MIX_TANK_INDEX].getFluidAmount());
			
			// Calculate the output and leftover ions
			NBTTagList outputIons = (NBTTagList) ionList.copy();
			int[][] molsLeft = new int[ionList.tagCount()][];
			for (int i = 0; i < ionList.tagCount(); i ++)
			{
				int[] prevMols = ionList.getCompoundTagAt(i).getIntArray(NBTKeys.Chemical.MOLS);
				int[] outMols = MathUtil.multFrac(prevMols, outMultiplier);
				molsLeft[i] = MathUtil.multFrac(prevMols, new int[] { outMultiplier[1] - outMultiplier[0], outMultiplier[1] });
				
				outputIons.getCompoundTagAt(i).setIntArray(NBTKeys.Chemical.MOLS, outMols);
			}
			
			// Create the output stack
			NBTTagCompound outputTag = new NBTTagCompound();
			outputTag.setTag(NBTKeys.Chemical.IONS, outputIons);
			outputTag.setTag(NBTKeys.Chemical.PRECIPITATES, new NBTTagList());
			ItemStack output = new ItemStack(ScienceModItems.solution);
			output.setTagCompound(outputTag);
			Solution.check(output);
			
			// Output the solution and consume jars and fluid
			if (allInventories[OUTPUT_INDEX][0] == null)
			{
				drainTank(new FluidStack(FluidRegistry.WATER, 250), MIX_TANK_INDEX);
				allInventories[JAR_INV_INDEX][JAR_INPUT_INDEX].splitStack(1);
				allInventories[OUTPUT_INDEX][0] = output;
			}
			else if (ItemStack.areItemStackTagsEqual(allInventories[OUTPUT_INDEX][0], output) && allInventories[OUTPUT_INDEX][0].stackSize < allInventories[OUTPUT_INDEX][0].getMaxStackSize())
			{
				drainTank(new FluidStack(FluidRegistry.WATER, 250), MIX_TANK_INDEX);
				allInventories[JAR_INV_INDEX][JAR_INPUT_INDEX].splitStack(1);
				allInventories[OUTPUT_INDEX][0].stackSize += 1;
			}
			else return;
			
			for (int i = 0; i < ionList.tagCount(); i++)
			{
				ionList.getCompoundTagAt(i).setIntArray(NBTKeys.Chemical.MOLS, molsLeft[i]);
			}
			solution.getTagCompound().setTag(NBTKeys.Chemical.IONS, ionList);
		}
		else if (!precipitateList.hasNoTags())
		{
			// If there is no fluid, but there are precipitates, scoop them up
			
			// Calculate the fraction of precipitate scooped
			double mols = 0;
			for (int i = 0; i < precipitateList.tagCount(); i ++)
			{
				mols += MathUtil.parseFrac(precipitateList.getCompoundTagAt(i).getIntArray(NBTKeys.Chemical.MOLS));
			}
			int[] outMultiplier;
			if (mols >= 10)
				outMultiplier = MathUtil.parseFrac(10. / mols);
			else
				outMultiplier = new int[] { 1, 1 };
			
			// Calculate the output and leftover precipitates
			NBTTagList outputPrecipitates = (NBTTagList) precipitateList.copy();
			int[][] molsLeft = new int[precipitateList.tagCount()][];
			for (int i = 0; i < precipitateList.tagCount(); i ++)
			{
				int[] prevMols = precipitateList.getCompoundTagAt(i).getIntArray(NBTKeys.Chemical.MOLS);
				int[] outMols = MathUtil.multFrac(prevMols, outMultiplier);
				molsLeft[i] = MathUtil.multFrac(prevMols, new int[] { outMultiplier[1] - outMultiplier[0], outMultiplier[1] });
				
				outputPrecipitates.getCompoundTagAt(i).setIntArray(NBTKeys.Chemical.MOLS, outMols);
			}
			
			// Create the output item
			NBTTagCompound outputTag = new NBTTagCompound();
			outputTag.setTag(NBTKeys.Chemical.PRECIPITATES, outputPrecipitates);
			ItemStack output = new ItemStack(ScienceModItems.mixture);
			output.setTagCompound(outputTag);
			Mixture.check(output);
			
			ItemStack unparsedOut = Mixture.unparseItemStackMixture(output);
			if (unparsedOut != null) output = unparsedOut;
			
			// Do the output and consume jars
			if (allInventories[OUTPUT_INDEX][0] == null)
			{
				allInventories[JAR_INV_INDEX][JAR_INPUT_INDEX].splitStack(1);
				allInventories[OUTPUT_INDEX][0] = output;
			}
			else if (ItemStack.areItemStacksEqual(allInventories[OUTPUT_INDEX][0], output) && allInventories[OUTPUT_INDEX][0].stackSize > allInventories[OUTPUT_INDEX][0].getMaxStackSize())
			{
				allInventories[JAR_INV_INDEX][JAR_INPUT_INDEX].splitStack(1);
				allInventories[OUTPUT_INDEX][0].stackSize += 1;
			}
			else return;
			
			for (int i = 0; i < precipitateList.tagCount(); i++)
			{
				precipitateList.getCompoundTagAt(i).setIntArray(NBTKeys.Chemical.MOLS, molsLeft[i]);
			}
			solution.getTagCompound().setTag(NBTKeys.Chemical.PRECIPITATES, precipitateList);
		}
		
		// Check everything
		check();
		InventoryHelper.checkEmptyStacks(allInventories);
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
		
		// Read solution from tag
		NBTTagCompound solutionTag = new NBTTagCompound();
		NBTTagList ionList = tag.getTagList(NBTKeys.Chemical.IONS, NBTTypes.COMPOUND);
		NBTTagList precipitateList = tag.getTagList(NBTKeys.Chemical.PRECIPITATES, NBTTypes.COMPOUND);
		solutionTag.setTag(NBTKeys.Chemical.IONS, ionList);
		solutionTag.setTag(NBTKeys.Chemical.PRECIPITATES, precipitateList);
		solutionTag.setBoolean(NBTKeys.Chemical.STABLE, false);
		if (solutionTag.getTagList(Chemical.IONS, NBTTypes.COMPOUND).tagCount() == 0) solution = new ItemStack(ScienceModItems.mixture);
		else solution = new ItemStack(ScienceModItems.solution);
		solution.setTagCompound(solutionTag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		// Write solution to tag
		tag.setTag(NBTKeys.Chemical.IONS, solution.getTagCompound().getTagList(NBTKeys.Chemical.IONS, NBTTypes.COMPOUND));
		tag.setTag(NBTKeys.Chemical.PRECIPITATES, solution.getTagCompound().getTagList(NBTKeys.Chemical.PRECIPITATES, NBTTypes.COMPOUND));
		tag.setBoolean(NBTKeys.Chemical.STABLE, solution.getTagCompound().getBoolean(NBTKeys.Chemical.STABLE));
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		if (index == JAR_INPUT_INDEX && !stack.getIsItemStackEqual(new ItemStack(ScienceModItems.jar, 1))) return false;
		return true;
	}
	
	/**
	 * @return The List of ion Strings
	 */
	public List<String> getIonList()
	{
		return ionList;
	}
	
	/**
	 * Sets the List of ion Strings. This is only used on the client side.
	 * 
	 * @param ionList The List of ion Strings
	 */
	public void setIonList(List<String> ionList)
	{
		// Only allowed on the client side
		if (!this.worldObj.isRemote) return;
		this.ionList = ionList;
	}
	
	/**
	 * @return The List of precipitate Strings
	 */
	public List<String> getPrecipitateList()
	{
		return precipitateList;
	}
	
	/**
	 * Sets the List of precipitate Strings. This is only used on the client side.
	 * 
	 * @param precipitateList The List of precipitate Strings
	 */
	public void setPrecipitateList(List<String> precipitateList)
	{
		// Only allowed on the client side
		if (!this.worldObj.isRemote) return;
		this.precipitateList = precipitateList;
	}
	
	/**
	 * Enum for mixer recipes.
	 * 
	 * @author JasonILTG and syy1125
	 */
	public enum MixerRecipe implements MachineRecipe
	{
		Solution(20, 1, new FluidStack(FluidRegistry.WATER, 250), new ItemStack[] { new ItemStack(ScienceModItems.solution) }),
		Mixture(20, 1, null, new ItemStack[] { new ItemStack(ScienceModItems.mixture) });
		
		/** The time required */
		public final int timeReq;
		/** The number of jars required */
		public final int reqJarCount;
		/** The FluidStack input required */
		public final FluidStack reqFluidStack;
		/** The ItemStack outputs */
		public final ItemStack[] outItemStack;
		
		/**
		 * Constructor.
		 * 
		 * @param timeRequired The time required
		 * @param requiredJarCount The number of jars required
		 * @param requiredFluidStack The FluidStack input required
		 * @param outputItemStacks The ItemStack outputs
		 */
		private MixerRecipe(int timeRequired, int requiredJarCount, FluidStack requiredFluidStack,
				ItemStack[] outputItemStacks)
		{
			timeReq = timeRequired;
			reqJarCount = requiredJarCount;
			reqFluidStack = requiredFluidStack;
			outItemStack = outputItemStacks;
		}
		
		/**
		 * Determines whether there are enough jars.
		 * 
		 * @param inputJarStack The jar ItemStack input
		 * @return Whether there are enough jars
		 */
		private boolean hasJars(ItemStack inputJarStack)
		{
			if (reqJarCount == 0) return true;
			if (inputJarStack == null) return false;
			return inputJarStack.stackSize >= reqJarCount;
		}
		
		/**
		 * Determines whether the required FluidStack is present.
		 * 
		 * @param inputFluidStack The FluidStack input
		 * @return Whether the required FluidStack is present
		 */
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
		 * @param params Input format: jar input stack, item input stack, fluid input stack
		 */
		@Override
		public boolean canProcess(Object... params)
		{
			ItemStack inputJarStack = (ItemStack) params[0];
			FluidStack inputFluidStack = (FluidStack) params[1];
			return hasJars(inputJarStack) && hasFluid(inputFluidStack);
		}
		
		@Override
		public ItemStack[] getItemOutputs()
		{
			return outItemStack;
		}
		
		@Override
		public int getTimeRequired()
		{
			return timeReq;
		}
	}
	
	@Override
	public void sendInfo()
	{
		if (this.worldObj.isRemote) return;

		super.sendInfo();
		
		ScienceMod.snw.sendToAll(new MixerSolutionMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), solution.getTagCompound()));
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#removeStackFromSlot(int)
	 */
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
