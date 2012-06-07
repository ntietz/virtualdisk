package com.virtualdisk.network.request;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class ReadRequest
extends Request
{
    private int volumeId;
    private long logicalOffset;

    public ReadRequest(int v, long l)
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

    public long getLogicalOffset()
    {
        return logicalOffset;
    }
    
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        
        buffer.writeInt(volumeId);
        buffer.writeLong(logicalOffset);
        
        return buffer;
    }
    
    public boolean decode(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 12) // TODO fix this
        {
            return false;
        }
        else
        {
            volumeId = buffer.readInt();
            logicalOffset = buffer.readLong();
            return true;
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + (int) (logicalOffset ^ (logicalOffset >>> 32));
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

