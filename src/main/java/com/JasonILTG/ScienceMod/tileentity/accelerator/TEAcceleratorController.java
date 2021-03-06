package com.JasonILTG.ScienceMod.tileentity.accelerator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

import com.JasonILTG.ScienceMod.item.chemistry.ItemElement;
import com.JasonILTG.ScienceMod.manager.ITileManager;
import com.JasonILTG.ScienceMod.manager.Manager;
import com.JasonILTG.ScienceMod.manager.power.IPowered;
import com.JasonILTG.ScienceMod.manager.power.PowerManager;
import com.JasonILTG.ScienceMod.manager.power.TilePowerManager;
import com.JasonILTG.ScienceMod.reference.Reference;
import com.JasonILTG.ScienceMod.tileentity.general.ITileEntityPowered;
import com.JasonILTG.ScienceMod.tileentity.machines.TEMachine;
import com.JasonILTG.ScienceMod.util.InventoryHelper;

public class TEAcceleratorController
		extends TEAccelerator
		implements ITileEntityPowered, IInventory, ITickable
{
	private static final String NAME = NAME_PREFIX + "Accelerator Controller";
	
	private ItemStack[] inputInv;
	private static final int INPUT_INDEX = 0;
	
	public TEAcceleratorController()
	{
		super();
		
		manager = new AcceleratorManager();
		
		inputInv = new ItemStack[1];
	}
	
	@Override
	public void update()
	{
		// Now in manager
	}
	
	public void onRightClick(EntityPlayer player, ItemStack stack)
	{
		if (stack != null && stack.getItem() instanceof ItemElement)
		{
			// Store 1 item and if there is already storage present, give the storage to the player.
			ItemStack storage = inputInv[0];
			inputInv[0] = stack.splitStack(1);
			
			if (storage != null) {
				InventoryHelper.tryGiveItem(worldObj, pos, player, stack);
			}
		}
		else
		{
			// Update the structure, and tries to activate.
			updateStructure();
			tryActivate();
		}
	}
	
	private void updateStructure()
	{
		manager.refreshStructure();
	}
	
	private void tryActivate()
	{
		if (!manager.formed) return;
		ItemStack input = getStackInSlot(INPUT_INDEX);
		
		if (input.getItem() instanceof ItemElement)
		{
			// Consume the item, activate.
			this.decrStackSize(INPUT_INDEX, 1);
			manager.activate();
		}
	}
	
	@Override
	public PowerManager getPowerManager()
	{
		return manager.power;
	}
	
	@Override
	public boolean hasPower()
	{
		return getPowerManager().getCurrentPower() >= manager.powerPerTick;
	}
	
	@Override
	public float getPowerCapacity()
	{
		return getPowerManager().getCapacity();
	}
	
	@Override
	public float getCurrentPower()
	{
		return getPowerManager().getCurrentPower();
	}
	
	@Override
	public void setCurrentPower(float amount)
	{
		getPowerManager().setCurrentPower(amount);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		getPowerManager().readFromNBT(compound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		getPowerManager().writeToNBT(compound);
	}
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		if (index < 0 || index > getSizeInventory() - 1) return null;
		return inputInv[index];
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
		if (index < 0 || index > getSizeInventory() - 1) return;
		inputInv[index] = stack;
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return Reference.DEFAULT_STACK_LIMIT;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getTileEntity(this.getPos()) == this && player.getDistanceSq(this.pos.add(0.5, 0.5, 0.5)) <= 64;
	}
	
	@Override
	public void openInventory(EntityPlayer player)
	{
		// Empty method
	}
	
	@Override
	public void closeInventory(EntityPlayer player)
	{
		// Empty method
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		// Cannot insert item.
		return false;
	}
	
	@Override
	public int getField(int id)
	{
		return 0;
	}
	
	@Override
	public void setField(int id, int value)
	{
		// Empty method
	}
	
	@Override
	public int getFieldCount()
	{
		return 0;
	}
	
	@Override
	public void clear()
	{
		for (int i = 0; i < this.getSizeInventory(); i ++)
			this.setInventorySlotContents(i, null);
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
	
	public class AcceleratorManager
			extends Manager
			implements ITileManager, IPowered
	{
		private static final String NBT_KEY = "AcceleratorManager";
		
		private static final int DEFAULT_POWER_DRAIN = 100;
		private static final int MAX_CHARGE_TIME = 200;
		private static final int DEFAULT_POWER_IN = DEFAULT_POWER_DRAIN * 5;
		private static final int DEFAULT_POWER_OUT = 0;
		
		private Set<TEAccelerator> blocks;
		
		private TilePowerManager power;
		private int powerPerTick;
		private TEAcceleratorOutput linkedOutput;
		
		/** The maximum amount of charging time for the accelerator to fire */
		private int maxCharge;
		/** The current amount of charge of the accelerator */
		private int currentCharge;
		/** Whether the accelerator is currently charging */
		private boolean charging;
		/** Whether the accelerator is formed */
		private boolean formed;
		
		public AcceleratorManager()
		{
			blocks = new HashSet<TEAccelerator>();
			
			maxCharge = MAX_CHARGE_TIME;
			currentCharge = 0;
			charging = false;
			formed = false;
			
			power = new TilePowerManager(worldObj, pos, TEMachine.DEFAULT_POWER_CAPACITY, DEFAULT_POWER_IN, DEFAULT_POWER_OUT,
					TilePowerManager.MACHINE);
			powerPerTick = DEFAULT_POWER_DRAIN;
		}
		
		/**
		 * Searches for all the attached blocks that are accelerator components.
		 */
		private void searchForAttachedBlocks()
		{
			// Clear the current structure.
			for (TEAccelerator acc : blocks)
			{
				acc.manager = null;
			}
			blocks.clear();
			
			// Initiate the search
			Queue<TEAccelerator> attachQueue = new LinkedList<TEAccelerator>();
			attachQueue.add(TEAcceleratorController.this);
			
			// Keep searching until the queue is done.
			while (attachQueue.size() > 0)
			{
				// Attach the block
				TEAccelerator acceleratorBlock = attachQueue.poll();
				BlockPos acceleratorPos = acceleratorBlock.getPos();
				blocks.add(acceleratorBlock);
				acceleratorBlock.manager = this;
				
				// Search for adjacent blocks
				for (EnumFacing facing : EnumFacing.VALUES)
				{
					TileEntity te = worldObj.getTileEntity(acceleratorPos.offset(facing));
					if (te instanceof TEAccelerator && !attachQueue.contains(te) && !blocks.contains(te))
					{
						attachQueue.add((TEAccelerator) te);
					}
				}
			}
		}
		
		private void refreshStructure()
		{
			Minecraft.getMinecraft().addScheduledTask(
					new Callable<Object>()
					{
						public Object call() throws Exception
						{
							searchForAttachedBlocks();
							
							boolean hasOutput = false;
							
							for (TEAccelerator acc : blocks)
							{
								if (acc instanceof TEAcceleratorOutput)
								{
									if (hasOutput) {
										dismantle();
										return null;
									}
									else {
										linkedOutput = (TEAcceleratorOutput) acc;
									}
								}
							}
							
							return null;
						}
					}
					);
		}
		
		public void activate()
		{
			charging = true;
		}
		
		public void deactivate()
		{
			charging = false;
		}
		
		public void form(TEAcceleratorOutput output)
		{
			formed = true;
			linkedOutput = output;
		}
		
		public void dismantle()
		{
			formed = false;
			linkedOutput = null;
		}
		
		@Override
		public PowerManager getPowerManager()
		{
			return power;
		}
		
		@Override
		public void refreshFields()
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onTickStart()
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onTickEnd()
		{
			// Do action only when formed and has power.
			if (!formed || !hasPower()) return;
			
			if (charging)
			{
				power.consumePower(DEFAULT_POWER_DRAIN);
				currentCharge ++;
				
				if (currentCharge >= maxCharge)
				{
					currentCharge = 0;
					deactivate();
					// Send message to the output block.
					linkedOutput.receiveItem((ItemElement) inputInv[0].getItem(), inputInv[0].getMetadata());
				}
			}
			else {
				
			}
		}
		
		@Override
		public void readFromDataTag(NBTTagCompound dataTag)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public NBTTagCompound makeDataTag()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void updateWorldInfo(World worldIn, BlockPos pos)
		{
			// TODO Auto-generated method stub
			
		}
		
	}
}