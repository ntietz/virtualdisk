package com.virtualdisk.network.request;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.util.Date;

public class OrderRequest
extends Request
{
    private int volumeId;
    private int logicalOffset;
    private Date timestamp;

    public OrderRequest(int v, int l, Date t)
    {
        volumeId = v;
        logicalOffset = l;
        timestamp = t;
    }

    public MessageType messageType()
    {
        return MessageType.orderRequest;
    }

    public int getVolumeId()
    {
        return volumeId;
    }

    public int getLogicalOffset()
    {
        return logicalOffset;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }
    
    public ChannelBuffer encode()
    {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

        buffer.writeInt(volumeId);
        buffer.writeInt(logicalOffset);
        buffer.writeLong(timestamp.getTime());
        
        return buffer;
    }
    
    public boolean decode(ChannelBuffer buffer)
    {
        if (buffer.readableBytes() < 16)
        {
            return false;
        }
        else
        {
            volumeId = buffer.readInt();
            logicalOffset = buffer.readInt();
            timestamp = new Date(buffer.readLong());
            return true;
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + logicalOffset;
        result = prime * result
                + ((timestamp == null) ? 0 : timestamp.hashCode());
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
        if (!(obj instanceof OrderRequest))
        {
            return false;
        }
        OrderRequest other = (OrderRequest) obj;
        if (logicalOffset != other.logicalOffset)
        {
            return false;
        }
        if (timestamp == null)
        {
            if (other.timestamp != null)
            {
                return false;
            }
        } else if (!timestamp.equals(other.timestamp))
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

