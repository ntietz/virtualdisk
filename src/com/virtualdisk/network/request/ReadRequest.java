package com.virtualdisk.network.request;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class ReadRequest
extends Request
{
    private int volumeId;
    private int logicalOffset;

    public ReadRequest(int v, int l)
    {
        volumeId = v;
        logicalOffset = l;
    }

    public MessageType messageType()
    {
        return MessageType.readRequest;
    }

    public int getVolumeId()
    {
        return volumeId;
    }

    public int getLogicalOffset()
    {
        return logicalOffset;
    }
    
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        
        buffer.writeInt(volumeId);
        buffer.writeInt(logicalOffset);
        
        return buffer;
    }
    
    public boolean decode(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 8)
        {
            return false;
        }
        else
        {
            volumeId = buffer.readInt();
            logicalOffset = buffer.readInt();
            return true;
        }
    }
}

