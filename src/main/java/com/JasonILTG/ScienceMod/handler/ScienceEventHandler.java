package com.JasonILTG.ScienceMod.handler;

import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.JasonILTG.ScienceMod.entity.EntityScience;

/**
 * A general event handler for anything that doesn't belong anywhere else.
 * 
 * @author JasonILTG and syy1125
 */
public class ScienceEventHandler
{
	/** Instance of the handler */
	public static final ScienceEventHandler instance = new ScienceEventHandler();
	
	/**
	 * Intended to remove entities that are not supposed to get influenced by an explosion from the list of entities that are.
	 * 
	 * @param event The explosion event that is taking place.
	 */
	@SubscribeEvent
	public void onExplosionDetonateEvent(ExplosionEvent.Detonate event)
	{
		Iterator<Entity> itr = event.getAffectedEntities().iterator();
		
		while (itr.hasNext())
		{
			Entity ent = itr.next();
			if (!(ent instanceof EntityScience)) continue;
			
			EntityScience entSci = (EntityScience) ent;
			if (!entSci.isPushedByExplosion()) itr.remove();
		}
	}
}
