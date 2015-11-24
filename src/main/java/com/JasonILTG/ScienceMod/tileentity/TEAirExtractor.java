package com.JasonILTG.ScienceMod.tileentity;

import net.minecraft.item.ItemStack;

import com.JasonILTG.ScienceMod.crafting.MachineRecipe;
import com.JasonILTG.ScienceMod.crafting.RandomizedItemStack;
import com.JasonILTG.ScienceMod.crafting.RandomOutputGenerator;
import com.JasonILTG.ScienceMod.init.ScienceModItems;
import com.JasonILTG.ScienceMod.tileentity.general.TEMachine;

public class TEAirExtractor extends TEMachine
{
	public static final String NAME = "Air Extractor";
	
	public static final int INVENTORY_SIZE = 28;
	public static final int JAR_INPUT_INDEX = 0;
	public static final int[] OUTPUT_INDEX = new int[INVENTORY_SIZE - 1];
	{
		for (int i = 0; i < OUTPUT_INDEX.length; i++) {
			OUTPUT_INDEX[i] = i + 1;
		}
	}
	
	public static final int DEFAULT_MAX_PROGRESS = 200;
	
	public TEAirExtractor()
	{
		super(NAME, DEFAULT_MAX_PROGRESS, INVENTORY_SIZE, OUTPUT_INDEX);
	}
	
	@Override
	public void craft()
	{	
		
	}
	
	@Override
	public MachineRecipe[] getRecipes()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void consumeInputs(MachineRecipe recipe)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected boolean canCraft(MachineRecipe recipeToUse)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void checkFields()
	{
		// TODO Auto-generated method stub
		
	}
	
	public enum AirExtractorRecipe implements MachineRecipe
	{
		DEFAULT(200, 1, new RandomOutputGenerator.Exclusive(
				new RandomizedItemStack(new ItemStack(ScienceModItems.element, 1, 7), 0.7809),
				new RandomizedItemStack(new ItemStack(ScienceModItems.element, 1, 8), 0.2095),
				new RandomizedItemStack(new ItemStack(ScienceModItems.element, 1, 17), 0.0093)));
		
		private final int reqTime;
		private final int reqJarCount;
		private final RandomOutputGenerator generator;
		
		private AirExtractorRecipe(int requiredTime, int requiredJarCount, RandomOutputGenerator.Exclusive outputGenerator)
		{
			reqTime = requiredTime;
			reqJarCount = requiredJarCount;
			generator = outputGenerator;
		}
		
		@Override
		public boolean canProcessUsing(Object... params)
		{
			if (params == null || params[0] == null) return false;
			ItemStack jarInput = (ItemStack) params[0];
			return jarInput.stackSize >= reqJarCount;
		}
		
		@Override
		public ItemStack[] getItemOutputs()
		{
			return generator.generateOutputs();
		}
		
		@Override
		public int getTimeRequired()
		{
			return reqTime;
		}
		
	}
}