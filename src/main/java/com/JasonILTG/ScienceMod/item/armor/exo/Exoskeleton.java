package com.JasonILTG.ScienceMod.item.armor.exo;

import java.util.List;
import java.util.Random;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.JasonILTG.ScienceMod.item.armor.ArmorScience;
import com.JasonILTG.ScienceMod.reference.Names;

public class Exoskeleton extends ArmorScience
{
	private int shieldCapacity;
	private int shield;
	
	/**
	 * 0 = Helmet
	 * 1 = Chestplate
	 * 2 = Leggings
	 * 3 = Boots
	 */
	private int armorType;
	
	private static final int DEFAULT_DURABILITY = 2500;
	private static final ArmorProperties DEFAULT_PROPERTIES = new ArmorProperties(0, 0.2, Integer.MAX_VALUE);
	private static final ArmorProperties SHIELD_PROPERTIES = new ArmorProperties(1, 0.25, Integer.MAX_VALUE);
	private static final ArmorProperties UNBLOCKABLE_PROPERTIES = new ArmorProperties(0, 0.1, 10);
	
	private Exoskeleton(String name)
	{
		super(Names.Items.Armor.EXO_PREFIX + name);
		shieldCapacity = 100;
		shield = 0;
		
		maxStackSize = 1;
		this.setMaxDamage(DEFAULT_DURABILITY);
	}
	
	public static Exoskeleton makeHelmet()
	{
		Exoskeleton helmet = new Exoskeleton(Names.Items.Armor.HELMET_NAME);
		helmet.armorType = 0;
		return helmet;
	}
	
	public static Exoskeleton makeChestplate()
	{
		Exoskeleton chest = new Exoskeleton(Names.Items.Armor.CHESTPLATE_NAME);
		chest.armorType = 1;
		return chest;
	}
	
	public static Exoskeleton makeLeggings()
	{
		Exoskeleton legs = new Exoskeleton(Names.Items.Armor.LEGGINGS_NAME);
		legs.armorType = 2;
		return legs;
	}
	
	public static Exoskeleton makeBoots()
	{
		Exoskeleton boots = new Exoskeleton(Names.Items.Armor.BOOTS_NAME);
		boots.armorType = 3;
		return boots;
	}
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot)
	{
		// TODO Auto-generated method stub
		if (source.isUnblockable()) return DEFAULT_PROPERTIES;
		if (shield >= damage / 4) return SHIELD_PROPERTIES;
		return DEFAULT_PROPERTIES;
	}
	
	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot)
	{
		// TODO Auto-generated method stub
		
	}
	
	private void rechargeShield()
	{
		// TODO consume power to recharge
		if (shield < shieldCapacity - 5) {
			shield += 5;
		}
		else {
			shield = shieldCapacity;
		}
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
	{
		// Recharges shield
		rechargeShield();
	}
	
	@Override
	public boolean isValidArmor(ItemStack stack, int type, Entity entity)
	{
		// Player only
		return (entity instanceof EntityPlayer && armorType == type);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
	{
		super.addInformation(stack, playerIn, tooltip, advanced);
		tooltip.add("Shield: " + shield + "/" + shieldCapacity);
	}
	
	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot)
	{
		return 5;
	}
	
	@Override
	public WeightedRandomChestContent getChestGenBase(ChestGenHooks chest, Random rnd, WeightedRandomChestContent original)
	{
		// TODO Auto-generated method stub
		return super.getChestGenBase(chest, rnd, original);
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
	{
		// TODO Auto-generated method stub
		return super.getArmorTexture(stack, entity, slot, type);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
	{
		// TODO Auto-generated method stub
		return super.getArmorModel(entityLiving, itemStack, armorSlot);
	}
	
}
