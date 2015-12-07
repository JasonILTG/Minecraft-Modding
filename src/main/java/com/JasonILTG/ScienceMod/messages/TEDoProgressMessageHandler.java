package com.JasonILTG.ScienceMod.messages;

import com.JasonILTG.ScienceMod.tileentity.machines.TEMachine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Message handler for TEDoProgressMessages.
 * 
 * @author JasonILTG and syy1125
 */
public class TEDoProgressMessageHandler implements IMessageHandler<TEDoProgressMessage, IMessage>
{
    @Override
    public IMessage onMessage(final TEDoProgressMessage message, MessageContext ctx)
    {
    	Minecraft minecraft = Minecraft.getMinecraft();
        final WorldClient worldClient = minecraft.theWorld;
        if (worldClient == null) return null;
    	minecraft.addScheduledTask(new Runnable() {
            @Override
            public void run() {
            	processMessage(worldClient, message);
            }
        });
        return null;
    }
    
    /**
     * Process the message
     * 
     * @param worldClient The world to process the message with
     * @param message The message
     */
    void processMessage(WorldClient worldClient, TEDoProgressMessage message)
    {
    	int x = message.getTEX();
        int y = message.getTEY();
        int z = message.getTEZ();
        TEMachine te = (TEMachine) worldClient.getTileEntity(new BlockPos(x, y, z));
        if (te == null) return;
        
        te.setDoProgress(message.getDoProgress());
    }
}