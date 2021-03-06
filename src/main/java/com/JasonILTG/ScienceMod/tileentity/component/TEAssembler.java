package com.JasonILTG.ScienceMod.tileentity.component;

import com.JasonILTG.ScienceMod.init.ScienceModBlocks;
import com.JasonILTG.ScienceMod.init.ScienceModItems;
import com.JasonILTG.ScienceMod.reference.NBTKeys;
import com.JasonILTG.ScienceMod.reference.chemistry.compounds.CommonCompounds;
import com.JasonILTG.ScienceMod.tileentity.general.ITileEntityGUI;
import com.JasonILTG.ScienceMod.tileentity.general.TEInventory;
import com.JasonILTG.ScienceMod.util.InventoryHelper;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

/**
 * <code>TileEntity</code> class for assemblers.
 * 
 * @author JasonILTG and syy1125
 */
public class TEAssembler extends TEInventory implements ITickable, ITileEntityGUI
{
	public static final String NAME = "Assembler";

	public static final int INPUT_INV_INDEX = 0;
	public static final int OUTPUT_INV_INDEX = 1;
	public static final int INPUT_INV_SIZE = 9;
	public static final int OUTPUT_INV_SIZE = 1;
	
	/** The current recipe */
	AssemblerRecipe currentRecipe;
	
	/** Whether or not to update this tick */
	public boolean toUpdate;
	
	/**
	 * Default constructor.
	 */
	public TEAssembler()
	{
		super(NAME, new int[]{ INPUT_INV_SIZE, OUTPUT_INV_SIZE }, 0);
		toUpdate = true;
	}

	@Override
	public void update()
	{
		if (!worldObj.isRemote && toUpdate)
		{
			currentRecipe = null;
			allInventories[OUTPUT_INV_INDEX][0] = null;
			for (AssemblerRecipe recipe : AssemblerRecipe.values())
			{
				if (recipe.canProcess(allInventories[INPUT_INV_INDEX]) > 0)
				{
					currentRecipe = recipe;
					allInventories[OUTPUT_INV_INDEX][0] = recipe.generateTaggedOutput(allInventories[INPUT_INV_INDEX]);
				}
			}
			toUpdate = false;
			super.update();
		}
	}
	
	/**
	 * Marks the assembler to be updated.
	 */
	public void markForUpdate()
	{
		toUpdate = true;
	}
	
	/**
	 * Consumes the inputs of the current recipe and produces the outputs if possible.
	 * 
	 * @param playerInv The player's inventory
	 * @param doAll Whether or not to process as much as possible (i.e. shift click)
	 */
	public void process(IInventory playerInv, boolean doAll)
	{
		if (currentRecipe != null)
		{
			currentRecipe.process(allInventories[INPUT_INV_INDEX], playerInv, doAll);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		toUpdate = true;
		currentRecipe = null;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
	}
	
	/**
	 * Enum for assembler recipes.
	 * 
	 * @author JasonILTG and syy1125
	 */
	public enum AssemblerRecipe
	{
		PowerBlock(new ItemStack[]{
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot),
				new ItemStack(ScienceModBlocks.wire), new ItemStack(ScienceModItems.battery), new ItemStack(ScienceModBlocks.wire),
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot),
		}, new ItemStack(ScienceModItems.powerBlock), new String[][]{
			null, null, null,
			new String[]{ NBTKeys.Item.Component.WIRE_IN }, new String[]{ NBTKeys.Item.Component.BATTERY }, new String[]{ NBTKeys.Item.Component.WIRE_OUT },
			null, null, null
		}, new String[]{ NBTKeys.Item.Component.WIRE_IN, NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.WIRE_OUT }),
		
		PowerBlockIn(new ItemStack[]{
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot),
				new ItemStack(ScienceModBlocks.wire), new ItemStack(ScienceModItems.battery), new ItemStack(Items.iron_ingot),
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot),
		}, new ItemStack(ScienceModItems.powerBlock), new String[][]{
			null, null, null,
			new String[]{ NBTKeys.Item.Component.WIRE_IN }, new String[]{ NBTKeys.Item.Component.BATTERY }, null,
			null, null, null
		}, new String[]{ NBTKeys.Item.Component.WIRE_IN, NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.WIRE_OUT }),
		
		PowerBlockOut(new ItemStack[]{
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot),
				new ItemStack(Items.iron_ingot), new ItemStack(ScienceModItems.battery), new ItemStack(ScienceModBlocks.wire),
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot),
		}, new ItemStack(ScienceModItems.powerBlock), new String[][]{
				null, null, null,
				null, new String[]{ NBTKeys.Item.Component.BATTERY }, new String[]{ NBTKeys.Item.Component.WIRE_OUT },
				null, null, null
		}, new String[]{ NBTKeys.Item.Component.WIRE_IN, NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.WIRE_OUT }),
		
		Electrolyzer(new ItemStack[]{
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot),
				new ItemStack(ScienceModItems.powerBlock), CommonCompounds.getWater(1), new ItemStack(ScienceModItems.hull),
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot)
		}, new ItemStack(ScienceModBlocks.electrolyzer), new String[][]{
				null, null, null,
				new String[]{ NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.WIRE_IN }, null, new String[]{ NBTKeys.Item.Component.HULL },
				null, null, null
		}, new String[]{ NBTKeys.Item.Component.WIRE_IN, NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.HULL }),

		AirExtractor(new ItemStack[]{
				new ItemStack(Items.iron_ingot), new ItemStack(Blocks.hopper), new ItemStack(Items.iron_ingot),
				new ItemStack(ScienceModItems.powerBlock), new ItemStack(ScienceModItems.jar), new ItemStack(ScienceModItems.hull),
				new ItemStack(Items.iron_ingot), new ItemStack(Blocks.chest), new ItemStack(Items.iron_ingot)
		}, new ItemStack(ScienceModBlocks.air_extractor), new String[][]{
				null, null, null,
				new String[]{ NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.WIRE_IN }, null, new String[]{ NBTKeys.Item.Component.HULL },
				null, null, null
		}, new String[]{ NBTKeys.Item.Component.WIRE_IN, NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.HULL }),

		Condenser(new ItemStack[]{
				new ItemStack(Items.iron_ingot), new ItemStack(Blocks.hopper), new ItemStack(Items.iron_ingot),
				new ItemStack(ScienceModItems.powerBlock), new ItemStack(Items.bucket), new ItemStack(ScienceModItems.hull),
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot)
		}, new ItemStack(ScienceModBlocks.condenser), new String[][]{
				null, null, null,
				new String[]{ NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.WIRE_IN }, null, new String[]{ NBTKeys.Item.Component.HULL },
				null, null, null
		}, new String[]{ NBTKeys.Item.Component.WIRE_IN, NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.HULL }),

		ChemReactor(new ItemStack[]{
				new ItemStack(Items.iron_ingot), new ItemStack(Blocks.furnace), new ItemStack(Items.iron_ingot),
				new ItemStack(ScienceModItems.jar), new ItemStack(ScienceModItems.powerBlock), new ItemStack(ScienceModItems.hull),
				new ItemStack(Items.iron_ingot), new ItemStack(ScienceModItems.jar), new ItemStack(Items.iron_ingot)
		}, new ItemStack(ScienceModBlocks.chemical_reactor), new String[][]{
			null, null, null,
			new String[]{ NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.WIRE_IN }, null, new String[]{ NBTKeys.Item.Component.HULL },
			null, null, null
		}, new String[]{ NBTKeys.Item.Component.WIRE_IN, NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.HULL }),
		
		Mixer(new ItemStack[]{
				new ItemStack(Items.iron_ingot), new ItemStack(Blocks.hopper), new ItemStack(Items.iron_ingot),
				new ItemStack(Items.iron_ingot), new ItemStack(ScienceModItems.jar), new ItemStack(ScienceModItems.hull),
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot)
		}, new ItemStack(ScienceModBlocks.mixer), new String[][]{
				null, null, null,
				null, null, new String[]{ NBTKeys.Item.Component.HULL },
				null, null, null
		}, new String[]{ NBTKeys.Item.Component.WIRE_IN, NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.HULL }),
		
		Drain(new ItemStack[]{
				new ItemStack(Items.iron_ingot), null, new ItemStack(Items.iron_ingot),
				new ItemStack(Items.iron_ingot), new ItemStack(ScienceModItems.jar), new ItemStack(Items.iron_ingot),
				new ItemStack(Items.iron_ingot), new ItemStack(Blocks.iron_bars), new ItemStack(Items.iron_ingot)
		}, new ItemStack(ScienceModBlocks.drain), new String[][]{
				null, null, null,
				null, null, null,
				null, null, null
		}, new String[]{ NBTKeys.Item.Component.WIRE_IN, NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.HULL }),
		
		Combuster(new ItemStack[]{
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot),
				new ItemStack(ScienceModItems.hull), new ItemStack(Blocks.furnace), new ItemStack(ScienceModItems.powerBlock),
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot)
		}, new ItemStack(ScienceModBlocks.combuster), new String[][]{
				null, null, null,
				new String[]{ NBTKeys.Item.Component.HULL }, null, new String[]{ NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.WIRE_OUT },
				null, null, null
		}, new String[]{ NBTKeys.Item.Component.WIRE_OUT, NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.HULL }),

		SolarPanel(new ItemStack[]{
				new ItemStack(Items.iron_ingot), new ItemStack(Blocks.glass), new ItemStack(Items.iron_ingot),
				new ItemStack(ScienceModItems.hull), new ItemStack(Blocks.daylight_detector), new ItemStack(ScienceModItems.powerBlock),
				new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot)
		}, new ItemStack(ScienceModBlocks.solar_panel), new String[][]{
				null, null, null,
				new String[]{ NBTKeys.Item.Component.HULL }, null, new String[]{ NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.WIRE_OUT },
				null, null, null
		}, new String[]{ NBTKeys.Item.Component.WIRE_OUT, NBTKeys.Item.Component.BATTERY, NBTKeys.Item.Component.HULL })
		;
		
		/** The required input <code>ItemStack</code>s */
		public final ItemStack[] inputItems;
		/** The output <code>ItemStack</code> */
		public final ItemStack outputItem;
		/** The keys of the required NBT tags on input items */
		public final String[][] inKeys;
		/** The keys of the NBT tags on the output item */
		public final String[] outKeys;
		
		/**
		 * Constructor.
		 * 
		 * @param input The required input <code>ItemStack</code>s
		 * @param output The output <code>ItemStack</code>
		 * @param NBTInKeys The keys of the required NBT tags on input items
		 * @param NBTOutKeys The keys of the NBT tags on the output item
		 */
		private AssemblerRecipe(ItemStack[] input, ItemStack output, String[][] NBTInKeys, String[] NBTOutKeys)
		{
			inputItems = input;
			outputItem = output;
			inKeys = NBTInKeys;
			outKeys = NBTOutKeys;
		}
		
		/**
		 * Determines the amount that can be processed from the given input.
		 * 
		 * @param in The input <code>ItemStack</code>s
		 * @return the amount that can be processed
		 */
		public int canProcess(ItemStack[] in)
		{
			if (in == null || in.length != inputItems.length) return 0;
			
			int minSize = Integer.MAX_VALUE;
			for (int i = 0; i < inputItems.length; i++)
			{
				if (inputItems[i] == null) continue;
				if (in[i] == null) return 0;
				if (inputItems[i].getItem() != in[i].getItem()) return 0;
				if (in[i].stackSize < minSize) minSize = in[i].stackSize;
			}
			return minSize;
		}
		
		/**
		 * Generates the output <code>ItemStack</code> with the proper NBT tags to be displayed
		 * 
		 * @param in The input <code>ItemStack</code>s
		 * @return The output <code>ItemStack</code>
		 */
		public ItemStack generateTaggedOutput(ItemStack[] in)
		{
			ItemStack taggedOut = outputItem.copy();
			NBTTagCompound tag = new NBTTagCompound();
			for (int i = 0; i < outKeys.length; i++)
			{
				tag.setTag(outKeys[i], new NBTTagCompound());
			}
			for (int i = 0; i < in.length; i++)
			{
				if (inKeys[i] != null && in[i] != null && in[i].getTagCompound() != null)
				{
					for (String key : inKeys[i])
					{
						tag.setTag(key, in[i].getTagCompound().getTag(key));
					}
				}
			}
			taggedOut.setTagCompound(tag);
			return taggedOut;
		}
		
		/**
		 * Attempts to process the input <code>ItemStack</code> and output the output <code>ItemStack</code>(s).
		 * 
		 * @param in The input <code>ItemStack</code>s
		 * @param playerInv The player's inventory
		 * @param doAll Whether to do as many as possible
		 * @return Whether processing was successful
		 */
		public boolean process(ItemStack[] in, IInventory playerInv, boolean doAll)
		{
			int numToDo = canProcess(in);
			if (numToDo == 0) return false;
			if (!doAll) numToDo = 1;
			
			ItemStack[] playerStacks = new ItemStack[playerInv.getSizeInventory()];
			for (int i = 0; i < playerStacks.length; i++)
			{
				playerStacks[i] = playerInv.getStackInSlot(i);
			}
			
			int numDone = 0;
			ItemStack taggedOut = generateTaggedOutput(in);
			while (numDone < numToDo)
			{
				ItemStack[] pattern = InventoryHelper.findInsertPattern(taggedOut, playerStacks);
				if (pattern == null) break;
				playerStacks = InventoryHelper.mergeStackArrays(pattern, playerStacks);
				numDone++;
			}
			
			if (numDone == 0) return false;
			
			for (int i = 0; i < in.length; i++)
			{
				if (in[i].stackSize == numDone) in[i] = null;
				else in[i].stackSize -= numDone;
			}
			
			return true;
		}
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
