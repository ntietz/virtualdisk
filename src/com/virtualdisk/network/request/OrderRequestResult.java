package com.virtualdisk.network.request;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class OrderRequestResult
extends RequestResult
{

    public OrderRequestResult(boolean c, boolean s)
    {
        completed = c;
        successful = s;
    }

    public MessageType messageType()
    {
        return MessageType.orderRequestResult;
    }
    
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        
        buffer.writeByte(completed ? 1 : 0);
        buffer.writeByte(successful ? 1 : 0);
        
        return buffer;
    }
    
    public boolean decode(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 2)
        {
            return false;
        }
        else
        {
            completed = (buffer.readByte() == 1) ? true : false;
            successful = (buffer.readByte() == 1) ? true : false;
            return true;
        }
    }
}

