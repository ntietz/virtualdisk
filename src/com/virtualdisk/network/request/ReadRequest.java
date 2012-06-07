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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + logicalOffset;
        result = prime * result + volumeId;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof ReadRequest))
        {
            return false;
        }
        ReadRequest other = (ReadRequest) obj;
        if (logicalOffset != other.logicalOffset)
        {
            return false;
        }
        if (volumeId != other.volumeId)
        {
            return false;
        }
        return true;
    }
}

