package com.JasonILTG.ScienceMod.handler.renderer;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.JasonILTG.ScienceMod.entity.projectile.ThrownElement;
import com.JasonILTG.ScienceMod.init.ScienceModItems;

@SideOnly(Side.CLIENT)
/**
 * Renderer for the thrown chemical entities.
 * @author JasonILTG and syy1125
 */
public class ProjectileScienceRenderer extends RenderSnowball
{
	private static final String __OBFID = "CL_00001008";
	
	public ProjectileScienceRenderer(RenderManager renderManager, Item itemIn, RenderItem renderIn)
	{
		super(renderManager, itemIn, renderIn);
	}
	
	@Override
	/**
	 * Gets the model for the entity.
	 */
	public ItemStack func_177082_d(Entity p_177082_1_)
	{
		if (p_177082_1_ instanceof ThrownElement) return new ItemStack(ScienceModItems.jar, 1);
		
		return new ItemStack(this.field_177084_a, 1, 0);
	}
	
}
