package com.JasonILTG.ScienceMod.messages;

import com.JasonILTG.ScienceMod.tileentity.general.TEMachine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TEMaxProgressMessageHandler implements IMessageHandler<TEMaxProgressMessage, IMessage>
{
    @Override
    public IMessage onMessage(final TEMaxProgressMessage message, MessageContext ctx)
    {
    	Minecraft minecraft = Minecraft.getMinecraft();
        final WorldClient worldClient = minecraft.theWorld;
    	minecraft.addScheduledTask(new Runnable() {
            @Override
            public void run() {
            	processMessage(worldClient, message);
            }
        });
        return null;
    }
    
    void processMessage(WorldClient worldClient, TEMaxProgressMessage message)
    {
    	int x = message.getTEX();
        int y = message.getTEY();
        int z = message.getTEZ();
        TEMachine te = (TEMachine) worldClient.getTileEntity(new BlockPos(x, y, z));
        if (te == null) return;
        
        te.setMaxProgress(message.getMaxProgress());
    }
}