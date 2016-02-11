package com.JasonILTG.ScienceMod.handler.renderer;

import com.JasonILTG.ScienceMod.entity.projectile.ProjectileScience;
import com.JasonILTG.ScienceMod.init.ScienceModItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class ProjectileScienceRenderFactory
		implements IRenderFactory<ProjectileScience>
{
	@Override
	public Render<? super ProjectileScience> createRenderFor(RenderManager manager)
	{
		return new ProjectileScienceRenderer(manager, ScienceModItems.jar, Minecraft.getMinecraft().getRenderItem());
	}
}
