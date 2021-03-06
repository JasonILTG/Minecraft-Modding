package com.JasonILTG.ScienceMod.item.chemistry;

import java.util.List;

import com.JasonILTG.ScienceMod.entity.projectile.ThrownElement;
import com.JasonILTG.ScienceMod.init.ScienceModItems;
import com.JasonILTG.ScienceMod.item.general.ItemJarred;
import com.JasonILTG.ScienceMod.item.metals.EnumDust;
import com.JasonILTG.ScienceMod.reference.NBTKeys;
import com.JasonILTG.ScienceMod.reference.Reference;
import com.JasonILTG.ScienceMod.reference.chemistry.ChemicalEffects;
import com.JasonILTG.ScienceMod.reference.chemistry.basics.EnumElement;
import com.JasonILTG.ScienceMod.reference.chemistry.basics.MatterState;
import com.JasonILTG.ScienceMod.util.EffectHelper;
import com.JasonILTG.ScienceMod.util.MathUtil;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Item that represents an element in a jar.
 * 
 * @author JasonILTG and syy1125
 */
public class ItemElement
		extends ItemJarred
{
	public static final String NAME = "element";
	
	/**
	 * Constructor.
	 */
	public ItemElement()
	{
		super();
		setHasSubtypes(true);
		setUnlocalizedName("element");
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{
		return String.format("item.%s%s.%s", Reference.RESOURCE_PREFIX, NAME,
				EnumElement.VALUES[MathHelper.clamp_int(itemStack.getItemDamage(), 0,
						EnumElement.ELEMENT_COUNT - 1)].getUnlocalizedName());
	}
	
	/**
	 * Adds items with the same ID, but different meta (eg: dye) to a list.
	 * 
	 * @param item The Item to get the subItems of
	 * @param creativeTab The Creative Tab the items belong to
	 * @param list The List of ItemStacks to add to
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> list)
	{
		for (int meta = 0; meta < EnumElement.ELEMENT_COUNT; meta ++)
		{
			list.add(new ItemStack(this, 1, meta));
		}
	}
	
	@Override
	public boolean getHasSubtypes()
	{
		return true;
	}
	
	@Override
	public int getNumSubtypes()
	{
		return EnumElement.ELEMENT_COUNT;
	}
	
	/**
	 * Allows items to add custom lines of information to the mouseover description.
	 * 
	 * @param tooltip All lines to display in the Item's tooltip. This is a List of Strings.
	 * @param advanced Whether the setting "Advanced tooltips" is enabled
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		super.addInformation(stack, playerIn, tooltip, advanced);
		
		tooltip.add("Symbol: " + EnumElement.VALUES[stack.getMetadata()].getElementSymbol());
		tooltip.add("Atomic number: " + (stack.getMetadata() + 1));
		tooltip.add("Current state: " + EnumElement.VALUES[stack.getMetadata()].getElementState());
		
		NBTTagCompound tag = stack.getTagCompound();
		int[] mols = tag == null ? null : tag.getIntArray(NBTKeys.Chemical.MOLS);
		tooltip.add(String.format("MMols: %.2f", mols == null ? 1 : MathUtil.parseFrac(mols)));
	}
	
	@Override
	public boolean isFluid(ItemStack stack)
	{
		return !(EnumElement.VALUES[stack.getMetadata()].getElementState() == MatterState.SOLID);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
	{
		if (isFluid(itemStackIn))
		{
			// Check that the element is fluid
			
			// Consume item if not in creative mode
			if (!playerIn.capabilities.isCreativeMode) itemStackIn.stackSize--;
			
			if (!worldIn.isRemote)
			{
				// Operation done on server
				if (!playerIn.isSneaking() && !worldIn.isRemote)
				{
					// Not sneaking = use on self
					switch (EnumElement.VALUES[itemStackIn.getMetadata()])
					{
						case OXYGEN: {
							EffectHelper.applyEffect(playerIn, ChemicalEffects.Drink.OXYGEN_EFFECTS);
							break;
						}
						case HYDROGEN: {
							EffectHelper.applyEffect(playerIn, ChemicalEffects.Drink.DEFAULT_EFFECTS);
							
							if (playerIn.worldObj.provider.getDimensionId() == -1) { // If in nether
								playerIn.setFire(5);
							}
							
							break;
						}
						default: {
							EffectHelper.applyEffect(playerIn, ChemicalEffects.Drink.DEFAULT_EFFECTS);
							break;
						}
					}
				}
				else {
					// Sneaking = throw
					worldIn.spawnEntityInWorld(new ThrownElement(worldIn, playerIn, itemStackIn.getMetadata()));
				}
			}
		}
		else
		{
			// If the element is solid, check if it can be dumped to make dust
			
			MovingObjectPosition mop = playerIn.rayTrace(200, 1.0F);
			TileEntity lookingAt = worldIn.getTileEntity(mop.getBlockPos());
			if (lookingAt == null || playerIn.isSneaking())
			{
				// If the player isn't looking at a TileEntity, or is sneaking
				
				// Drop the appropriate amount of dust for the appropriate elements
				int meta = itemStackIn.getMetadata();
				int mols = (int) MathUtil.parseFrac(itemStackIn.getTagCompound().getIntArray(NBTKeys.Chemical.MOLS));
				if (meta == EnumElement.COPPER.ordinal()) playerIn.dropItem(new ItemStack(ScienceModItems.dust, mols, EnumDust.COPPER.ordinal()), false, false);
				else if (meta == EnumElement.TIN.ordinal()) playerIn.dropItem(new ItemStack(ScienceModItems.dust, mols, EnumDust.TIN.ordinal()), false, false);
				else if (meta == EnumElement.IRON.ordinal()) playerIn.dropItem(new ItemStack(ScienceModItems.dust, mols, EnumDust.IRON.ordinal()), false, false);
				else if (meta == EnumElement.SILVER.ordinal()) playerIn.dropItem(new ItemStack(ScienceModItems.dust, mols, EnumDust.SILVER.ordinal()), false, false);
				else if (meta == EnumElement.LEAD.ordinal()) playerIn.dropItem(new ItemStack(ScienceModItems.dust, mols, EnumDust.LEAD.ordinal()), false, false);
				else if (meta == EnumElement.GOLD.ordinal()) playerIn.dropItem(new ItemStack(ScienceModItems.dust, mols, EnumDust.GOLD.ordinal()), false, false);
				else if (meta == EnumElement.CHROMIUM.ordinal()) playerIn.dropItem(new ItemStack(ScienceModItems.dust, mols, EnumDust.CHROMIUM.ordinal()), false, false);
				else if (meta == EnumElement.TITANIUM.ordinal()) playerIn.dropItem(new ItemStack(ScienceModItems.dust, mols, EnumDust.TITANIUM.ordinal()), false, false);
				else if (meta == EnumElement.PLATINUM.ordinal()) playerIn.dropItem(new ItemStack(ScienceModItems.dust, mols, EnumDust.PLATINUM.ordinal()), false, false);
				else return itemStackIn; // If none match, return
				
				// Consume item if not in creative mode
				if (!playerIn.capabilities.isCreativeMode) itemStackIn.stackSize--;
				
				// Drop jar
				playerIn.dropItem(new ItemStack(ScienceModItems.jar), false, false);
			}
		}
		
		if (itemStackIn.stackSize == 0) return null;
		return itemStackIn;
	}
	
	/**
	 * Makes an <code>ItemStack</code> with the specified size, metadata, and number of mols (as an int array).
	 * 
	 * @param size The size of the stack
	 * @param meta The metadata
	 * @param mols The number of mols
	 * @return The <code>ItemStack</code>
	 */
	public static ItemStack getElementStack(int size, int meta, int[] mols)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setIntArray(NBTKeys.Chemical.MOLS, mols);
		ItemStack stack = new ItemStack(ScienceModItems.element, size, meta);
		stack.setTagCompound(tag);
		return stack;
	}
	
	/**
	 * Makes an <code>ItemStack</code> with the specified size, metadata, and number of mols (as a double).
	 * 
	 * @param size The size of the stack
	 * @param meta The metadata
	 * @param mols The number of mols
	 * @return The <code>ItemStack</code>
	 */
	public static ItemStack getElementStack(int size, int meta, double mols)
	{
		return getElementStack(size, meta, MathUtil.parseFrac(mols));
	}
	
	/**
	 * Makes an <code>ItemStack</code> with the specified metadata and number of mols (as an int array).
	 * 
	 * @param meta The metadata
	 * @param mols The number of mols
	 * @return The <code>ItemStack</code>
	 */
	public static ItemStack getElementStack(int meta, int[] mols)
	{
		return getElementStack(1, meta, mols);
	}
	
	/**
	 * Makes an <code>ItemStack</code> with the specified metadata and number of mols (as a double).
	 * 
	 * @param meta The metadata
	 * @param mols The number of mols
	 * @return The <code>ItemStack</code>
	 */
	public static ItemStack getElementStack(int meta, double mols)
	{
		return getElementStack(1, meta, MathUtil.parseFrac(mols));
	}
	
	// TODO For whatever reason, onItemUseFinish is not working.
	/*
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn)
	{
		super.onItemUseFinish(stack, worldIn, playerIn);
		
		if (!playerIn.capabilities.isCreativeMode) stack.splitStack(1);
		
		new Throwable().printStackTrace(System.out);
		
		if (!worldIn.isRemote) { // If the world is server-side - still haven't figured why exactly this is - apply potion effects.
			switch (ChemElement.values()[stack.getMetadata()])
			{
				case OXYGEN: {
					LogHelper.info("Oxygen effect activated.");
					playerIn.addPotionEffect(new PotionEffect(ChemEffect.Special.OXYGEN_EFFECT));
					break;
				}
				default: {
					LogHelper.info("Default effect activated.");
					playerIn.addPotionEffect(new PotionEffect(ChemEffect.DEFAULT_EFFECT));
					break;
				}
			}
		}
		
		return stack;
	}
	*/
}
