package com.JasonILTG.ScienceMod.tileentity.general;

import com.JasonILTG.ScienceMod.ScienceMod;
import com.JasonILTG.ScienceMod.messages.TETankMessage;
import com.JasonILTG.ScienceMod.reference.NBTKeys;
import com.JasonILTG.ScienceMod.util.InventoryHelper;
import com.JasonILTG.ScienceMod.util.NBTHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Wrapper class for all tile entities with inventories.
 * 
 * @author JasonILTG and syy1125
 */
public abstract class TEInventory extends TEScience implements IInventory, ISidedInventory, ITickable
{
	/** The custom name of the tile entity. */
	protected String customName;
	
	/** The 2D inventory array */
	protected ItemStack[][] allInventories;
	/** The default number of inventories */
	protected int defaultInvCount;
	/** The sizes of the different inventories */
	protected int[] invSizes;
	public static final int NO_INV_SIZE = 0;
	
	/** The number of tanks the generator has */
	protected int numTanks;
	public static final int DEFAULT_TANK_CAPACITY = 10000;
	/** The machine's tanks (null if there is none) */
	protected FluidTank[] tanks;
	/** Whether the tank is updated on the client side */
	protected boolean[] tanksUpdated;

	protected int[][] sidedAccess;

	protected static final int BOTTOM = 0;
	protected static final int TOP = 1;
	protected static final int FRONT = 2;
	protected static final int BACK = 3;
	protected static final int LEFT = 4;
	protected static final int RIGHT = 5;
	
	protected EnumFacing frontFacingSide;
	protected EnumFacing topFacingSide;
	
	/**
	 * Constructor.
	 * 
	 * @param name The custom name of the tile entity
	 * @param inventorySizes The sizes of the different inventories
	 * @param numTanks The number of tanks
	 */
	public TEInventory(String name, int[] inventorySizes, int numTanks)
	{
		customName = name;
		
		// Inventory
		invSizes = inventorySizes;
		defaultInvCount = inventorySizes.length;
		allInventories = new ItemStack[inventorySizes.length][];
		for (int i = 0; i < allInventories.length; i ++) {
			allInventories[i] = new ItemStack[inventorySizes[i]];
		}
		
		this.numTanks = numTanks;
		if (numTanks > 0)
		{
			tanks = new FluidTank[numTanks];
			tanksUpdated = new boolean[numTanks];
			for (int i = 0; i < numTanks; i ++)
			{
				tanks[i] = new FluidTank(DEFAULT_TANK_CAPACITY);
				tanksUpdated[i] = false;
			}
		}
		
		sidedAccess = new int[6][];
		int totalInvSize = this.getSizeInventory();
		for (int i = 0; i < sidedAccess.length; i++)
		{
			sidedAccess[i] = new int[totalInvSize];
			for (int j = 0; j < totalInvSize; j++) sidedAccess[i][j] = j;
		}
		
		frontFacingSide = EnumFacing.NORTH;
		topFacingSide = EnumFacing.UP;
	}
	
	/**
	 * @return The custom name of the entity
	 */
	public String getCustomName()
	{
		return customName;
	}
	
	/**
	 * Sets the custom name of the tile entity.
	 * 
	 * @param name The custom name
	 */
	public void setCustomName(String name)
	{
		customName = name;
	}
	
	@Override
	public String getName()
	{
		return this.hasCustomName() ? this.customName : "container.inventory_tile_entity";
	}
	
	@Override
	public boolean hasCustomName()
	{
		return this.customName != null && !this.customName.equals("");
	}
	
	@Override
	public IChatComponent getDisplayName()
	{
		return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getTileEntity(this.getPos()) == this && player.getDistanceSq(this.pos.add(0.5, 0.5, 0.5)) <= 64;
	}
	
	@Override
	public void openInventory(EntityPlayer player)
	{	
		
	}
	
	@Override
	public void closeInventory(EntityPlayer player)
	{	
		
	}
	
	/**
	 * Updates the machine every tick.
	 */
	@Override
	public void update()
	{
		if (this.worldObj.isRemote) return;
		
		// Common actions
		checkFields();
		
		// Update tanks on client side if needed
		for (int i = 0; i < numTanks; i ++)
		{
			if (numTanks > 0 && !tanksUpdated[i])
			{
				ScienceMod.snw.sendToAll(new TETankMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.getFluidAmount(i), i));
				tanksUpdated[i] = true;
			}
		}
	}
	
	@Override
	public int getSizeInventory()
	{
		int inventorySize = 0;
		for (ItemStack[] inv : allInventories)
			inventorySize += inv.length;
		return inventorySize;
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		for (ItemStack[] inventory : allInventories)
		{
			if (index >= inventory.length) {
				index -= inventory.length;
			}
			else {
				return inventory[index];
			}
		}
		
		// Default return.
		return null;
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		ItemStack stack = getStackInSlot(index);
		
		if (stack != null)
		{
			if (count >= stack.stackSize) {
				// The action will deplete the stack.
				setInventorySlotContents(index, null);
			}
			else {
				// The action should not deplete the stack
				stack = stack.splitStack(count);
			}
		}
		
		return stack;
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		for (ItemStack[] inventory : allInventories)
		{
			if (index >= inventory.length) {
				index -= inventory.length;
			}
			else {
				inventory[index] = stack;
				return;
			}
		}
	}
	
	/**
	 * Returns the index in the 2D inventory array given the slot index.
	 * 
	 * @param index The slot index
	 * @return The index in inventory
	 */
	public int getInvIndexBySlotIndex(int index)
	{
		for (int i = 0; i < allInventories.length; i ++)
		{
			if (index >= allInventories[i].length) {
				index -= allInventories[i].length;
			}
			else {
				return i;
			}
		}
		
		return allInventories.length;
	}
	
	/**
	 * Attempts to insert the given fluid into the specified tank.
	 * 
	 * @param fluid The fluid to insert
	 * @param tankIndex The index of the tank
	 * @return Whether the fluid can be inserted into the specified tank
	 */
	public boolean fillAll(FluidStack fluid, int tankIndex)
	{
		if (tankIndex >= numTanks || tankIndex < 0) return false;
		
		// If tank cannot hold the input fluid, then don't do input.
		if (tanks[tankIndex].getCapacity() - tanks[tankIndex].getFluidAmount() < fluid.amount) return false;
		
		tanks[tankIndex].fill(fluid, true);
		tanksUpdated[tankIndex] = false;
		return true;
	}
	
	/**
	 * Attempts to drain the given fluid from the specified tank.
	 * 
	 * @param fluid The fluid to drain
	 * @param tankIndex The index of the tank
	 * @return Whether the fluid can be drained from the specified tank
	 */
	public boolean drainTank(FluidStack fluid, int tankIndex)
	{
		if (tankIndex >= numTanks || tankIndex < 0) return false;
		
		// If tank doesn't have enough fluid, don't drain
		if (tanks[tankIndex].getFluidAmount() < fluid.amount) return false;
		
		tanks[tankIndex].drain(fluid.amount, true);
		tanksUpdated[tankIndex] = false;
		return true;
	}
	
	/**
	 * @param tankIndex The index of the tank
	 * @return The specified tank's capacity (0 if there is no tank at the specified index)
	 */
	public int getTankCapacity(int tankIndex)
	{
		if (tankIndex >= numTanks || tankIndex < 0) return 0;
		return tanks[tankIndex].getCapacity();
	}
	
	/**
	 * @param tankIndex The index of the tank
	 * @return The FluidStack in the specified tank (null if there is no tank at the specified index)
	 */
	public FluidStack getFluidInTank(int tankIndex)
	{
		if (tankIndex >= numTanks || tankIndex < 0) return null;
		return tanks[tankIndex].getFluid();
	}
	
	/**
	 * 
	 * @param tankIndex The index of the tank
	 * @return The amount of fluid in the tank at the specified index (0 if there is no tank at the specified index)
	 */
	public int getFluidAmount(int tankIndex)
	{
		if (tankIndex >= numTanks || tankIndex < 0) return 0;
		checkFields();
		return tanks[tankIndex].getFluidAmount();
	}
	
	/**
	 * Sets the amount of fluid in the tank at the specified index. Used only on the client side.
	 * 
	 * @param amount The amount of fluid
	 * @param tankIndex The index of the tank
	 */
	public void setFluidAmount(int amount, int tankIndex)
	{
		// Only allowed on the client side
		if (!this.worldObj.isRemote) return;
		if (numTanks == 0) return;
		if (tankIndex >= numTanks || tankIndex < 0) return;
		checkFields();
		if (tanks[tankIndex].getFluid() == null)
			tanks[tankIndex].setFluid(new FluidStack(FluidRegistry.WATER, amount));
		else
			tanks[tankIndex].getFluid().amount = amount;
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int getField(int id)
	{
		return 0;
	}
	
	@Override
	public void setField(int id, int value)
	{	
		
	}
	
	@Override
	public int getFieldCount()
	{
		return 0;
	}
	
	/**
	 * Checks for and initializes any null inventories or tanks.
	 */
	public void checkFields()
	{
		if (allInventories == null) allInventories = new ItemStack[defaultInvCount][];
		
		for (int i = 0; i < allInventories.length; i ++) {
			if (allInventories[i] == null)
			{
				if (i < invSizes.length) {
					allInventories[i] = new ItemStack[invSizes[i]];
				}
				else {
					allInventories[i] = new ItemStack[1];
				}
			}
		}
		
		if (numTanks > 0 && tanks == null)
		{
			tanks = new FluidTank[numTanks];
			for (int i = 0; i < numTanks; i ++)
			{
				tanks[i] = new FluidTank(DEFAULT_TANK_CAPACITY);
			}
		}
	}
	
	/**
	 * Clears the inventory.
	 */
	@Override
	public void clear()
	{
		for (int i = 0; i < this.getSizeInventory(); i ++)
			this.setInventorySlotContents(i, null);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		// Inventory
		invSizes = tag.getIntArray(NBTKeys.Inventory.INV_SIZES);
		allInventories = InventoryHelper.readInvArrayFromNBT(tag);
		
		// Load tank if it exists
		if (numTanks > 0)
		{
			NBTHelper.readTanksFromNBT(tanks, tag);
			// null check
			for (int i = 0; i < numTanks; i ++)
			{
				if (tanks[i] == null) tanks[i] = new FluidTank(DEFAULT_TANK_CAPACITY);
				
				tanksUpdated[i] = false;
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		// Inventory
		tag.setIntArray(NBTKeys.Inventory.INV_SIZES, invSizes);
		InventoryHelper.writeInvArrayToNBT(allInventories, tag);
		
		// Save tanks if they exist
		if (numTanks > 0) NBTHelper.writeTanksToNBT(tanks, tag);
	}

	public int getMachineSide(EnumFacing side)
	{
		if (side == frontFacingSide) return FRONT;
		if (side == topFacingSide) return TOP;
		if (side.getOpposite() == frontFacingSide) return BACK;
		if (side.getOpposite() == topFacingSide) return BOTTOM;
		if (topFacingSide == EnumFacing.DOWN)
		{
			if (frontFacingSide == EnumFacing.NORTH)
			{
				if (side == EnumFacing.EAST) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.EAST)
			{
				if (side == EnumFacing.SOUTH) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.SOUTH)
			{
				if (side == EnumFacing.WEST) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.WEST)
			{
				if (side == EnumFacing.NORTH) return LEFT;
				else return RIGHT;
			}
		}
		if (topFacingSide == EnumFacing.UP)
		{
			if (frontFacingSide == EnumFacing.NORTH)
			{
				if (side == EnumFacing.WEST) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.EAST)
			{
				if (side == EnumFacing.NORTH) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.SOUTH)
			{
				if (side == EnumFacing.EAST) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.WEST)
			{
				if (side == EnumFacing.SOUTH) return LEFT;
				else return RIGHT;
			}
		}
		if (topFacingSide == EnumFacing.NORTH)
		{
			if (frontFacingSide == EnumFacing.UP)
			{
				if (side == EnumFacing.WEST) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.WEST)
			{
				if (side == EnumFacing.DOWN) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.DOWN)
			{
				if (side == EnumFacing.EAST) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.EAST)
			{
				if (side == EnumFacing.UP) return LEFT;
				else return RIGHT;
			}
		}
		if (topFacingSide == EnumFacing.SOUTH)
		{
			if (frontFacingSide == EnumFacing.UP)
			{
				if (side == EnumFacing.EAST) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.EAST)
			{
				if (side == EnumFacing.DOWN) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.DOWN)
			{
				if (side == EnumFacing.WEST) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.WEST)
			{
				if (side == EnumFacing.UP) return LEFT;
				else return RIGHT;
			}
		}
		if (topFacingSide == EnumFacing.WEST)
		{
			if (frontFacingSide == EnumFacing.UP)
			{
				if (side == EnumFacing.NORTH) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.NORTH)
			{
				if (side == EnumFacing.DOWN) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.DOWN)
			{
				if (side == EnumFacing.SOUTH) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.SOUTH)
			{
				if (side == EnumFacing.UP) return LEFT;
				else return RIGHT;
			}
		}
		if (topFacingSide == EnumFacing.EAST)
		{
			if (frontFacingSide == EnumFacing.UP)
			{
				if (side == EnumFacing.SOUTH) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.SOUTH)
			{
				if (side == EnumFacing.DOWN) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.DOWN)
			{
				if (side == EnumFacing.NORTH) return LEFT;
				else return RIGHT;
			}
			if (frontFacingSide == EnumFacing.NORTH)
			{
				if (side == EnumFacing.UP) return LEFT;
				else return RIGHT;
			}
		}
		return -1;
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
		return false;
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		return false;
	}
	
	@Override
	public void sendInfo()
	{
		for (int i = 0; i < numTanks; i ++)
		{
			ScienceMod.snw.sendToAll(new TETankMessage(this.pos.getX(), this.pos.getY(), this.pos.getZ(), tanks[i].getFluidAmount(), i));
		}
	}
}
