package com.JasonILTG.ScienceMod.messages;

import com.JasonILTG.ScienceMod.util.LogHelper;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class TEInfoRequestMessage implements IMessage
{
    public int x;
    public int y;
    public int z;
    
    public TEInfoRequestMessage()
    {
    	
    }

    public TEInfoRequestMessage(int x, int y, int z)
    { 
        this.x = x;
        this.y = y;
        this.z = z;
        LogHelper.info("Info request created.");
    }
    
    public int getTEX()
    {
    	return x;
    }
    
    public int getTEY()
    {
    	return y;
    }
    
    public int getTEZ()
    {
    	return z;
    }

    @Override
    public void toBytes(ByteBuf buf)
    { 
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    { 
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }
}